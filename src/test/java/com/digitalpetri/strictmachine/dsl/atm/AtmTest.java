package com.digitalpetri.strictmachine.dsl.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.digitalpetri.strictmachine.dsl.atm.AtmFsm.Event;
import com.digitalpetri.strictmachine.dsl.atm.AtmFsm.State;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MethodName")
class AtmTest {

  @Test
  void Idle_Connected_Loading() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.Idle);

    assertEquals(State.Loading, fsm.fireEventBlocking(Event.Connected));
  }

  @Test
  void Loading_LoadSuccess_InService() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.Loading);

    assertEquals(State.InService, fsm.fireEventBlocking(Event.LoadSuccess));
  }

  @Test
  void Loading_LoadFail_OutOfService() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.Loading);

    assertEquals(State.OutOfService, fsm.fireEventBlocking(Event.LoadFail));
  }

  @Test
  void Loading_ConnectionClosed_Disconnected() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.Loading);

    assertEquals(State.Disconnected, fsm.fireEventBlocking(Event.ConnectionClosed));
  }

  @Test
  void OutOfService_Startup_InService() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.OutOfService);

    assertEquals(State.InService, fsm.fireEventBlocking(Event.Startup));
  }

  @Test
  void OutOfService_ConnectionLost_Disconnected() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.OutOfService);

    assertEquals(State.Disconnected, fsm.fireEventBlocking(Event.ConnectionLost));
  }

  @Test
  void InService_ConnectionLost_Disconnected() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.InService);

    assertEquals(State.Disconnected, fsm.fireEventBlocking(Event.ConnectionLost));
  }

  @Test
  void InService_Shutdown_OutOfService() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.InService);

    assertEquals(State.OutOfService, fsm.fireEventBlocking(Event.Shutdown));
  }

  @Test
  void Disconnected_ConnectionRestored_InService() throws InterruptedException {
    var fsm = AtmFsm.buildFsm(fb -> State.Disconnected);

    assertEquals(State.InService, fsm.fireEventBlocking(Event.ConnectionRestored));
  }

}
