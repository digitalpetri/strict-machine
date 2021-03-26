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
import java.util.Objects;
import java.util.function.Predicate;

import com.digitalpetri.strictmachine.FsmContext;

public class GuardBuilder<S, E> extends ActionBuilder<S, E> {

    private final PredicatedTransition<S, E> transition;

    GuardBuilder(
        PredicatedTransition<S, E> transition,
        LinkedList<TransitionAction<S, E>> transitionActions
    ) {

        super(
            transition.getFrom(),
            s -> Objects.equals(s, transition.getTarget()),
            transition.getVia(),
            transitionActions
        );

        this.transition = transition;
    }

    public ActionBuilder<S, E> guardedBy(Predicate<FsmContext<S, E>> guard) {
        transition.setGuard(guard);

        return this;
    }

}
