package de.rtner.security.auth.spi;

public interface PBKDF2 {
  byte[] deriveKey(String paramString);
  
  byte[] deriveKey(String paramString, int paramInt);
  
  boolean verifyKey(String paramString1, String paramString2, String paramString3);
  
  PBKDF2Parameters getParameters();
  
  void setParameters(PBKDF2Parameters paramPBKDF2Parameters);
  
  PRF getPseudoRandomFunction();
  
  void setPseudoRandomFunction(PRF paramPRF);
}
