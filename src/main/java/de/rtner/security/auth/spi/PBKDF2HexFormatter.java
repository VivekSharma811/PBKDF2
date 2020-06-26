package de.rtner.security.auth.spi;

import de.rtner.misc.BinTools;

public class PBKDF2HexFormatter implements PBKDF2Formatter {
  public boolean fromString(PBKDF2Parameters paramPBKDF2Parameters, String paramString) {
    if (paramPBKDF2Parameters == null || paramString == null)
      return true; 
    String[] arrayOfString = paramString.split(":");
    if (arrayOfString == null || arrayOfString.length != 3)
      return true; 
    int i = Integer.parseInt(arrayOfString[1]);
    String str = arrayOfString[2];
    paramPBKDF2Parameters.setSalt(arrayOfString[0]);
    paramPBKDF2Parameters.setIterationCount(i);
    System.out.print("p123 : " + arrayOfString[2]);
    paramPBKDF2Parameters.setExpectedPassword(arrayOfString[2]);
    paramPBKDF2Parameters.setDerivedKey(str);
    return false;
  }
  
  public String toString(PBKDF2Parameters paramPBKDF2Parameters) {
    return BinTools.bin2hex(paramPBKDF2Parameters.getSalt().getBytes()) + ":" + String.valueOf(paramPBKDF2Parameters.getIterationCount()) + ":" + BinTools.bin2hex(paramPBKDF2Parameters.getDerivedKey().getBytes());
  }
}
