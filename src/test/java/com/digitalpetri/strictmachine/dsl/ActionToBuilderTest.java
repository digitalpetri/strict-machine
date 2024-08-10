package com.digitalpetri.strictmachine.dsl;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionToBuilderTest {

  @Test
  void actionToBuilder_fromInstance() throws InterruptedException {
    assertActionExecuted(atb -> atb.from(State.S1));
  }

  @Test
  void actionToBuilder_fromPredicate() throws InterruptedException {
    assertActionExecuted(atb -> atb.from(s -> s == State.S1));
  }

  @Test
  void actionToBuilder_fromAny() throws InterruptedException {
    assertActionExecuted(ActionToBuilder::fromAny);
  }

  private void assertActionExecuted(
      Function<ActionToBuilder<State, Event>, ViaBuilder<State, Event>> f
  ) throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.class)
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    ActionToBuilder<State, Event> atb = fb.onTransitionTo(State.S2);
    ViaBuilder<State, Event> viaBuilder = f.apply(atb);

    viaBuilder.via(Event.E1.class)
        .execute(ctx -> executed.set(true));

    fb.build(State.S1).fireEventBlocking(new Event.E1());

    assertTrue(executed.get());
  }

}
