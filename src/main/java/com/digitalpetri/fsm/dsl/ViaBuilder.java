/*
 * Copyright (c) 2024 Kevin Herron
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.digitalpetri.fsm.dsl;

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
