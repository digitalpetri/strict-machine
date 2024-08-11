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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.digitalpetri.fsm.FsmContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class StrictMachineTest {

  @Test
  void eventsFiredInExecuteCallbacks() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    var latch = new CountDownLatch(1);

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2)
        .execute(ctx -> {
          ctx.fireEvent(new Event.E2());
          ctx.fireEvent(new Event.E3());
        });

    fb.when(State.S2)
        .on(Event.E2.class)
        .transitionTo(State.S3);

    fb.when(State.S3)
        .on(Event.E3.class)
        .transitionTo(State.S1)
        .execute(ctx -> latch.countDown());

    var fsm = fb.build(State.S1);
    fsm.fireEvent(new Event.E1());

    assertTrue(latch.await(5, TimeUnit.SECONDS));
    assertEquals(State.S1, fsm.getState());
  }

  @Test
  void eventShelving() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2);

    fb.onInternalTransition(State.S1)
        .via(Event.E2.class)
        .execute(ctx -> ctx.shelveEvent(ctx.event()));

    fb.onTransitionFrom(State.S1)
        .to(s -> s != State.S1)
        .viaAny()
        .execute(FsmContext::processShelvedEvents);

    fb.when(State.S2)
        .on(Event.E2.class)
        .transitionTo(State.S3);

    fb.when(State.S3)
        .on(Event.E3.class)
        .transitionTo(State.S4);

    var fsm = fb.build(State.S1);

    // fire an E2 that gets shelved
    fsm.fireEventBlocking(new Event.E2());

    // fire E1 to trigger S1 -> S2
    fsm.fireEventBlocking(new Event.E1());

    // fsm should have processed event shelf and landed in S3.
    // now move to S4 via E3 and check the result.
    assertEquals(State.S4, fsm.fireEventBlocking(new Event.E3()));
  }

}
