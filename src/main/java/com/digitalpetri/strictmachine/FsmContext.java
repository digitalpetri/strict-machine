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

package com.digitalpetri.strictmachine;

import java.util.Objects;
import java.util.StringJoiner;

public interface FsmContext<S, E> {

  /**
   * Get the current state of the FSM.
   *
   * @return the current state of the FSM.
   */
  S currentState();

  /**
   * Fire an event to be evaluated against the current state of the {@link Fsm}.
   *
   * @param event the event to be evaluated.
   */
  void fireEvent(E event);

  /**
   * Shelve an event to be evaluated at some later time.
   *
   * <p>This is useful e.g. when an event can't be handled in the current state but shouldn't be
   * discarded or ignored with an action-less internal transition.
   *
   * @param event the event to be queued.
   * @see #processShelvedEvents()
   */
  void shelveEvent(E event);

  /**
   * Drain the event shelf of any queued events and fire them for evaluation.
   */
  void processShelvedEvents();

  /**
   * Get the value identified by {@code key} from the context, or {@code null} if it does not
   * exist.
   *
   * @param key the {@link Key}.
   * @return the value identified by {@code key}, or {@code null} if it does not exist.
   */
  Object get(FsmContext.Key<?> key);

  /**
   * Get and remove the value identified by {@code key} from the context, or {@code null} if it does
   * not exist.
   *
   * @param key the {@link Key}.
   * @return the value identified by {@code key}, or {@code null} if it did not exist.
   */
  Object remove(FsmContext.Key<?> key);

  /**
   * Set a value identified by {@code key} on the context.
   *
   * @param key the {@link Key}.
   * @param value the value.
   */
  void set(FsmContext.Key<?> key, Object value);

  /**
   * Get the id assigned to this FSM instance.
   *
   * <p>The id is a monotonically increasing value assigned to each new instance to aid in
   * determining which log messages belong to which instance.
   *
   * @return the id assigned to this FSM instance.
   */
  long getInstanceId();

  final class Key<T> {

    private final String name;
    private final Class<T> type;

    public Key(String name, Class<T> type) {
      this.name = name;
      this.type = type;
    }

    public String name() {
      return name;
    }

    public Class<T> type() {
      return type;
    }

    public T get(FsmContext<?, ?> context) {
      Object value = context.get(this);

      return value != null ? type.cast(value) : null;
    }

    public T remove(FsmContext<?, ?> context) {
      Object value = context.remove(this);

      return value != null ? type.cast(value) : null;
    }

    public void set(FsmContext<?, ?> context, T value) {
      context.set(this, value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Key<?> key = (Key<?>) o;
      return Objects.equals(name, key.name)
          && Objects.equals(type, key.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type);
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", Key.class.getSimpleName() + "[", "]")
          .add("name='" + name + "'")
          .add("type=" + type)
          .toString();
    }

  }

}
