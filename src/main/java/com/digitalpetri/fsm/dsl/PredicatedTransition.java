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
