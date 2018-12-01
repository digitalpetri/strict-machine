package com.digitalpetri.strictmachine.dsl


enum class State { S1, S2, S3, S4 }

sealed class Event {
    object E1 : Event() {
        override fun toString(): String = "E1"
    }

    object E2 : Event() {
        override fun toString(): String = "E2"
    }

    object E3 : Event() {
        override fun toString(): String = "E3"
    }

    object E4 : Event() {
        override fun toString(): String = "E4"
    }
}
