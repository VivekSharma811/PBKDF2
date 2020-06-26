package de.rtner.security.auth.spi;

public interface PBKDF2Formatter {
  String toString(PBKDF2Parameters paramPBKDF2Parameters);
  
  boolean fromString(PBKDF2Parameters paramPBKDF2Parameters, String paramString);
}
