/*
 * Copyright 2019 Kevin Herron
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ActionProxyTest {

    @Test
    fun `configured ActionProxy gets called`() {
        val fb = FsmBuilder<State, Event>()

        val actionProxyLatch = CountDownLatch(2)
        val s3Latch = CountDownLatch(1)

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
                s3Latch.countDown()
            }

        fb.setActionProxy { ctx, action ->
            actionProxyLatch.countDown()

            action.execute(ctx)
        }

        val fsm = fb.build(State.S1)

        fsm.fireEvent(Event.E1)

        s3Latch.await(5, TimeUnit.SECONDS)
        actionProxyLatch.await(5, TimeUnit.SECONDS)
        Assertions.assertEquals(fsm.state, State.S1)
    }
}
