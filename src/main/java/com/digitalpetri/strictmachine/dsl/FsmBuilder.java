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

package com.digitalpetri.strictmachine.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import com.digitalpetri.strictmachine.Fsm;
import com.digitalpetri.strictmachine.StrictMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FsmBuilder<S extends Enum<S>, E> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final List<Transition<S, E>> transitions = new ArrayList<>();

    private final List<TransitionAction<S, E>> transitionActions = new ArrayList<>();

    private ActionProxy<S, E> actionProxy = null;

    private final Logger logger;
    private final Map<String, String> mdc;
    private final Executor executor;

    public FsmBuilder() {
        this(EXECUTOR_SERVICE, StrictMachine.class.getName());
    }

    public FsmBuilder(Executor executor, String loggerName) {
        this(executor, loggerName, Collections.emptyMap());
    }

    public FsmBuilder(Executor executor, String loggerName, Map<String, String> mdc) {
        this.executor = executor;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.mdc = mdc;
    }

    /**
     * Start defining a {@link Transition} from state {@code state}.
     *
     * @param state the state the transition begins in.
     * @return a {@link TransitionBuilder}.
     */
    public TransitionBuilder<S, E> when(S state) {
        return new TransitionBuilder<>(state, transitions, transitionActions);
    }

    /**
     * Start defining an {@link Action} that will be executed after an internal transition from/to {@code state}.
     * <p>
     * The criteria for the event that causes this transition is defined on the returned {@link ViaBuilder}.
     *
     * @param state the state experiencing an internal transition.
     * @return a {@link ViaBuilder}.
     */
    public ViaBuilder<S, E> onInternalTransition(S state) {
        return onTransitionFrom(state).to(state);
    }

    /**
     * Start defining an {@link Action} that will executed after a transition to {@code state}.
     * <p>
     * Further criteria for execution will be defined on the returned {@link ActionToBuilder}.
     *
     * @param state the state being transitioned to.
     * @return an {@link ActionToBuilder}.
     */
    public ActionToBuilder<S, E> onTransitionTo(S state) {
        return onTransitionTo(s -> Objects.equals(s, state));
    }

    /**
     * Start defining an {@link Action} that will execute after a transition to any state that passes {@code filter}.
     * <p>
     * Further criteria for execution will be defined on the returned {@link ActionToBuilder}.
     *
     * @param filter the filter for states being transitioned to.
     * @return an {@link ActionToBuilder}.
     */
    public ActionToBuilder<S, E> onTransitionTo(Predicate<S> filter) {
        return new ActionToBuilder<>(filter, transitionActions);
    }

    /**
     * Start defining an {@link Action} that will execute after a transition from {@code state}.
     *
     * @param state the state being transitioned from.
     * @return an {@link ActionFromBuilder}.
     */
    public ActionFromBuilder<S, E> onTransitionFrom(S state) {
        return onTransitionFrom(s -> Objects.equals(s, state));
    }

    /**
     * Start defining an {@link Action} that will execute after a transition from any state that passes {@code filter}.
     *
     * @param filter the filter for states being transitioned from.
     * @return an {@link ActionFromBuilder}.
     */
    public ActionFromBuilder<S, E> onTransitionFrom(Predicate<S> filter) {
        return new ActionFromBuilder<>(filter, transitionActions);
    }

    /**
     * Add a manually defined {@link Transition}.
     *
     * @param transition the {@link Transition} to add.
     */
    public void addTransition(Transition<S, E> transition) {
        transitions.add(transition);
    }

    /**
     * Add a manually defined {@link TransitionAction}.
     *
     * @param transitionAction the {@link TransitionAction} to add.
     */
    public void addTransitionAction(TransitionAction<S, E> transitionAction) {
        transitionActions.add(transitionAction);
    }

    /**
     * Configure an {@link ActionProxy} for the {@link Fsm} instance being built.
     *
     * @param actionProxy an {@link ActionProxy} for the {@link Fsm} instance being built.
     */
    public void setActionProxy(ActionProxy<S, E> actionProxy) {
        this.actionProxy = actionProxy;
    }

    public Fsm<S, E> build(S initialState) {
        return new StrictMachine<>(
            logger,
            mdc,
            executor,
            actionProxy,
            initialState,
            new ArrayList<>(transitions),
            new ArrayList<>(transitionActions)
        );
    }

}
