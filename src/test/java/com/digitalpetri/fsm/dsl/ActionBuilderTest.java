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

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ActionBuilderTest {

  @Test
  void actionsAreExecutedInOrder() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    var executed = new ArrayList<Integer>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2)
        .execute(ctx -> executed.add(0))
        .executeLast(ctx -> executed.add(1))
        .executeFirst(ctx -> executed.add(2));

    fb.build(State.S1).fireEventBlocking(new Event.E1());

    assertEquals(2, (int) executed.get(0));
    assertEquals(0, (int) executed.get(1));
    assertEquals(1, (int) executed.get(2));
  }

}
