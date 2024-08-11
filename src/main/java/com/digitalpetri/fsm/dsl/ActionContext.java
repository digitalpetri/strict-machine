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

/**
 * The context in which a {@link Action} is being executed.
 *
 * <p>Provides access to the transition criteria: the from state, to state, and event that triggered
 * the transition.
 *
 * @param <S> state type
 * @param <E> event type
 */
public interface ActionContext<S, E> extends FsmContext<S, E> {

  /**
   * Get the state being transitioned from.
   *
   * @return the state being transitioned from.
   */
  S from();

  /**
   * Get the state transitioned to.
   *
   * @return the state transitioned to.
   */
  S to();

  /**
   * Get the event that caused the transition.
   *
   * @return the event that caused the transition.
   */
  E event();

}
