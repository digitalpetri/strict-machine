/*
 * Copyright (c) 2024 Kevin Herron
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.digitalpetri.strictmachine.dsl;

import java.util.Objects;
import java.util.function.Predicate;

class Transitions {

  private Transitions() {}

  static <S, E> PredicatedTransition<S, E> fromInstanceViaClass(
      S state,
      Class<? extends E> event,
      S target
  ) {

    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        e -> Objects.equals(e.getClass(), event),
        target
    );
  }

  static <S, E> PredicatedTransition<S, E> fromInstanceViaDynamic(
      S state,
      Predicate<E> via,
      S target
  ) {

    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        via,
        target
    );
  }

  static <S, E> PredicatedTransition<S, E> fromInstanceViaInstance(
      S state,
      E event,
      S target
  ) {

    return new PredicatedTransition<>(
        s -> Objects.equals(s, state),
        e -> Objects.equals(e, event),
        target
    );
  }

}
