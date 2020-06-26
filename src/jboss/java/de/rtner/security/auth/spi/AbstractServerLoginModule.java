package de.rtner.security.auth.spi;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public abstract class AbstractServerLoginModule implements LoginModule {
  protected Subject subject;
  
  protected CallbackHandler callbackHandler;
  
  protected Map sharedState;
  
  protected Map options;
  
  protected Logger log;
  
  protected boolean useFirstPass;
  
  protected boolean loginOk;
  
  protected String principalClassName;
  
  protected Principal unauthenticatedIdentity;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map paramMap1, Map paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    this.log = Logger.getLogger(getClass());
    this.log.trace("initialize");
    String str1 = (String)paramMap2.get("password-stacking");
    if (str1 != null && str1.equalsIgnoreCase("useFirstPass"))
      this.useFirstPass = true; 
    this.principalClassName = (String)paramMap2.get("principalClass");
    String str2 = (String)paramMap2.get("unauthenticatedIdentity");
    if (str2 != null)
      try {
        this.unauthenticatedIdentity = createIdentity(str2);
        this.log.trace("Saw unauthenticatedIdentity=" + str2);
      } catch (Exception exception) {
        this.log.warn("Failed to create custom unauthenticatedIdentity", exception);
      }  
  }
  
  public boolean login() throws LoginException {
    this.log.trace("login");
    this.loginOk = false;
    if (this.useFirstPass == true)
      try {
        Object object1 = this.sharedState.get("javax.security.auth.login.name");
        Object object2 = this.sharedState.get("javax.security.auth.login.password");
        if (object1 != null && object2 != null) {
          this.loginOk = true;
          return true;
        } 
      } catch (Exception exception) {
        this.log.error("login failed", exception);
      }  
    return false;
  }
  
  public boolean commit() throws LoginException {
    this.log.trace("commit, loginOk=" + this.loginOk);
    if (!this.loginOk)
      return false; 
    Set<Principal> set = this.subject.getPrincipals();
    Principal principal = getIdentity();
    set.add(principal);
    try {
      Group[] arrayOfGroup = getRoleSets();
      System.out.println("roleGroup returned : " + arrayOfGroup[0]);
      for (byte b = 0; b < arrayOfGroup.length; b++) {
        Group group1 = arrayOfGroup[b];
        String str = group1.getName();
        Group group2 = createGroup(str, set);
        if (group2 instanceof NestableGroup) {
          SimpleGroup simpleGroup = new SimpleGroup("Roles");
          group2.addMember(simpleGroup);
          group2 = simpleGroup;
        } 
        Enumeration<? extends Principal> enumeration = group1.members();
        while (enumeration.hasMoreElements()) {
          Principal principal1 = enumeration.nextElement();
          group2.addMember(principal1);
        } 
        System.out.println("subjectGroup : " + group2);
      } 
    } catch (Exception exception) {
      System.out.println("Exception : " + exception.toString());
    } 
    return true;
  }
  
  public boolean abort() throws LoginException {
    this.log.trace("abort");
    return true;
  }
  
  public boolean logout() throws LoginException {
    this.log.trace("logout");
    Principal principal = getIdentity();
    Set<Principal> set = this.subject.getPrincipals();
    set.remove(principal);
    return true;
  }
  
  protected abstract Principal getIdentity();
  
  protected abstract Group[] getRoleSets() throws LoginException;
  
  protected abstract String getRoles() throws LoginException;
  
  protected boolean getUseFirstPass() {
    return this.useFirstPass;
  }
  
  protected Principal getUnauthenticatedIdentity() {
    return this.unauthenticatedIdentity;
  }
  
  protected Group createGroup(String paramString, Set<Group> paramSet) {
    Group group = null;
    for (Group group1 : paramSet) {
      if (!(group1 instanceof Group))
        continue; 
      Group group2 = group1;
      if (group2.getName().equals(paramString)) {
        group = group2;
        break;
      } 
    } 
    if (group == null) {
      group = new SimpleGroup(paramString);
      paramSet.add(group);
    } 
    return group;
  }
  
  protected Principal createIdentity(String paramString) throws Exception {
    Principal principal = null;
    if (this.principalClassName == null) {
      principal = new SimplePrincipal(paramString);
    } else {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> clazz = classLoader.loadClass(this.principalClassName);
      Class[] arrayOfClass = { String.class };
      Constructor<?> constructor = clazz.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramString };
      principal = (Principal)constructor.newInstance(arrayOfObject);
    } 
    return principal;
  }
}
