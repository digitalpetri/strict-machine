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
