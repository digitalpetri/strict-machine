/*
 * Copyright (c) 2024 Kevin Herron
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.digitalpetri.fsm;

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
   * Get the user-configurable context associated with this FSM instance.
   *
   * @return the user-configurable context associated with this FSM instance.
   */
  Object getContext();

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
