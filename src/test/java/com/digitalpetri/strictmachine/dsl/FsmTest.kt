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
