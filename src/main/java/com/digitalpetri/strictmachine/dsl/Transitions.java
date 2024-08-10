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

import java.util.Objects;
import java.util.function.Predicate;

class Transitions {

  private Transitions() {}

  static <S, E> PredicatedTransition<S, E> fromInstanceViaClass(S state, Class<? extends E> event, S target) {
    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        e -> Objects.equals(e.getClass(), event),
        target
    );
  }

  static <S, E> PredicatedTransition<S, E> fromInstanceViaDynamic(S state, Predicate<E> via, S target) {
    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        via,
        target
    );
  }

  static <S, E> PredicatedTransition<S, E> fromInstanceViaInstance(S state, E event, S target) {
    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        e -> Objects.equals(e, event),
        target
    );
  }

}
