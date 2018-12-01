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
