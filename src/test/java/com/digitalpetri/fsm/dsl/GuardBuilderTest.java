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

import com.digitalpetri.fsm.FsmContext;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class GuardBuilderTest {

  @Test
  void guardBuilder() {
    var transition = new PredicatedTransition<State, Event>(
        s -> s == State.S1,
        e -> e instanceof Event.E1,
        State.S2
    );

    var actions = new LinkedList<TransitionAction<State, Event>>();
    var guardBuilder = new GuardBuilder<>(transition, actions);
    Predicate<FsmContext<State, Event>> guard = ctx -> true;

    guardBuilder.guardedBy(guard);

    assertEquals(guard, transition.getGuard());
  }

  @Test
  void guardedTransition() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    var guardCondition = new AtomicBoolean(false);

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2)
        .guardedBy(ctx -> guardCondition.get());

    var fsm = fb.build(State.S1);

    assertEquals(State.S1, fsm.fireEventBlocking(new Event.E1()));
    guardCondition.set(true);
    assertEquals(State.S2, fsm.fireEventBlocking(new Event.E1()));
  }

}
