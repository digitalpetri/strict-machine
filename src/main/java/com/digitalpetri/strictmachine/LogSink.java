package com.digitalpetri.strictmachine;

public interface LogSink {

  void log(Log.Level level, String message);

  void log(Log.Level level, String message, Throwable t);

  boolean isLevelEnabled(Log.Level level);

  class NoOpLogSink implements LogSink {

    @Override
    public void log(Log.Level level, String message) {}

    @Override
    public void log(Log.Level level, String message, Throwable t) {}

    @Override
    public boolean isLevelEnabled(Log.Level level) {
      return false;
    }

  }

  class StdOutLogSink implements LogSink {

    @Override
    public void log(Log.Level level, String message) {
      System.out.println(level + ": " + message);
    }

    @Override
    public void log(Log.Level level, String message, Throwable t) {
      System.out.println(level + ": " + message);
      t.printStackTrace(System.out);
    }

    @Override
    public boolean isLevelEnabled(Log.Level level) {
      return true;
    }

  }

}
