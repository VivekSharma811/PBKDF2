package de.rtner.security.auth.spi;

import java.io.Serializable;
import java.security.Principal;

public class SimplePrincipal implements Principal, Serializable {
  static final long serialVersionUID = 7701951188631723261L;
  
  private String name;
  
  public SimplePrincipal(String paramString) {
    this.name = paramString;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Principal))
      return false; 
    String str = ((Principal)paramObject).getName();
    boolean bool = false;
    if (this.name == null) {
      bool = (str == null);
    } else {
      bool = this.name.equals(str);
    } 
    return bool;
  }
  
  public int hashCode() {
    return (this.name == null) ? 0 : this.name.hashCode();
  }
  
  public String toString() {
    return this.name;
  }
  
  public String getName() {
    return this.name;
  }
}
