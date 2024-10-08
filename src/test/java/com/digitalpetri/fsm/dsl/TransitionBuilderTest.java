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

import org.junit.jupiter.api.Test;

public class TransitionBuilderTest {

  @Test
  void transitionFromEventInstance() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.INSTANCE)
        .transitionTo(State.S2);

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(Event.E1.INSTANCE));

    // internal transitions
    assertEquals(State.S1, fb.build(State.S1).fireEventBlocking(Event.E2.INSTANCE));
    assertEquals(State.S1, fb.build(State.S1).fireEventBlocking(Event.E3.INSTANCE));
  }

  @Test
  void transitionFromEventClass() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2);

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E1()));

    // internal transitions
    assertEquals(State.S1, fb.build(State.S1).fireEventBlocking(new Event.E2()));
    assertEquals(State.S1, fb.build(State.S1).fireEventBlocking(new Event.E3()));
  }

  @Test
  void transitionFromPredicate() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(e -> e instanceof Event.E1 || e instanceof Event.E2)
        .transitionTo(State.S2);

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E1()));
    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E2()));

    // internal transitions
    assertEquals(State.S1, fb.build(State.S1).fireEventBlocking(new Event.E3()));
  }

  @Test
  void transitionFromOnAny() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .onAny()
        .transitionTo(State.S2);

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E1()));
    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E2()));
    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E3()));
    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(new Event.E4()));
  }

  @Test
  void firstOfMultipleWins() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S3);

    // first definition "wins", this should not result in S2
    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2);

    assertEquals(State.S3, fb.build(State.S1).fireEventBlocking(new Event.E1()));
  }

}
