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

package com.digitalpetri.strictmachine;

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
     * <p>
     * The subsequent state transition may occur asynchronously. There is no guarantee that a subsequent call to
     * {@link #getState()} reflects a state arrived at via evaluation of this event.
     *
     * @param event the event to evaluate.
     * @see #fireEvent(Object, Consumer)
     */
    void fireEvent(E event);

    /**
     * Fire an event for the FSM to evaluate, providing a callback that will be invoked when the event is evaluated.
     * This callback may occur asynchronously.
     *
     * @param event         the event to evaluate.
     * @param stateConsumer the callback that will receive the state transitioned to via evaluation of {@code event}.
     */
    void fireEvent(E event, Consumer<S> stateConsumer);

    /**
     * Fire an event for the FSM to evaluate and block waiting until the state transitioned to as a result of
     * evaluating {@code event} is available.
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

}
