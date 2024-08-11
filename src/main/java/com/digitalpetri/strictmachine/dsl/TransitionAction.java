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

public interface TransitionAction<S, E> {

  /**
   * Execute the {@link Action}s backing this TransitionAction.
   *
   * @param context the {@link ActionContext}.
   */
  void execute(ActionContext<S, E> context);

  /**
   * Test whether this TransitionAction is applicable to the transition criteria.
   *
   * @param from the state transitioned from.
   * @param to the state transitioned to.
   * @param event the event that caused the transition.
   * @return {@code true} if this transition action is applicable to the transition criteria.
   */
  boolean matches(S from, S to, E event);

}
