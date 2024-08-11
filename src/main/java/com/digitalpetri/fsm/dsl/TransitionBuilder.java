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
   * Continue defining a {@link Transition} that is triggered by an event of type
   * {@code eventClass}.
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
   * Continue defining a {@link Transition} that is triggered by any event that passes
   * {@code eventFilter}.
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
