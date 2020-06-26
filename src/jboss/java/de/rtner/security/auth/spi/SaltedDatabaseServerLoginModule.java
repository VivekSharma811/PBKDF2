package de.rtner.security.auth.spi;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

public class SaltedDatabaseServerLoginModule extends DatabaseServerLoginModule {
  public final String DEFAULT_FORMATTER = "de.rtner.security.auth.spi.PBKDF2HexFormatter";
  
  public final String DEFAULT_ENGINE = "de.rtner.security.auth.spi.PBKDF2Engine";
  
  public final String DEFAULT_PARAMETER = "de.rtner.security.auth.spi.PBKDF2Parameters";
  
  protected String hashAlgorithm = null;
  
  protected String hashCharset = null;
  
  protected String formatterClassName = null;
  
  protected PBKDF2Formatter formatter = null;
  
  protected String engineClassName = null;
  
  protected String parameterClassName = null;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map paramMap1, Map paramMap2) {
    super.initialize(paramSubject, paramCallbackHandler, paramMap1, paramMap2);
    this.hashAlgorithm = (String)paramMap2.get("hmacAlgorithm");
    if (this.hashAlgorithm == null)
      this.hashAlgorithm = "HMacSHA512"; 
    this.hashCharset = (String)paramMap2.get("hashCharset");
    this.formatterClassName = (String)paramMap2.get("formatter");
    if (this.formatterClassName == null)
      this.formatterClassName = "de.rtner.security.auth.spi.PBKDF2HexFormatter"; 
    this.engineClassName = (String)paramMap2.get("engine");
    if (this.engineClassName == null)
      this.engineClassName = "de.rtner.security.auth.spi.PBKDF2Engine"; 
    this.parameterClassName = (String)paramMap2.get("engine-parameters");
    if (this.parameterClassName == null)
      this.parameterClassName = "de.rtner.security.auth.spi.PBKDF2Parameters"; 
  }
  
  protected String createPasswordHash(String paramString1, String paramString2) {
    return paramString2;
  }
  
  protected boolean validatePassword(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    String[] arrayOfString = paramString2.split(":");
    String str1 = arrayOfString[0];
    System.out.println("Salt in validatePassword : " + str1);
    String str2 = arrayOfString[2];
    System.out.println("inputPassword : " + paramString1);
    System.out.println("expectedPassword : " + str2);
    PBKDF2Parameters pBKDF2Parameters = getEngineParameters();
    PBKDF2Formatter pBKDF2Formatter = getFormatter();
    if (pBKDF2Formatter.fromString(pBKDF2Parameters, paramString2))
      return false; 
    PBKDF2 pBKDF2 = getEngine(pBKDF2Parameters);
    return pBKDF2.verifyKey(paramString1, str1, str2);
  }
  
  protected PBKDF2Parameters getEngineParameters() {
    PBKDF2Parameters pBKDF2Parameters = null;
    try {
      pBKDF2Parameters = (PBKDF2Parameters)Class.forName(this.parameterClassName).newInstance();
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to instantiate implementation class (" + this.parameterClassName + ")");
    } 
    pBKDF2Parameters.setHashAlgorithm(this.hashAlgorithm);
    if (this.hashCharset != null)
      pBKDF2Parameters.setHashCharset(this.hashCharset); 
    return pBKDF2Parameters;
  }
  
  protected PBKDF2 getEngine(PBKDF2Parameters paramPBKDF2Parameters) {
    PBKDF2 pBKDF2 = null;
    try {
      pBKDF2 = (PBKDF2)Class.forName(this.engineClassName).newInstance();
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to instantiate implementation class (" + this.engineClassName + ")");
    } 
    pBKDF2.setParameters(paramPBKDF2Parameters);
    return pBKDF2;
  }
  
  protected PBKDF2Formatter getFormatter() {
    if (this.formatter == null)
      try {
        this.formatter = (PBKDF2Formatter)Class.forName(this.formatterClassName).newInstance();
      } catch (Exception exception) {
        throw new IllegalArgumentException("Unable to instantiate implementation class (" + this.formatterClassName + ")");
      }  
    return this.formatter;
  }
}
