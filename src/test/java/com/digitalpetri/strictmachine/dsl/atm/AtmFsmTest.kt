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

