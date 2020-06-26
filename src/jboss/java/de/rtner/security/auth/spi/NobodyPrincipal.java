package de.rtner.security.auth.spi;

import java.security.Principal;

public class NobodyPrincipal implements Comparable, Principal {
  public static final String NOBODY = "<NOBODY>";
  
  public static final NobodyPrincipal NOBODY_PRINCIPAL = new NobodyPrincipal();
  
  public int hashCode() {
    return "<NOBODY>".hashCode();
  }
  
  public String getName() {
    return "<NOBODY>";
  }
  
  public String toString() {
    return "<NOBODY>";
  }
  
  public boolean equals(Object paramObject) {
    return false;
  }
  
  public int compareTo(Object paramObject) {
    return 1;
  }
}
