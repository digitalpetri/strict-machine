package com.digitalpetri.strictmachine;

import com.digitalpetri.strictmachine.LogSink.NoOpLogSink;

import java.util.concurrent.atomic.AtomicReference;

public class Log {

  private static final AtomicReference<LogSink> LOG_SINK =
      new AtomicReference<>(new NoOpLogSink());

  public static void configure(LogSink sink) {
    LOG_SINK.set(sink);
  }

  public static void log(Level level, String message) {
    LOG_SINK.get().log(level, message);
  }

  public static void log(Level level, String message, Throwable t) {
    LOG_SINK.get().log(level, message, t);
  }

  public static boolean isLevelEnabled(Level level) {
    return LOG_SINK.get().isLevelEnabled(level);
  }

  public enum Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
  }
  
}
