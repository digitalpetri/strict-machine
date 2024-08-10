/*
 * Copyright 2018 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpetri.strictmachine;

import com.digitalpetri.strictmachine.FsmLogging.Level;
import com.digitalpetri.strictmachine.dsl.ActionContext;
import com.digitalpetri.strictmachine.dsl.ActionProxy;
import com.digitalpetri.strictmachine.dsl.Transition;
import com.digitalpetri.strictmachine.dsl.TransitionAction;
import com.digitalpetri.strictmachine.internal.Log;
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

public class StrictMachine<S, E> implements Fsm<S, E> {

  private static final AtomicLong INSTANCE_ID = new AtomicLong(0);

  private final long instanceId = INSTANCE_ID.getAndIncrement();

  private volatile boolean pollExecuted = false;
  private final Object queueLock = new Object();
  private final ArrayDeque<PendingEvent> eventQueue = new ArrayDeque<>();
  private final ArrayDeque<PendingEvent> eventShelf = new ArrayDeque<>();

  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
  private final Map<FsmContext.Key<?>, Object> contextValues = new ConcurrentHashMap<>();
  private final AtomicReference<S> state = new AtomicReference<>();

  private final Executor executor;
  private final ActionProxy<S, E> actionProxy;
  private final List<Transition<S, E>> transitions;
  private final List<TransitionAction<S, E>> transitionActions;

  public StrictMachine(
      Executor executor,
      ActionProxy<S, E> actionProxy,
      S initialState,
      List<Transition<S, E>> transitions,
      List<TransitionAction<S, E>> transitionActions
  ) {

    this.executor = executor;
    this.actionProxy = actionProxy;
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

      maybeExecutePoll();
    }
  }

  @Override
  public S fireEventBlocking(E event) throws InterruptedException {
    var transferQueue = new LinkedTransferQueue<S>();

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

  @Override
  public void withContext(Consumer<FsmContext<S, E>> contextConsumer) {
    try {
      readWriteLock.writeLock().lock();

      contextConsumer.accept(new FsmContextImpl());
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  private void maybeExecutePoll() {
    synchronized (queueLock) {
      if (!pollExecuted && !eventQueue.isEmpty()) {
        executor.execute(new PollAndEvaluate());
        pollExecuted = true;
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

        if (pending == null) {
          return;
        }
      }

      E event = pending.event;

      try {
        readWriteLock.writeLock().lock();

        S currState = state.get();
        S nextState = currState;

        var ctx = new FsmContextImpl();

        for (Transition<S, E> transition : transitions) {
          if (transition.matches(ctx, currState, event)) {
            nextState = transition.target();
            break;
          }
        }

        state.set(nextState);

        if (Log.isLevelEnabled(Level.DEBUG)) {
          Log.debug(
              instanceId,
              "%s x %s = %s",
              padRight(String.format("S(%s)", currState)),
              padRight(String.format("E(%s)", event)),
              padRight(String.format("S'(%s)", nextState))
          );
        }

        var actionContext = new ActionContextImpl(
            currState,
            nextState,
            event
        );

        var matchingActions = new ArrayList<TransitionAction<S, E>>();

        for (TransitionAction<S, E> transitionAction : transitionActions) {
          if (transitionAction.matches(currState, nextState, event)) {
            matchingActions.add(transitionAction);
          }
        }

        Log.trace(instanceId, "found %d matching TransitionActions", matchingActions.size());

        matchingActions.forEach(transitionAction -> {
          try {
            if (actionProxy == null) {
              Log.trace(instanceId, "executing TransitionAction: %s", transitionAction);

              transitionAction.execute(actionContext);
            } else {
              Log.trace(
                  instanceId,
                  "executing (via proxy) TransitionAction: %s", transitionAction
              );

              actionProxy.execute(actionContext, transitionAction::execute);
            }
          } catch (Throwable ex) {
            Log.warn(
                instanceId,
                "Uncaught Throwable executing TransitionAction: %s\n%s", transitionAction, ex
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
        if (eventQueue.isEmpty()) {
          pollExecuted = false;
        } else {
          // pollExecuted remains true
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

          maybeExecutePoll();
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
