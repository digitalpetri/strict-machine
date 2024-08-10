# Strict Machine
![Maven Central](https://img.shields.io/maven-central/v/com.digitalpetri.fsm/strict-machine)

Strict Machine is a declarative DSL for building asynchronously evaluated Finite State Machines.

Release builds are available from [Maven Central](https://repo.maven.apache.org/maven2/) and SNAPSHOT builds are available from the [Sonatype OSS Snapshot Repository](https://oss.sonatype.org/content/repositories/snapshots/).

## Gradle
```
dependencies {
    compile("com.digitalpetri.fsm:strict-machine:1.0.0-SNAPSHOT")
}
```

## Maven
```
<dependencies>
    <dependency>
      <groupId>com.digitalpetri.fsm</groupId>
      <artifactId>strict-machine</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```


# What does it look like?

Given this simple ATM example:

![atmfsm.png](https://github.com/digitalpetri/strict-machine/blob/master/src/test/java/com/digitalpetri/strictmachine/dsl/atm/atmfsm.png)


Define the State and Event types:
```java
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
```

Define the transitions:
```java
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

Fsm<State, Event> fsm = fb.build(State.Idle);
```

Fire events to be evaluated asynchronously:
```java
fsm.fireEvent(Event.Connected);
```
or block waiting for the state that resulted from evaluating the event:
```java
State state = fsm.fireEventBlocking(Event.Connected); 
```


# Transition Guards
Guard conditions that prevent a transition from occurring unless they're met can be defined using `guardedBy`:

```java
fb.when(State.Idle)
    .on(Event.Connected)
    .transitionTo(State.Loading)
    .guardedBy(ctx -> ...guard condition here...);
```

Where the guard provided to `guardedBy` is a `Predicate` that receives the `FsmContext`.


# Transition Actions

Of course, in order for your state machine to actually *do* something, you need to define actions that execute when events are evaluated and transitions occur.

This is accomplished using the `onTransitionTo`, `onTransitionFrom`, and `onInternalTransition` methods on `FsmBuilder`. 

For example, logging a message on the transition from `Idle` to `Loading` might be defined in either of two ways:
```java
// define the action in the context of a transition away from Idle
fb.onTransitionFrom(State.Idle)
    .to(State.Loading)
    .via(Event.Connected)
    .execute(ctx -> System.out.println("S(Idle) x E(Connected) = S'(Loading)"));
        
// define the action in the context of a transition to Loading
fb.onTransitionTo(State.Loading)
    .from(State.Idle)
    .via(Event.Connected)
    .execute(ctx -> System.out.println("S(Idle) x E(Connected) = S'(Loading)"));
```


# More Examples

See the full [AtmFsm](https://github.com/kevinherron/strict-machine/blob/master/src/test/java/com/digitalpetri/strictmachine/dsl/atm/AtmFsm.java) defined in the test suite.

Advanced examples from production code:
- [ChannelFsmFactory](https://github.com/digitalpetri/netty-channel-fsm/blob/master/src/main/java/com/digitalpetri/netty/fsm/ChannelFsmFactory.java) from [netty-channel-fsm](https://github.com/digitalpetri/netty-channel-fsm).
- [SessionFsmFactory](https://github.com/eclipse/milo/blob/master/opc-ua-sdk/sdk-client/src/main/java/org/eclipse/milo/opcua/sdk/client/session/SessionFsmFactory.java) from [Eclipse Milo](https://github.com/eclipse/milo).
