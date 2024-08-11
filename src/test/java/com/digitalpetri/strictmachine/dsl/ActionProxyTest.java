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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ActionProxyTest {

  @Test
  void actionProxyGetsCalled() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    var state3Latch = new CountDownLatch(1);

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
        .execute(ctx -> state3Latch.countDown());

    var actionProxyLatch = new CountDownLatch(2);

    fb.setActionProxy((ctx, action) -> {
      actionProxyLatch.countDown();
      action.execute(ctx);
    });

    var fsm = fb.build(State.S1);

    fsm.fireEvent(new Event.E1());

    assertTrue(state3Latch.await(5, TimeUnit.SECONDS));
    assertTrue(actionProxyLatch.await(5, TimeUnit.SECONDS));
    assertEquals(State.S1, fsm.getState());
  }

}
