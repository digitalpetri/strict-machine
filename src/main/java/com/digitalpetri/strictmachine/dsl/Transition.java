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

import com.digitalpetri.strictmachine.FsmContext;

public interface Transition<S, E> {

  /**
   * Get the target state of this transition.
   *
   * @return the target state of this transition.
   */
  S target();

  /**
   * Test whether this Transition is applicable for the current {@code state} and {@code event}.
   *
   * @param ctx the {@link FsmContext}.
   * @param state the current FSM state.
   * @param event the event being evaluated.
   * @return {@code true} if this transition is applicable for {@code state} and {@code event}.
   */
  boolean matches(FsmContext<S, E> ctx, S state, E event);


}
