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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean


class ActionToBuilderTest {

    @Test
    fun `Action built with onTransitionTo using from Instance`() {
        testOnTransitionTo {
            from(State.S1)
        }
    }

    @Test
    fun `Action built with onTransitionTo using from Predicate`() {
        testOnTransitionTo {
            from { s -> s == State.S1 }
        }
    }

    @Test
    fun `Action built with onTransitionTo using from Any`() {
        testOnTransitionTo {
            fromAny()
        }
    }

    private fun testOnTransitionTo(configureFrom: ActionToBuilder<State, Event>.() -> ViaBuilder<State, Event>) {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        val executed = AtomicBoolean(false)

        val actionToBuilder = fb.onTransitionTo(State.S2)
        val viaBuilder = actionToBuilder.configureFrom()

        viaBuilder.via(Event.E1).execute { executed.set(true) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        Assertions.assertTrue(executed.get()) {
            "expected execute callback to set executed=true"
        }
    }


}
