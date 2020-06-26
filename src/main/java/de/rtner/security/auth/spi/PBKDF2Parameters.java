package de.rtner.security.auth.spi;

public class PBKDF2Parameters {
  protected String salt = this.salt;
  
  protected String expectedPassword = this.expectedPassword;
  
  protected int iterationCount = 1000;
  
  protected String hashAlgorithm = null;
  
  protected String hashCharset = "UTF-8";
  
  protected String derivedKey = null;
  
  public PBKDF2Parameters() {}
  
  public PBKDF2Parameters(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4) {}
  
  public PBKDF2Parameters(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5) {}
  
  public int getIterationCount() {
    return this.iterationCount;
  }
  
  public void setIterationCount(int paramInt) {
    this.iterationCount = paramInt;
  }
  
  public String getSalt() {
    return this.salt;
  }
  
  public void setSalt(String paramString) {
    this.salt = paramString;
  }
  
  public String getDerivedKey() {
    return this.derivedKey;
  }
  
  public void setDerivedKey(String paramString) {
    this.derivedKey = paramString;
  }
  
  public String getHashAlgorithm() {
    return this.hashAlgorithm;
  }
  
  public void setHashAlgorithm(String paramString) {
    this.hashAlgorithm = paramString;
  }
  
  public void setExpectedPassword(String paramString) {
    this.expectedPassword = paramString;
  }
  
  public String getExpectedPassword() {
    return this.expectedPassword;
  }
  
  public String getHashCharset() {
    return this.hashCharset;
  }
  
  public void setHashCharset(String paramString) {
    this.hashCharset = paramString;
  }
}
