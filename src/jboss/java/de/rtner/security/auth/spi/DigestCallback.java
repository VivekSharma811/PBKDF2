package de.rtner.security.auth.spi;

import java.security.MessageDigest;
import java.util.Map;

public interface DigestCallback {
  void init(Map paramMap);
  
  void preDigest(MessageDigest paramMessageDigest);
  
  void postDigest(MessageDigest paramMessageDigest);
}
