package de.rtner.security.auth.spi;

import java.security.Principal;

public class AnybodyPrincipal implements Comparable, Principal {
  public static final String ANYBODY = "<ANYBODY>";
  
  public static final AnybodyPrincipal ANYBODY_PRINCIPAL = new AnybodyPrincipal();
  
  public int hashCode() {
    return "<ANYBODY>".hashCode();
  }
  
  public String getName() {
    return "<ANYBODY>";
  }
  
  public String toString() {
    return "<ANYBODY>";
  }
  
  public boolean equals(Object paramObject) {
    return true;
  }
  
  public int compareTo(Object paramObject) {
    return 0;
  }
}
