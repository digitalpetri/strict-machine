package com.digitalpetri.strictmachine.internal;

import com.digitalpetri.strictmachine.FsmLogging;
import com.digitalpetri.strictmachine.FsmLogging.Level;
import java.util.concurrent.atomic.AtomicReference;

public class Log {

  public static final AtomicReference<FsmLogging.Callback> CALLBACK = new AtomicReference<>();

  private Log() {}

  public static void trace(Object context, String format, Object... args) {
    log(context, Level.TRACE, format, args);
  }

  public static void debug(Object context, String format, Object... args) {
    log(context, Level.DEBUG, format, args);
  }

  public static void info(Object context, String format, Object... args) {
    log(context, Level.INFO, format, args);
  }

  public static void warn(Object context, String format, Object... args) {
    log(context, Level.WARN, format, args);
  }

  public static void error(Object context, String format, Object... args) {
    log(context, Level.ERROR, format, args);
  }

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
