package com.digitalpetri.strictmachine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import com.digitalpetri.strictmachine.dsl.ActionContext;
import com.digitalpetri.strictmachine.dsl.Transition;
import com.digitalpetri.strictmachine.dsl.TransitionAction;
import org.slf4j.Logger;

public class StrictMachine<S, E> implements Fsm<S, E> {

    private static final AtomicLong INSTANCE_ID = new AtomicLong(0);

    private final long instanceId = INSTANCE_ID.getAndIncrement();

    private volatile boolean paused = false;
    private volatile boolean pollSubmitted = false;
    private final Object queueLock = new Object();
    private final ArrayDeque<PendingEvent> eventQueue = new ArrayDeque<>();
    private final ArrayDeque<PendingEvent> eventShelf = new ArrayDeque<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Map<FsmContext.Key<?>, Object> contextValues = new ConcurrentHashMap<>();
    private final AtomicReference<S> state = new AtomicReference<>();

    private final Logger logger;
    private final Executor executor;
    private final List<Transition<S, E>> transitions;
    private final List<TransitionAction<S, E>> transitionActions;


    public StrictMachine(
        Logger logger,
        Executor executor,
        S initialState,
        List<Transition<S, E>> transitions,
        List<TransitionAction<S, E>> transitionActions) {

        this.logger = logger;
        this.executor = executor;
        this.transitions = transitions;
        this.transitionActions = transitionActions;

        state.set(initialState);
    }

    @Override
    public S getState() {
        try {
            readWriteLock.readLock().lock();

            return state.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void fireEvent(E event) {
        fireEvent(event, null);
    }

    @Override
    public void fireEvent(E event, Consumer<S> stateConsumer) {
        synchronized (queueLock) {
            eventQueue.add(new PendingEvent(event, stateConsumer));

            maybeSubmitPoll();
        }
    }

    @Override
    public S fireEventBlocking(E event) throws InterruptedException {
        LinkedTransferQueue<S> transferQueue = new LinkedTransferQueue<>();

        fireEvent(event, transferQueue::put);

        return transferQueue.take();
    }

    @Override
    public <T> T getFromContext(Function<FsmContext<S, E>, T> get) {
        try {
            readWriteLock.writeLock().lock();

            return get.apply(new FsmContextImpl());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void maybeSubmitPoll() {
        synchronized (queueLock) {
            if (!pollSubmitted && !paused && !eventQueue.isEmpty()) {
                executor.execute(new PollAndEvaluate());
                pollSubmitted = true;
            }
        }
    }

    private class PendingEvent {
        final E event;
        final Consumer<S> stateConsumer;

        PendingEvent(E event, Consumer<S> stateConsumer) {
            this.event = event;
            this.stateConsumer = stateConsumer;
        }
    }

    private class PollAndEvaluate implements Runnable {
        @Override
        public void run() {
            PendingEvent pending;

            synchronized (queueLock) {
                pending = eventQueue.poll();

                if (pending == null) return;
            }

            E event = pending.event;

            try {
                readWriteLock.writeLock().lock();

                S currState = state.get();
                S nextState = currState;

                FsmContextImpl ctx = new FsmContextImpl();

                for (Transition<S, E> transition : transitions) {
                    if (transition.matches(ctx, currState, event)) {
                        nextState = transition.target();
                        break;
                    }
                }

                state.set(nextState);

                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "[{}] {} x {} = {}",
                        instanceId,
                        padRight(String.format("S(%s)", currState)),
                        padRight(String.format("E(%s)", event)),
                        padRight(String.format("S'(%s)", nextState))
                    );
                }

                ActionContextImpl actionContext = new ActionContextImpl(
                    currState,
                    nextState,
                    event
                );

                List<TransitionAction<S, E>> matchingActions = new ArrayList<>();

                for (TransitionAction<S, E> transitionAction : transitionActions) {
                    if (transitionAction.matches(currState, nextState, event)) {
                        matchingActions.add(transitionAction);
                    }
                }

                if (logger.isTraceEnabled()) {
                    logger.trace(
                        "[{}] found {} matching TransitionActions",
                        instanceId, matchingActions.size()
                    );
                }

                matchingActions.forEach(transitionAction -> {
                    try {
                        if (logger.isTraceEnabled()) {
                            logger.trace(
                                "[{}] executing TransitionAction: {}",
                                instanceId, transitionAction
                            );
                        }

                        transitionAction.execute(actionContext);
                    } catch (Throwable ex) {
                        logger.warn(
                            "[{}] Uncaught Throwable executing TransitionAction: {}",
                            instanceId, transitionAction, ex
                        );
                    }
                });

            } finally {
                readWriteLock.writeLock().unlock();
            }

            if (pending.stateConsumer != null) {
                pending.stateConsumer.accept(state.get());
            }

            synchronized (queueLock) {
                if (eventQueue.isEmpty() || paused) {
                    pollSubmitted = false;
                } else {
                    // pollSubmitted remains true
                    executor.execute(new PollAndEvaluate());
                }
            }
        }
    }

    private static final int PADDING = 24;

    private static String padRight(String s) {
        return String.format("%1$-" + PADDING + "s", s);
    }

    private class FsmContextImpl implements FsmContext<S, E> {

        @Override
        public S currentState() {
            return getState();
        }

        @Override
        public void fireEvent(E event) {
            StrictMachine.this.fireEvent(event);
        }

        @Override
        public void shelveEvent(E event) {
            try {
                readWriteLock.writeLock().lock();

                eventShelf.add(new PendingEvent(event, s -> {}));
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        @Override
        public void processShelvedEvents() {
            try {
                readWriteLock.writeLock().lock();

                synchronized (queueLock) {
                    while (!eventShelf.isEmpty()) {
                        eventQueue.addFirst(eventShelf.removeLast());
                    }

                    maybeSubmitPoll();
                }
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        @Override
        public Object get(Key<?> key) {
            try {
                readWriteLock.readLock().lock();

                return contextValues.get(key);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }

        @Override
        public Object remove(Key<?> key) {
            try {
                readWriteLock.writeLock().lock();

                return contextValues.remove(key);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        @Override
        public void set(Key<?> key, Object value) {
            try {
                readWriteLock.writeLock().lock();

                contextValues.put(key, value);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        @Override
        public long getInstanceId() {
            return instanceId;
        }

    }

    private class ActionContextImpl extends FsmContextImpl implements ActionContext<S, E> {

        private final S from;
        private final S to;
        private final E event;

        ActionContextImpl(S from, S to, E event) {
            this.from = from;
            this.to = to;
            this.event = event;
        }

        @Override
        public S from() {
            return from;
        }

        @Override
        public S to() {
            return to;
        }

        @Override
        public E event() {
            return event;
        }

    }

}
