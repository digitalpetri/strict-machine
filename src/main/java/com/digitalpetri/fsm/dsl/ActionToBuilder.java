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

public class ActionToBuilder<S extends Enum<S>, E> {

  private final Predicate<S> toFilter;
  private final LinkedList<TransitionAction<S, E>> transitionActions;

  ActionToBuilder(Predicate<S> toFilter, LinkedList<TransitionAction<S, E>> transitionActions) {
    this.toFilter = toFilter;
    this.transitionActions = transitionActions;
  }

  public ViaBuilder<S, E> from(S from) {
    return from(s -> Objects.equals(s, from));
  }

  public ViaBuilder<S, E> from(Predicate<S> fromFilter) {
    return new ViaBuilder<>(
        fromFilter,
        toFilter,
        transitionActions
    );
  }

  public ViaBuilder<S, E> fromAny() {
    return from(s -> true);
  }

}
