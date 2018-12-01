package com.digitalpetri.strictmachine.dsl

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean

class ViaBuilderTest {

    @Test
    fun `Action built with via Instance`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        fb.onTransitionFrom(State.S1)
            .to(State.S2)
            .via(Event.E1)
            .execute { executed.set(true) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        assertTrue(executed.get()) {
            "expected execute callback to set executed=true"
        }
    }

    @Test
    fun `Action built with via Class`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        fb.onTransitionFrom(State.S1)
            .to(State.S2)
            .via(Event.E1::class.java)
            .execute { executed.set(true) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        assertTrue(executed.get()) {
            "expected execute callback to set executed=true"
        }
    }

    @Test
    fun `Action built with via Predicate`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        fb.onTransitionFrom(State.S1)
            .to(State.S2)
            .via { e -> e == Event.E1 }
            .execute { executed.set(true) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        assertTrue(executed.get()) {
            "expected execute callback to set executed=true"
        }
    }

    @Test
    fun `Action built with via Any`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .onAny()
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        fb.onTransitionFrom(State.S1)
            .to(State.S2)
            .viaAny()
            .execute { executed.set(true) }

        for (event in listOf(
            Event.E1,
            Event.E2,
            Event.E3,
            Event.E4
        )) {
            executed.set(false)

            fb.build(State.S1).fireEventBlocking(event)

            assertTrue(executed.get()) {
                "expected execute callback to set executed=true"
            }
        }
    }

}
