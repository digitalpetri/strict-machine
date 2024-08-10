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
import java.util.function.Predicate;

class PredicatedTransition<S, E> implements Transition<S, E> {

  private volatile Predicate<FsmContext<S, E>> guard = ctx -> true;

  private final Predicate<S> from;
  private final Predicate<E> via;
  private final S target;

  PredicatedTransition(Predicate<S> from, Predicate<E> via, S target) {
    this.from = from;
    this.via = via;
    this.target = target;
  }

  @Override
  public S target() {
    return target;
  }

  @Override
  public boolean matches(FsmContext<S, E> ctx, S state, E event) {
    return from.test(state) && via.test(event) && guard.test(ctx);
  }

  Predicate<FsmContext<S, E>> getGuard() {
    return guard;
  }

  Predicate<S> getFrom() {
    return from;
  }

  Predicate<E> getVia() {
    return via;
  }

  S getTarget() {
    return target;
  }

  void setGuard(Predicate<FsmContext<S, E>> guard) {
    this.guard = guard;
  }

}
