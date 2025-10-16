package com.example.legacydemo.legacy;

import java.lang.reflect.Field;

/** Access to sun.misc.Unsafe is restricted and discouraged. */
public final class UnsafeDemo {
  private static Object unsafe() {
    try {
      Class<?> c = Class.forName("sun.misc.Unsafe");
      Field f = c.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      return f.get(null);
    } catch (Throwable t) {
      return null;
    }
  }
  public static boolean isAvailable() { return unsafe() != null; }
  public static int addressSize() {
    Object u = unsafe();
    if (u == null) return -1;
    try {
      return (int) u.getClass().getMethod("addressSize").invoke(u);
    } catch (Exception e) {
      return -1;
    }
  }
}
