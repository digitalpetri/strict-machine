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
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionBuilder<S, E> {

  private Predicate<S> from;
  private Predicate<S> to;
  private Predicate<E> via;
  private final LinkedList<TransitionAction<S, E>> transitionActions;

  ActionBuilder(
      Predicate<S> from,
      Predicate<S> to,
      Predicate<E> via,
      LinkedList<TransitionAction<S, E>> transitionActions
  ) {

    this.from = from;
    this.to = to;
    this.via = via;
    this.transitionActions = transitionActions;
  }

  /**
   * Add {@code action} to the list of {@link TransitionAction}s to be executed.
   * <p>
   * Actions are executed in the order they appear in the list.
   *
   * @param action the action to execute.
   * @return this {@link ActionBuilder}.
   */
  public ActionBuilder<S, E> execute(Action<S, E> action) {
    return executeLast(action);
  }

  /**
   * Add {@code action} to the end of the list of {@link TransitionAction}s to be executed.
   * <p>
   * Actions are executed in the order they appear in the list.
   *
   * @param action the action to execute.
   * @return this {@link ActionBuilder}.
   */
  public ActionBuilder<S, E> executeLast(Action<S, E> action) {
    transitionActions.addLast(
        new PredicatedTransitionAction<>(
            from,
            to,
            via,
            action::execute
        )
    );

    return this;
  }

  /**
   * Add {@code action} to the beginning of the list of {@link TransitionAction}s to be executed.
   * <p>
   * Actions are executed in the order they appear in the list.
   *
   * @param action the action to execute.
   * @return this {@link ActionBuilder}.
   */
  public ActionBuilder<S, E> executeFirst(Action<S, E> action) {
    transitionActions.addFirst(
        new PredicatedTransitionAction<>(
            from,
            to,
            via,
            action::execute
        )
    );

    return this;
  }

  private static class PredicatedTransitionAction<S, E> implements TransitionAction<S, E> {

    private final Predicate<S> from;
    private final Predicate<S> to;
    private final Predicate<E> via;
    private final Consumer<ActionContext<S, E>> action;

    PredicatedTransitionAction(
        Predicate<S> from,
        Predicate<S> to,
        Predicate<E> via,
        Consumer<ActionContext<S, E>> action
    ) {

      this.from = from;
      this.to = to;
      this.via = via;
      this.action = action;
    }

    @Override
    public void execute(ActionContext<S, E> context) {
      action.accept(context);
    }

    @Override
    public boolean matches(S from, S to, E event) {
      return this.from.test(from) && this.to.test(to) && this.via.test(event);
    }

  }

}
