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
