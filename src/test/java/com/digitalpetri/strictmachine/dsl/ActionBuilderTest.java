package com.digitalpetri.strictmachine.dsl;

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
