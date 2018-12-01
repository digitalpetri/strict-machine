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

/**
 * The context in which a {@link Action} is being executed.
 * <p>
 * Provides access to the transition criteria: the from state, to state, and event that triggered the transition.
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
