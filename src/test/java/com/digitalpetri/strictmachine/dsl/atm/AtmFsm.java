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

package com.digitalpetri.strictmachine.dsl.atm;

import com.digitalpetri.strictmachine.Fsm;
import com.digitalpetri.strictmachine.dsl.FsmBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

public class AtmFsm {

  private final Fsm<State, Event> fsm;

  private AtmFsm(Fsm<State, Event> fsm) {
    this.fsm = fsm;
  }

  void fireEvent(Event event, Consumer<State> stateConsumer) {
    fsm.fireEvent(event, stateConsumer);
  }

  State fireEventBlocking(Event event) throws InterruptedException {
    return fsm.fireEventBlocking(event);
  }

  enum State {
    Idle,
    Loading,
    OutOfService,
    InService,
    Disconnected
  }

  enum Event {
    Connected,
    ConnectionClosed,
    ConnectionLost,
    ConnectionRestored,
    LoadFail,
    LoadSuccess,
    Shutdown,
    Startup
  }

  /**
   * Create a new {@link AtmFsm} in {@link State#Idle}.
   *
   * @return a new {@link AtmFsm} in {@link State#Idle}.
   */
  public static AtmFsm newAtmFsm() {
    return buildFsm(fb -> State.Idle);
  }

  /**
   * Build an {@link AtmFsm}.
   * <p>
   * {@code builderStateFunction} may make modifications to the FSM before it's built via the builder and returns the
   * desired initial state.
   *
   * @param builderStateFunction invoked after the builder has set up all state transitions. Returns the desired
   *                             initial state of the FSM.
   * @return an {@link AtmFsm}.
   */
  static AtmFsm buildFsm(Function<FsmBuilder<State, Event>, State> builderStateFunction) {
    FsmBuilder<State, Event> fb = new FsmBuilder<>();

    /* Idle */
    fb.when(State.Idle)
        .on(Event.Connected)
        .transitionTo(State.Loading);

    /* Loading */
    fb.when(State.Loading)
        .on(Event.LoadSuccess)
        .transitionTo(State.InService);

    fb.when(State.Loading)
        .on(Event.LoadFail)
        .transitionTo(State.OutOfService);

    fb.when(State.Loading)
        .on(Event.ConnectionClosed)
        .transitionTo(State.Disconnected);

    /* OutOfService */
    fb.when(State.OutOfService)
        .on(Event.Startup)
        .transitionTo(State.InService);

    fb.when(State.OutOfService)
        .on(Event.ConnectionLost)
        .transitionTo(State.Disconnected);

    /* InService */
    fb.when(State.InService)
        .on(Event.ConnectionLost)
        .transitionTo(State.Disconnected);

    fb.when(State.InService)
        .on(Event.Shutdown)
        .transitionTo(State.OutOfService);

    /* Disconnected */
    fb.when(State.Disconnected)
        .on(Event.ConnectionRestored)
        .transitionTo(State.InService);

    State initialState = builderStateFunction.apply(fb);

    return new AtmFsm(fb.build(initialState));
  }

}
