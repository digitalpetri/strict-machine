/*
 * Copyright (c) 2024 Kevin Herron
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.digitalpetri.fsm;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Fsm<S, E> {

  /**
   * Get the current state of the FSM.
   *
   * @return the current state of the FSM.
   */
  S getState();

  /**
   * Fire an event for the FSM to evaluate.
   *
   * <p>The subsequent state transition may occur asynchronously. There is no guarantee that a
   * subsequent call to {@link #getState()} reflects a state arrived at via evaluation of this
   * event.
   *
   * @param event the event to evaluate.
   * @see #fireEvent(Object, Consumer)
   */
  void fireEvent(E event);

  /**
   * Fire an event for the FSM to evaluate, providing a callback that will be invoked when the event
   * is evaluated.
   *
   * <p>This callback may occur asynchronously.
   *
   * @param event the event to evaluate.
   * @param stateConsumer the callback that will receive the state transitioned to via
   *     evaluation of {@code event}.
   */
  void fireEvent(E event, Consumer<S> stateConsumer);

  /**
   * Fire an event for the FSM to evaluate and block waiting until the state transitioned to as a
   * result of evaluating {@code event} is available.
   *
   * @param event the event to evaluate.
   * @return the state transitioned to as a result of evaluating {@code event}.
   * @throws InterruptedException if interrupted while blocking.
   */
  S fireEventBlocking(E event) throws InterruptedException;

  /**
   * Provides safe access to the {@link FsmContext} in order to retrieve a value from it.
   *
   * @param get the Function provided access to the context.
   * @param <T> the type of the value being retrieved.
   * @return a value from {@code context}.
   */
  <T> T getFromContext(Function<FsmContext<S, E>, T> get);

  /**
   * Provides safe access to the {@link FsmContext}.
   *
   * @param contextConsumer the callback that will receive the {@link FsmContext}.
   */
  void withContext(Consumer<FsmContext<S, E>> contextConsumer);

}
