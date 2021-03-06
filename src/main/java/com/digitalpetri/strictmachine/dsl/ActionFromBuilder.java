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

public class ActionFromBuilder<S extends Enum<S>, E> {

    private final Predicate<S> fromFilter;
    private final LinkedList<TransitionAction<S, E>> transitionActions;

    ActionFromBuilder(Predicate<S> fromFilter, LinkedList<TransitionAction<S, E>> transitionActions) {
        this.fromFilter = fromFilter;
        this.transitionActions = transitionActions;
    }

    public ViaBuilder<S, E> to(S state) {
        return to(s -> Objects.equals(s, state));
    }

    public ViaBuilder<S, E> to(Predicate<S> toFilter) {
        return new ViaBuilder<>(
            fromFilter,
            toFilter,
            transitionActions
        );
    }

    public ViaBuilder<S, E> toAny() {
        return to(s -> true);
    }

}
