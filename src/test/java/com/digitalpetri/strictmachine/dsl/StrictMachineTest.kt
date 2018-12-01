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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch


class StrictMachineTest {

    @Test
    fun `Events fired in execute callbacks evaluate correctly`() {
        val fb = FsmBuilder<State, Event>()

        val latch = CountDownLatch(1)

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)
            .execute { ctx ->
                ctx.fireEvent(Event.E2)
                ctx.fireEvent(Event.E3)
            }

        fb.`when`(State.S2)
            .on(Event.E2)
            .transitionTo(State.S3)

        fb.`when`(State.S3)
            .on(Event.E3)
            .transitionTo(State.S1)
            .execute {
                latch.countDown()
            }

        val fsm = fb.build(State.S1)

        fsm.fireEvent(Event.E1)

        latch.await()
        assertEquals(fsm.state, State.S1)
    }

    @Test
    fun `Events can be shelved and un-shelved`() {
        val fb = FsmBuilder<State, Event>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)

        // on an internal transition via E2 shelve that event
        fb.onInternalTransition(State.S1)
            .via(Event.E2)
            .execute { ctx -> ctx.shelveEvent(ctx.event()) }

        // on an external transition from S1, process any events shelved while in S1
        fb.onTransitionFrom(State.S1)
            .to { s -> s != State.S1 }
            .viaAny()
            .execute { ctx -> ctx.processShelvedEvents() }

        fb.`when`(State.S2)
            .on(Event.E2)
            .transitionTo(State.S3)

        fb.`when`(State.S3)
            .on(Event.E3)
            .transitionTo(State.S4)


        val fsm = fb.build(State.S1)

        // fire an E2 that gets shelved
        fsm.fireEventBlocking(Event.E2)

        // fire E1 to trigger S1 -> S2
        fsm.fireEventBlocking(Event.E1)

        // fsm should have processed event shelf and landed in S3.
        // now move it to S4 via E3 and check the result.
        assertEquals(State.S4, fsm.fireEventBlocking(Event.E3))
    }

}
