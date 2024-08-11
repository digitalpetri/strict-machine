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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class ActionFromBuilderTest {

  @Test
  void actionFromBuilder_toInstance() throws InterruptedException {
    assertActionExecuted(afb -> afb.to(State.S2));
  }

  @Test
  void actionFromBuilder_toPredicate() throws InterruptedException {
    assertActionExecuted(afb -> afb.to(s -> s == State.S2));
  }

  @Test
  void actionFromBuilder_toAny() throws InterruptedException {
    assertActionExecuted(ActionFromBuilder::toAny);
  }

  private void assertActionExecuted(
      Function<ActionFromBuilder<State, Event>, ViaBuilder<State, Event>> f
  ) throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    ActionFromBuilder<State, Event> afb = fb.onTransitionFrom(State.S1);
    ViaBuilder<State, Event> viaBuilder = f.apply(afb);

    viaBuilder.via(Event.E1.class)
        .execute(ctx -> executed.set(true));

    fb.build(State.S1).fireEventBlocking(new Event.E1());

    assertTrue(executed.get());
  }

}
