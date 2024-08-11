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

import com.digitalpetri.fsm.FsmLogging.Level;
import java.util.concurrent.atomic.AtomicReference;

public class Log {

  public static final AtomicReference<FsmLogging.Callback> CALLBACK = new AtomicReference<>();

  private Log() {}

  /**
   * Log a message at {@link Level#TRACE}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void trace(Object context, String format, Object... args) {
    log(context, Level.TRACE, format, args);
  }

  /**
   * Log a message at {@link Level#DEBUG}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void debug(Object context, String format, Object... args) {
    log(context, Level.DEBUG, format, args);
  }

  /**
   * Log a message at {@link Level#INFO}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void info(Object context, String format, Object... args) {
    log(context, Level.INFO, format, args);
  }

  /**
   * Log a message at {@link Level#WARN}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void warn(Object context, String format, Object... args) {
    log(context, Level.WARN, format, args);
  }

  /**
   * Log a message at {@link Level#ERROR}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void error(Object context, String format, Object... args) {
    log(context, Level.ERROR, format, args);
  }

  /**
   * Check if logging is enabled for the given {@link Level}.
   *
   * @param level the {@link Level} to check.
   * @return {@code true} if logging is enabled for the given {@link Level}.
   */
  public static boolean isLevelEnabled(FsmLogging.Level level) {
    FsmLogging.Callback callback = CALLBACK.get();

    return callback == null || callback.isEnabled(level);
  }

  /**
   * Log a message at the given {@link Level}.
   *
   * @param context the user-configurable context. May be {@code null} even when configured.
   * @param level the {@link Level} to log at.
   * @param format the message format.
   * @param args the message arguments.
   */
  public static void log(Object context, Level level, String format, Object... args) {
    FsmLogging.Callback callback = CALLBACK.get();
    if (callback != null && callback.isEnabled(level)) {
      callback.log(context, level, String.format(format, args));
    }
  }

}
