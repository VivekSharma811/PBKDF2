package de.rtner.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.LinkedList;

public class NestableGroup extends SimplePrincipal implements Group {
  private LinkedList rolesStack = new LinkedList();
  
  public NestableGroup(String paramString) {
    super(paramString);
  }
  
  public Enumeration members() {
    return new IndexEnumeration();
  }
  
  public boolean removeMember(Principal paramPrincipal) {
    return this.rolesStack.remove(paramPrincipal);
  }
  
  public boolean addMember(Principal paramPrincipal) throws IllegalArgumentException {
    if (!(paramPrincipal instanceof Group))
      throw new IllegalArgumentException("The addMember argument must be a Group"); 
    this.rolesStack.addFirst(paramPrincipal);
    return true;
  }
  
  public boolean isMember(Principal paramPrincipal) {
    if (this.rolesStack.size() == 0)
      return false; 
    Group group = this.rolesStack.getFirst();
    return group.isMember(paramPrincipal);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(getName());
    stringBuffer.append("(members:");
    Enumeration enumeration = members();
    while (enumeration.hasMoreElements()) {
      stringBuffer.append(enumeration.nextElement());
      stringBuffer.append(',');
    } 
    stringBuffer.setCharAt(stringBuffer.length() - 1, ')');
    return stringBuffer.toString();
  }
  
  private class IndexEnumeration implements Enumeration {
    private Enumeration iter;
    
    IndexEnumeration() {
      if (NestableGroup.this.rolesStack.size() > 0) {
        Group group = NestableGroup.this.rolesStack.get(0);
        this.iter = group.members();
      } 
    }
    
    public boolean hasMoreElements() {
      return (this.iter != null && this.iter.hasMoreElements());
    }
    
    public Object nextElement() {
      Object object = null;
      if (this.iter != null)
        object = this.iter.nextElement(); 
      return object;
    }
  }
}
