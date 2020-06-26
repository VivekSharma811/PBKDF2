package de.rtner.security.auth.spi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PBKDF2Engine implements PBKDF2 {
  protected PBKDF2Parameters parameters = null;
  
  protected PRF prf = null;
  
  public PBKDF2Engine() {}
  
  public PBKDF2Engine(PBKDF2Parameters paramPBKDF2Parameters) {}
  
  public PBKDF2Engine(PBKDF2Parameters paramPBKDF2Parameters, PRF paramPRF) {}
  
  public byte[] deriveKey(String paramString) {
    return deriveKey(paramString, 0);
  }
  
  public byte[] deriveKey(String paramString, int paramInt) {
    null = null;
    byte[] arrayOfByte = null;
    String str = this.parameters.getHashCharset();
    if (paramString == null)
      paramString = ""; 
    try {
      if (str == null) {
        arrayOfByte = paramString.getBytes();
      } else {
        arrayOfByte = paramString.getBytes(str);
      } 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    } 
    assertPRF(arrayOfByte);
    if (paramInt == 0)
      paramInt = this.prf.getHLen(); 
    return PBKDF2(this.prf, this.parameters.getSalt(), this.parameters.getIterationCount(), paramInt);
  }
  
  public boolean verifyKey(String paramString1, String paramString2, String paramString3) {
    byte[] arrayOfByte1 = deriveKey(paramString1, 0);
    System.out.println("Inputkey is : " + arrayOfByte1);
    String str1 = paramString1 + paramString2;
    String str2 = "SHA-512";
    byte[] arrayOfByte2 = new byte[100000];
    System.out.println("strToEncode : " + str1);
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(str2);
      arrayOfByte2 = str1.getBytes(StandardCharsets.UTF_8);
      System.out.println("Bytes are : " + arrayOfByte2);
      byte[] arrayOfByte = messageDigest.digest(arrayOfByte2);
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b : arrayOfByte) {
        stringBuilder.append(String.format("%02x", new Object[] { Integer.valueOf(b & 0xFF) }));
      } 
      String str = stringBuilder.toString();
      System.out.println("pwd is : " + str);
      PBKDF2Parameters pBKDF2Parameters = new PBKDF2Parameters();
      System.out.println("expected pwd is : " + paramString3);
      return str.equals(paramString3);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      System.out.println("Exception : " + noSuchAlgorithmException.toString());
      return false;
    } 
  }
  
  protected void assertPRF(byte[] paramArrayOfbyte) {
    if (this.prf == null)
      this.prf = new MacBasedPRF(this.parameters.getHashAlgorithm()); 
    this.prf.init(paramArrayOfbyte);
  }
  
  public PRF getPseudoRandomFunction() {
    return this.prf;
  }
  
  protected byte[] PBKDF2(PRF paramPRF, String paramString, int paramInt1, int paramInt2) {
    if (paramString == null)
      paramString = ""; 
    int i = paramPRF.getHLen();
    int j = ceil(paramInt2, i);
    int k = paramInt2 - (j - 1) * i;
    byte[] arrayOfByte = new byte[j * i];
    int m = 0;
    for (byte b = 1; b <= j; b++) {
      _F(arrayOfByte, m, paramPRF, paramString, paramInt1, b);
      m += i;
    } 
    if (k < i) {
      byte[] arrayOfByte1 = new byte[paramInt2];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, paramInt2);
      return arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  protected int ceil(int paramInt1, int paramInt2) {
    byte b = 0;
    if (paramInt1 % paramInt2 > 0)
      b = 1; 
    return paramInt1 / paramInt2 + b;
  }
  
  protected void _F(byte[] paramArrayOfbyte, int paramInt1, PRF paramPRF, String paramString, int paramInt2, int paramInt3) {
    int i = paramPRF.getHLen();
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[(paramString.getBytes()).length + 4];
    System.arraycopy(paramString.getBytes(), 0, arrayOfByte2, 0, paramString.length());
    INT(arrayOfByte2, paramString.length(), paramInt3);
    for (byte b = 0; b < paramInt2; b++) {
      arrayOfByte2 = paramPRF.doFinal(arrayOfByte2);
      xor(arrayOfByte1, arrayOfByte2);
    } 
    System.arraycopy(arrayOfByte1, 0, paramArrayOfbyte, paramInt1, i);
  }
  
  protected void xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < paramArrayOfbyte1.length; b++)
      paramArrayOfbyte1[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]); 
  }
  
  protected void INT(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    paramArrayOfbyte[paramInt1 + 0] = (byte)(paramInt2 / 16777216);
    paramArrayOfbyte[paramInt1 + 1] = (byte)(paramInt2 / 65536);
    paramArrayOfbyte[paramInt1 + 2] = (byte)(paramInt2 / 256);
    paramArrayOfbyte[paramInt1 + 3] = (byte)paramInt2;
  }
  
  public PBKDF2Parameters getParameters() {
    return this.parameters;
  }
  
  public void setParameters(PBKDF2Parameters paramPBKDF2Parameters) {
    this.parameters = paramPBKDF2Parameters;
  }
  
  public void setPseudoRandomFunction(PRF paramPRF) {
    this.prf = paramPRF;
  }
  
  public static void main(String[] paramArrayOfString) throws IOException, NoSuchAlgorithmException {
    String str1 = "password";
    String str2 = null;
    PBKDF2HexFormatter pBKDF2HexFormatter = new PBKDF2HexFormatter();
    if (paramArrayOfString.length >= 1)
      str1 = paramArrayOfString[0]; 
    if (paramArrayOfString.length >= 2)
      str2 = paramArrayOfString[1]; 
    PBKDF2Parameters pBKDF2Parameters = new PBKDF2Parameters();
    pBKDF2Parameters.setHashAlgorithm("HmacSHA512");
    pBKDF2Parameters.setHashCharset("UTF-8");
    if (pBKDF2HexFormatter.fromString(pBKDF2Parameters, str2))
      throw new IllegalArgumentException("Candidate data does not have correct format (\"" + str2 + "\")"); 
    PBKDF2Engine pBKDF2Engine = new PBKDF2Engine(pBKDF2Parameters);
    boolean bool = pBKDF2Engine.verifyKey(str1, pBKDF2Parameters.getSalt(), pBKDF2Parameters.getDerivedKey());
    System.out.println(bool ? "OK" : "FAIL");
    System.exit(bool ? 0 : 1);
  }
}
