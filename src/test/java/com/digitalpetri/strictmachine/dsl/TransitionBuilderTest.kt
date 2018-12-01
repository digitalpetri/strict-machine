package com.digitalpetri.strictmachine.dsl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TransitionBuilderTest {

    @Test
    fun `Transition built from Event instance`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        assertEquals(
            State.S2, fb.build(State.S1).fireEventBlocking(
                Event.E1
            )
        )

        // internal transitions
        assertEquals(
            State.S1, fb.build(State.S1).fireEventBlocking(
                Event.E2
            )
        )
        assertEquals(
            State.S1, fb.build(State.S1).fireEventBlocking(
                Event.E3
            )
        )
    }

    @Test
    fun `Transition built from Event class`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1::class.java)
            .transitionTo(State.S2)

        assertEquals(
            State.S2, fb.build(State.S1).fireEventBlocking(
                Event.E1
            )
        )

        // internal transitions
        assertEquals(
            State.S1, fb.build(State.S1).fireEventBlocking(
                Event.E2
            )
        )
        assertEquals(
            State.S1, fb.build(State.S1).fireEventBlocking(
                Event.E3
            )
        )
    }

    @Test
    fun `Transition built from Predicate`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on { e -> e == Event.E1 || e == Event.E2 }
            .transitionTo(State.S2)

        assertEquals(
            State.S2, fb.build(State.S1).fireEventBlocking(
                Event.E1
            )
        )
        assertEquals(
            State.S2, fb.build(State.S1).fireEventBlocking(
                Event.E2
            )
        )

        // internal transition
        assertEquals(
            State.S1, fb.build(State.S1).fireEventBlocking(
                Event.E3
            )
        )
    }

    @Test
    fun `Transition built from onAny()`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .onAny()
            .transitionTo(State.S2)

        fb.build(State.S1).apply {
            assertEquals(State.S2, fireEventBlocking(Event.E1))
            assertEquals(State.S2, state)
        }
        fb.build(State.S1).apply {
            assertEquals(State.S2, fireEventBlocking(Event.E2))
            assertEquals(State.S2, state)
        }
        fb.build(State.S1).apply {
            assertEquals(State.S2, fireEventBlocking(Event.E3))
            assertEquals(State.S2, state)
        }
    }

    @Test
    fun `First defined Transition of multiple wins`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S3)

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        assertEquals(
            State.S3, fb.build(State.S1).fireEventBlocking(
                Event.E1
            )
        )
    }

}
