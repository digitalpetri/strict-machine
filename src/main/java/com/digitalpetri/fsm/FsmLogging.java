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

public final class FsmLogging {

  private FsmLogging() {}

  /**
   * Configure the {@link Callback} to use for logging.
   *
   * @param callback the {@link Callback} to use for logging.
   */
  public static void configure(Callback callback) {
    Log.CALLBACK.set(callback);
  }

  public interface Callback {

    /**
     * Log a message at the given {@link Level}.
     *
     * @param context the user-configurable context. May be {@code null} even when configured if
     *     the message originates from a global context.
     * @param level the {@link Level} to log at.
     * @param message the message.
     */
    void log(Object context, Level level, String message);

    /**
     * Check if logging is enabled for the given {@link Level}.
     *
     * @param level the {@link Level} to check.
     * @return {@code true} if logging is enabled for the given {@link Level}.
     */
    default boolean isEnabled(Level level) {
      return true;
    }

  }

  public enum Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
  }

  /**
   * A logging {@link Callback} that logs to {@link System#out}.
   */
  public static final Callback SYSTEM_OUT_CALLBACK =
      (context, level, message) -> System.out.printf("%s: %s%n", level, message);

}
