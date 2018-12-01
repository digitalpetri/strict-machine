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
     * @param ctx   the {@link FsmContext}.
     * @param state the current FSM state.
     * @param event the event being evaluated.
     * @return {@code true} if this transition is applicable for {@code state} and {@code event}.
     */
    boolean matches(FsmContext<S, E> ctx, S state, E event);


}
