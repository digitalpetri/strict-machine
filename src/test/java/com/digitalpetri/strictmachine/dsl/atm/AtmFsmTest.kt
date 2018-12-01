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

package com.digitalpetri.strictmachine.dsl.atm

import com.digitalpetri.strictmachine.dsl.atm.AtmFsm.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AtmFsmTest {

    @Test
    fun `S(Idle) x E(Connected) = S'(Loading)`() {
        assertEquals(State.Loading, State.Idle x Event.Connected)
    }

    @Test
    fun `S(Loading) x E(LoadSuccess) = S'(InService)`() {
        assertEquals(State.InService, State.Loading x Event.LoadSuccess)
    }

    @Test
    fun `S(Loading) x E(LoadFail) = S'(OutOfService)`() {
        assertEquals(State.OutOfService, State.Loading x Event.LoadFail)
    }

    @Test
    fun `S(Loading) x E(ConnectionClosed) = S'(Disconnected)`() {
        assertEquals(State.Disconnected, State.Loading x Event.ConnectionClosed)
    }

    @Test
    fun `S(OutOfService) x E(Startup) = S'(InService)`() {
        assertEquals(State.InService, State.OutOfService x Event.Startup)
    }

    @Test
    fun `S(OutOfService) x E(ConnectionLost) = S'(Disconnected)`() {
        assertEquals(State.Disconnected, State.OutOfService x Event.ConnectionLost)
    }

    @Test
    fun `S(InService) x E(ConnectionLost) = S'(Disconnected)`() {
        assertEquals(State.Disconnected, State.InService x Event.ConnectionLost)
    }

    @Test
    fun `S(InService) x E(Shutdown) = S'(OutOfService)`() {
        assertEquals(State.OutOfService, State.InService x Event.Shutdown)
    }

    @Test
    fun `S(Disconnected) x E(ConnectionRestored) = S'(InService)`() {
        assertEquals(State.InService, State.Disconnected x Event.ConnectionRestored)
    }

    private infix fun State.x(event: Event): State {
        val fsm = newAtmFsmInState(this)

        return fsm.fireEventBlocking(event).also { nextState ->
            println("S($this) x E($event) = S'($nextState)")
        }
    }

    private fun newAtmFsmInState(state: AtmFsm.State): AtmFsm {
        return buildFsm { state }
    }

}

