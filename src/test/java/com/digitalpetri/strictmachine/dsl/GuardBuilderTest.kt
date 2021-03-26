/*
 * Copyright 2018 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpetri.strictmachine.dsl

import com.digitalpetri.strictmachine.FsmContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
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
        val transitionActions = LinkedList<TransitionAction<State, Event>>()

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
