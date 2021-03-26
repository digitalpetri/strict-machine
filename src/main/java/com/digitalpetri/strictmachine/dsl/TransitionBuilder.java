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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class TransitionBuilder<S extends Enum<S>, E> {

    private final S from;
    private final List<Transition<S, E>> transitions;
    private final LinkedList<TransitionAction<S, E>> transitionActions;

    TransitionBuilder(
        S from,
        List<Transition<S, E>> transitions,
        LinkedList<TransitionAction<S, E>> transitionActions
    ) {

        this.from = from;
        this.transitions = transitions;
        this.transitionActions = transitionActions;
    }

    /**
     * Continue defining a {@link Transition} that is triggered by {@code event}.
     *
     * @param event the event that triggers this transition.
     * @return a {@link TransitionTo}.
     */
    public TransitionTo<S, E> on(E event) {
        return to -> {
            PredicatedTransition<S, E> transition =
                Transitions.fromInstanceViaInstance(from, event, to);

            transitions.add(transition);

            return new GuardBuilder<>(transition, transitionActions);
        };
    }

    /**
     * Continue defining a {@link Transition} that is triggered by an event of type {@code eventClass}.
     *
     * @param eventClass the †ype of event that triggers this transition.
     * @return a {@link TransitionTo}.
     */
    public TransitionTo<S, E> on(Class<? extends E> eventClass) {
        return to -> {
            PredicatedTransition<S, E> transition =
                Transitions.fromInstanceViaClass(from, eventClass, to);

            transitions.add(transition);

            return new GuardBuilder<>(transition, transitionActions);
        };
    }

    /**
     * Continue defining a {@link Transition} that is triggered by any event that passes {@code eventFilter}.
     *
     * @param eventFilter the filter for events that trigger this transition.
     * @return a {@link TransitionTo}.
     */
    public TransitionTo<S, E> on(Predicate<E> eventFilter) {
        return to -> {
            PredicatedTransition<S, E> transition =
                Transitions.fromInstanceViaDynamic(from, eventFilter, to);

            transitions.add(transition);

            return new GuardBuilder<>(transition, transitionActions);
        };
    }

    /**
     * Continue defining a {@link Transition} that is triggered by any event.
     *
     * @return a {@link TransitionTo}.
     */
    public TransitionTo<S, E> onAny() {
        return on(e -> true);
    }

    public interface TransitionTo<S, E> {

        GuardBuilder<S, E> transitionTo(S state);

    }

}
