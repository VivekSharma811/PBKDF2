package de.rtner.security.auth.spi;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

public abstract class UsernamePasswordLoginModule extends AbstractServerLoginModule {
  private Principal identity;
  
  private char[] credential;
  
  private String hashAlgorithm = null;
  
  private String hashCharset = null;
  
  private String hashEncoding = null;
  
  private boolean ignorePasswordCase;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map paramMap1, Map paramMap2) {
    super.initialize(paramSubject, paramCallbackHandler, paramMap1, paramMap2);
    this.hashAlgorithm = (String)paramMap2.get("hashAlgorithm");
    if (this.hashAlgorithm != null) {
      this.hashEncoding = (String)paramMap2.get("hashEncoding");
      if (this.hashEncoding == null)
        this.hashEncoding = "BASE64"; 
      this.hashCharset = (String)paramMap2.get("hashCharset");
      if (this.log.isTraceEnabled())
        this.log.trace("Password hashing activated: algorithm = " + this.hashAlgorithm + ", encoding = " + this.hashEncoding + ", charset = " + ((this.hashCharset == null) ? "{default}" : this.hashCharset) + ", callback = " + paramMap2.get("digestCallback")); 
    } 
    String str = (String)paramMap2.get("ignorePasswordCase");
    this.ignorePasswordCase = Boolean.valueOf(str).booleanValue();
  }
  
  public boolean login() throws LoginException {
    if (super.login() == true) {
      Object object1 = this.sharedState.get("javax.security.auth.login.name");
      if (object1 instanceof Principal) {
        this.identity = (Principal)object1;
      } else {
        String str = object1.toString();
        try {
          this.identity = createIdentity(str);
        } catch (Exception exception) {
          this.log.debug("Failed to create principal", exception);
          throw new LoginException("Failed to create principal: " + exception.getMessage());
        } 
      } 
      Object object2 = this.sharedState.get("javax.security.auth.login.password");
      if (object2 instanceof char[]) {
        this.credential = (char[])object2;
      } else if (object2 != null) {
        String str = object2.toString();
        this.credential = str.toCharArray();
      } 
      return true;
    } 
    this.loginOk = false;
    String[] arrayOfString = getUsernameAndPassword();
    String str1 = arrayOfString[0];
    String str2 = arrayOfString[1];
    if (str1 == null && str2 == null) {
      this.identity = this.unauthenticatedIdentity;
      this.log.trace("Authenticating as unauthenticatedIdentity=" + this.identity);
    } 
    if (this.identity == null) {
      try {
        this.identity = createIdentity(str1);
      } catch (Exception exception) {
        this.log.debug("Failed to create principal", exception);
        throw new LoginException("Failed to create principal: " + exception.getMessage());
      } 
      if (this.hashAlgorithm != null)
        str2 = createPasswordHash(str1, str2); 
      String str = getUsersPassword();
      if (!validatePassword(str2, str)) {
        this.log.debug("Bad password for username=" + str1);
        throw new FailedLoginException("Password Incorrect/Password Required");
      } 
    } 
    if (getUseFirstPass() == true) {
      this.sharedState.put("javax.security.auth.login.name", str1);
      this.sharedState.put("javax.security.auth.login.password", this.credential);
    } 
    this.loginOk = true;
    this.log.trace("User '" + this.identity + "' authenticated, loginOk=" + this.loginOk);
    return true;
  }
  
  protected Principal getIdentity() {
    return this.identity;
  }
  
  protected Principal getUnauthenticatedIdentity() {
    return this.unauthenticatedIdentity;
  }
  
  protected Object getCredentials() {
    return this.credential;
  }
  
  protected String getUsername() {
    String str = null;
    if (getIdentity() != null)
      str = getIdentity().getName(); 
    return str;
  }
  
  protected String[] getUsernameAndPassword() throws LoginException {
    String[] arrayOfString = { null, null };
    if (this.callbackHandler == null)
      throw new LoginException("Error: no CallbackHandler available to collect authentication information"); 
    NameCallback nameCallback = new NameCallback("User name: ", "guest");
    PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
    Callback[] arrayOfCallback = { nameCallback, passwordCallback };
    String str1 = null;
    String str2 = null;
    try {
      this.callbackHandler.handle(arrayOfCallback);
      str1 = nameCallback.getName();
      char[] arrayOfChar = passwordCallback.getPassword();
      if (arrayOfChar != null) {
        this.credential = new char[arrayOfChar.length];
        System.arraycopy(arrayOfChar, 0, this.credential, 0, arrayOfChar.length);
        passwordCallback.clearPassword();
        str2 = new String(this.credential);
      } 
    } catch (IOException iOException) {
      throw new LoginException(iOException.toString());
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new LoginException("CallbackHandler does not support: " + unsupportedCallbackException.getCallback());
    } 
    arrayOfString[0] = str1;
    arrayOfString[1] = str2;
    return arrayOfString;
  }
  
  protected String createPasswordHash(String paramString1, String paramString2) {
    DigestCallback digestCallback = null;
    String str = (String)this.options.get("digestCallback");
    if (str != null) {
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = classLoader.loadClass(str);
        digestCallback = (DigestCallback)clazz.newInstance();
        if (this.log.isTraceEnabled())
          this.log.trace("Created DigestCallback: " + digestCallback); 
      } catch (Exception exception) {
        if (this.log.isTraceEnabled())
          this.log.trace("Failed to load DigestCallback", exception); 
        SecurityException securityException = new SecurityException("Failed to load DigestCallback");
        securityException.initCause(exception);
        throw securityException;
      } 
      HashMap<Object, Object> hashMap = new HashMap<>(this.options);
      hashMap.put("javax.security.auth.login.name", paramString1);
      hashMap.put("javax.security.auth.login.password", paramString2);
      digestCallback.init(hashMap);
    } 
    return Util.createPasswordHash(this.hashAlgorithm, this.hashEncoding, this.hashCharset, paramString1, paramString2, digestCallback);
  }
  
  protected boolean validatePassword(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    boolean bool = false;
    if (this.ignorePasswordCase == true) {
      bool = paramString1.equalsIgnoreCase(paramString2);
    } else {
      bool = paramString1.equals(paramString2);
    } 
    return bool;
  }
  
  protected abstract String getUsersPassword() throws LoginException;
}
