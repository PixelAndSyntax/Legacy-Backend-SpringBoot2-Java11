package com.example.legacydemo.legacy;

/** References Applet API which is removed in Java 17. */
public class AppletRef {
  public static String appletClassName() {
    return java.applet.Applet.class.getName();
  }
}
