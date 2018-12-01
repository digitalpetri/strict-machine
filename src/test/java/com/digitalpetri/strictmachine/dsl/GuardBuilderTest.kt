package com.digitalpetri.strictmachine.dsl

import com.digitalpetri.strictmachine.FsmContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Predicate


class GuardBuilderTest {

    @Test
    fun `GuardBuilder#guardedBy() sets the guard condition on the transition`() {
        val transition = PredicatedTransition<State, Event>(
            { s -> s == State.S1 },
            { e -> e == Event.E1 },
            State.S2
        )
        val transitionActions = mutableListOf<TransitionAction<State, Event>>()

        val gb = GuardBuilder(transition, transitionActions)

        val guard: Predicate<FsmContext<State, Event>> = Predicate { true }

        gb.guardedBy(guard)

        assertEquals(guard, transition.guard)
    }

    @Test
    fun `Transition only occurs when the guard condition is met`() {
        val fb = FsmBuilder<State, Event>()

        val guard = AtomicBoolean(false)

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)
            .guardedBy { guard.get() }

        val fsm = fb.build(State.S1)

        assertEquals(State.S1, fsm.fireEventBlocking(Event.E1))

        guard.set(true)

        assertEquals(State.S2, fsm.fireEventBlocking(Event.E1))
    }

}
