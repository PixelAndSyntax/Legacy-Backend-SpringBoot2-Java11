package com.example.legacydemo.legacy;

import java.security.Principal;
import java.security.acl.*; // Deprecated in Java 11; removed in Java 17

/**
 * Minimal use of java.security.acl API to illustrate removal in Java 17.
 */
public class SecurityAclDemo {
  static class SimpleOwner implements Owner {
    private final Principal p;
    SimpleOwner(Principal p) { this.p = p; }
    @Override public boolean addOwner(Principal caller, Principal owner) { return false; }
    @Override public boolean deleteOwner(Principal caller, Principal owner) { return false; }
    @Override public boolean isOwner(Principal owner) { return p.equals(owner); }
  }
}
