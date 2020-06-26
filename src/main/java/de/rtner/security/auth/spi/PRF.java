package de.rtner.security.auth.spi;

public interface PRF {
  void init(byte[] paramArrayOfbyte);
  
  byte[] doFinal(byte[] paramArrayOfbyte);
  
  int getHLen();
}
