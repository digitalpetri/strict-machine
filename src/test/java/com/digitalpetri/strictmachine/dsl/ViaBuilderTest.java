package com.digitalpetri.strictmachine.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

public class ViaBuilderTest {

  @Test
  void actionBuiltWithViaInstance() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.INSTANCE)
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    fb.onTransitionFrom(State.S1)
        .to(State.S2)
        .via(Event.E1.INSTANCE)
        .execute(ctx -> executed.set(true));

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(Event.E1.INSTANCE));

    assertTrue(executed.get());
  }

  @Test
  void actionBuiltWithViaClass() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.INSTANCE)
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    fb.onTransitionFrom(State.S1)
        .to(State.S2)
        .via(Event.E1.class)
        .execute(ctx -> executed.set(true));

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(Event.E1.INSTANCE));

    assertTrue(executed.get());
  }

  @Test
  void actionBuiltWithViaPredicate() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .on(Event.E1.INSTANCE)
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    fb.onTransitionFrom(State.S1)
        .to(State.S2)
        .via(e -> e instanceof Event.E1)
        .execute(ctx -> executed.set(true));

    assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(Event.E1.INSTANCE));

    assertTrue(executed.get());
  }

  @Test
  void actionBuiltWithViaAny() throws InterruptedException {
    var fb = new FsmBuilder<State, Event>();

    fb.when(State.S1)
        .onAny()
        .transitionTo(State.S2);

    var executed = new AtomicBoolean(false);

    fb.onTransitionFrom(State.S1)
        .to(State.S2)
        .viaAny()
        .execute(ctx -> executed.set(true));

    for (Event event : List.of(new Event.E1(), new Event.E2(), new Event.E3())) {
      executed.set(false);
      assertEquals(State.S2, fb.build(State.S1).fireEventBlocking(event));
      assertTrue(executed.get());
    }
  }
  
}
