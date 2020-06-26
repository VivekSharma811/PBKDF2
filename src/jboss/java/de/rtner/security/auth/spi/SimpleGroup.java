package de.rtner.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleGroup extends SimplePrincipal implements Group {
  private HashMap members = new HashMap<>(3);
  
  public SimpleGroup(String paramString) {
    super(paramString);
  }
  
  public boolean addMember(Principal paramPrincipal) {
    boolean bool = this.members.containsKey(paramPrincipal);
    if (!bool)
      this.members.put(paramPrincipal, paramPrincipal); 
    return !bool;
  }
  
  public boolean isMember(Principal paramPrincipal) {
    boolean bool = this.members.containsKey(paramPrincipal);
    if (!bool) {
      bool = paramPrincipal instanceof AnybodyPrincipal;
      if (!bool && paramPrincipal instanceof NobodyPrincipal)
        return false; 
    } 
    if (!bool) {
      Collection collection = this.members.values();
      Iterator<Object> iterator = collection.iterator();
      while (!bool && iterator.hasNext()) {
        Group group = (Group)iterator.next();
        if (group instanceof Group) {
          Group group1 = group;
          bool = group1.isMember(paramPrincipal);
        } 
      } 
    } 
    return bool;
  }
  
  public Enumeration members() {
    return Collections.enumeration(this.members.values());
  }
  
  public boolean removeMember(Principal paramPrincipal) {
    Object object = this.members.remove(paramPrincipal);
    return (object != null);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(getName());
    stringBuffer.append("(members:");
    Iterator iterator = this.members.keySet().iterator();
    while (iterator.hasNext()) {
      stringBuffer.append(iterator.next());
      stringBuffer.append(',');
    } 
    stringBuffer.setCharAt(stringBuffer.length() - 1, ')');
    return stringBuffer.toString();
  }
}
