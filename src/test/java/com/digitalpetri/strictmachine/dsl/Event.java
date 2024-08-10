package com.digitalpetri.strictmachine.dsl;

abstract class Event {

  static class E1 extends Event {

    static E1 INSTANCE = new E1();

    @Override
    public String toString() {
      return "E1";
    }
  }

  static class E2 extends Event {

    static E2 INSTANCE = new E2();

    @Override
    public String toString() {
      return "E2";
    }
  }

  static class E3 extends Event {

    static E3 INSTANCE = new E3();

    @Override
    public String toString() {
      return "E3";
    }
  }

  static class E4 extends Event {

    static E4 INSTANCE = new E4();

    @Override
    public String toString() {
      return "E4";
    }
  }

}
