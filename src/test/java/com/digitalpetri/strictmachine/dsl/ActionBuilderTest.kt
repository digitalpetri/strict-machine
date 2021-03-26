package com.digitalpetri.strictmachine.dsl

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ActionBuilderTest {

    @Test
    fun `Actions are executed in order`() {
        val fb = FsmBuilder<State, Event>()

        val executed = mutableListOf<Int>()

        fb.`when`(State.S1)
            .on(Event.E1)
            .transitionTo(State.S2)
            .execute { executed.add(0) }
            .executeLast { executed.add(1) }
            .executeFirst { executed.add(2) }

        fb.build(State.S1).fireEventBlocking(Event.E1)

        Assertions.assertTrue(executed[0] == 2)
        Assertions.assertTrue(executed[1] == 0)
        Assertions.assertTrue(executed[2] == 1)
    }

}
