package com.digitalpetri.strictmachine.dsl

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean


class ActionFromBuilderTest {

    @Test
    fun `Action built with onTransitionFrom using to Instance`() {
        testOnTransitionFrom {
            to(State.S2)
        }
    }

    @Test
    fun `Action built with onTransitionFrom using to Predicate`() {
        testOnTransitionFrom {
            to { s -> s == State.S2 }
        }
    }

    @Test
    fun `Action built with onTransitionFrom using to Any`() {
        testOnTransitionFrom {
            toAny()
        }
    }

    private fun testOnTransitionFrom(configureTo: ActionFromBuilder<State, Event>.() -> ViaBuilder<State, Event>) {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        val actionFromBuilder = fb.onTransitionFrom(State.S1)
        val viaBuilder = actionFromBuilder.configureTo()

        viaBuilder.via(Event.E1).execute { executed.set(true) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        assertTrue(executed.get()) {
            "expected execute callback to set executed=true"
        }
    }

}
