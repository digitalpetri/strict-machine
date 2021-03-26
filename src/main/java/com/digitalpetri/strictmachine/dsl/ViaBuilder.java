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

public class ViaBuilder<S, E> {

    private final Predicate<S> fromFilter;
    private final Predicate<S> toFilter;
    private final LinkedList<TransitionAction<S, E>> transitionActions;

    ViaBuilder(
        Predicate<S> fromFilter,
        Predicate<S> toFilter,
        LinkedList<TransitionAction<S, E>> transitionActions
    ) {

        this.fromFilter = fromFilter;
        this.toFilter = toFilter;
        this.transitionActions = transitionActions;
    }

    public ActionBuilder<S, E> via(E event) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> Objects.equals(e, event),
            transitionActions
        );
    }

    public ActionBuilder<S, E> via(Class<? extends E> eventClass) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> Objects.equals(e.getClass(), eventClass),
            transitionActions
        );
    }

    public ActionBuilder<S, E> via(Predicate<E> eventFilter) {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            eventFilter,
            transitionActions
        );
    }

    public ActionBuilder<S, E> viaAny() {
        return new ActionBuilder<>(
            fromFilter,
            toFilter,
            e -> true,
            transitionActions
        );
    }

}
