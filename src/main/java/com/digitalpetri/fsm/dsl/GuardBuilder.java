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

import com.digitalpetri.fsm.FsmContext;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

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
