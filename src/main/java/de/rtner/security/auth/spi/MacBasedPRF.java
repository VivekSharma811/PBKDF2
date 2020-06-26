package de.rtner.security.auth.spi;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MacBasedPRF implements PRF {
  protected Mac mac;
  
  protected int hLen;
  
  protected String macAlgorithm;
  
  public MacBasedPRF(String paramString) {
    this.macAlgorithm = paramString;
    try {
      this.mac = Mac.getInstance(paramString);
      this.hLen = this.mac.getMacLength();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public MacBasedPRF(String paramString1, String paramString2) {
    this.macAlgorithm = paramString1;
    try {
      this.mac = Mac.getInstance(paramString1, paramString2);
      this.hLen = this.mac.getMacLength();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new RuntimeException(noSuchProviderException);
    } 
  }
  
  public byte[] doFinal(byte[] paramArrayOfbyte) {
    return this.mac.doFinal(paramArrayOfbyte);
  }
  
  public int getHLen() {
    return this.hLen;
  }
  
  public void init(byte[] paramArrayOfbyte) {
    try {
      this.mac.init(new SecretKeySpec(paramArrayOfbyte, this.macAlgorithm));
    } catch (InvalidKeyException invalidKeyException) {
      throw new RuntimeException(invalidKeyException);
    } 
  }
}
