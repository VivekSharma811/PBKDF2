package de.rtner.security.auth.spi;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.Security;
import java.security.acl.Group;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.Subject;

public class Util {
  private static Logger log = Logger.getLogger(Util.class);
  
  private static final int HASH_LEN = 20;
  
  public static final String BASE64_ENCODING = "BASE64";
  
  public static final String BASE16_ENCODING = "HEX";
  
  private static SecureRandom psuedoRng;
  
  private static MessageDigest sha1Digest;
  
  private static boolean initialized;
  
  public static void init() throws NoSuchAlgorithmException {
    if (initialized)
      return; 
    init(null);
  }
  
  public static void init(byte[] paramArrayOfbyte) throws NoSuchAlgorithmException {
    sha1Digest = MessageDigest.getInstance("SHA");
    psuedoRng = SecureRandom.getInstance("SHA1PRNG");
    if (paramArrayOfbyte != null)
      psuedoRng.setSeed(paramArrayOfbyte); 
    JBossSXProvider jBossSXProvider = new JBossSXProvider();
    Security.addProvider(jBossSXProvider);
    initialized = true;
  }
  
  public static Group[] getRoleSets(String paramString1, String paramString2, String paramString3, Subject paramSubject, String paramString4) {
    System.out.println("getRoleSets in Util reached");
    Set<Principal> set = paramSubject.getPrincipals();
    Group group = createGroup("Roles", set);
    String[] arrayOfString = paramString4.split(",");
    for (byte b = 0; b < arrayOfString.length; b++) {
      SimpleGroup simpleGroup = new SimpleGroup(arrayOfString[b]);
      group.addMember(simpleGroup);
    } 
    Group[] arrayOfGroup = new Group[arrayOfString.length];
    arrayOfGroup[0] = group;
    System.out.println("principal : " + set);
    System.out.println("group : " + group);
    System.out.println("roleStes : " + arrayOfGroup[0]);
    return arrayOfGroup;
  }
  
  protected static Group createGroup(String paramString, Set<Group> paramSet) {
    Group group = null;
    for (Group group1 : paramSet) {
      if (!(group1 instanceof Group))
        continue; 
      Group group2 = group1;
      if (group2.getName().equals(paramString)) {
        group = group2;
        break;
      } 
    } 
    if (group == null) {
      group = new SimpleGroup(paramString);
      paramSet.add(group);
    } 
    return group;
  }
  
  public static MessageDigest newDigest() {
    MessageDigest messageDigest = null;
    try {
      messageDigest = (MessageDigest)sha1Digest.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    return messageDigest;
  }
  
  public static MessageDigest copy(MessageDigest paramMessageDigest) {
    MessageDigest messageDigest = null;
    try {
      messageDigest = (MessageDigest)paramMessageDigest.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    return messageDigest;
  }
  
  public static Random getPRNG() {
    return psuedoRng;
  }
  
  public static double nextDouble() {
    return psuedoRng.nextDouble();
  }
  
  public static long nextLong() {
    return psuedoRng.nextLong();
  }
  
  public static void nextBytes(byte[] paramArrayOfbyte) {
    psuedoRng.nextBytes(paramArrayOfbyte);
  }
  
  public static byte[] generateSeed(int paramInt) {
    return psuedoRng.generateSeed(paramInt);
  }
  
  public static byte[] calculatePasswordHash(String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte) {
    MessageDigest messageDigest = newDigest();
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = new byte[0];
    try {
      arrayOfByte1 = paramString.getBytes("UTF-8");
      arrayOfByte2 = ":".getBytes("UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      log.error("Failed to convert username to byte[] using UTF-8", unsupportedEncodingException);
      arrayOfByte1 = paramString.getBytes();
      arrayOfByte2 = ":".getBytes();
    } 
    byte[] arrayOfByte3 = new byte[2 * paramArrayOfchar.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfchar.length; b2++) {
      int i = paramArrayOfchar[b2] & Character.MAX_VALUE;
      byte b3 = (byte)(i & 0xFF);
      byte b4 = (byte)((i & 0xFF00) >> 8);
      arrayOfByte3[b1++] = b3;
      if (i > 255)
        arrayOfByte3[b1++] = b4; 
    } 
    messageDigest.update(arrayOfByte1);
    messageDigest.update(arrayOfByte2);
    messageDigest.update(arrayOfByte3, 0, b1);
    byte[] arrayOfByte4 = messageDigest.digest();
    messageDigest.reset();
    messageDigest.update(paramArrayOfbyte);
    messageDigest.update(arrayOfByte4);
    return messageDigest.digest();
  }
  
  public static byte[] calculateVerifier(String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    BigInteger bigInteger1 = new BigInteger(1, paramArrayOfbyte3);
    BigInteger bigInteger2 = new BigInteger(1, paramArrayOfbyte2);
    return calculateVerifier(paramString, paramArrayOfchar, paramArrayOfbyte1, bigInteger2, bigInteger1);
  }
  
  public static byte[] calculateVerifier(String paramString, char[] paramArrayOfchar, byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    byte[] arrayOfByte = calculatePasswordHash(paramString, paramArrayOfchar, paramArrayOfbyte);
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = paramBigInteger2.modPow(bigInteger1, paramBigInteger1);
    return bigInteger2.toByteArray();
  }
  
  public static byte[] sessionKeyHash(byte[] paramArrayOfbyte) {
    byte b2;
    for (b2 = 0; b2 < paramArrayOfbyte.length && paramArrayOfbyte[b2] == 0; b2++);
    byte[] arrayOfByte1 = new byte[40];
    int i = (paramArrayOfbyte.length - b2) / 2;
    byte[] arrayOfByte3 = new byte[i];
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      arrayOfByte3[b1] = paramArrayOfbyte[paramArrayOfbyte.length - 2 * b1 - 1]; 
    byte[] arrayOfByte2 = newDigest().digest(arrayOfByte3);
    for (b1 = 0; b1 < 20; b1++)
      arrayOfByte1[2 * b1] = arrayOfByte2[b1]; 
    for (b1 = 0; b1 < i; b1++)
      arrayOfByte3[b1] = paramArrayOfbyte[paramArrayOfbyte.length - 2 * b1 - 2]; 
    arrayOfByte2 = newDigest().digest(arrayOfByte3);
    for (b1 = 0; b1 < 20; b1++)
      arrayOfByte1[2 * b1 + 1] = arrayOfByte2[b1]; 
    return arrayOfByte1;
  }
  
  public static byte[] trim(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length == 0 || paramArrayOfbyte[0] != 0)
      return paramArrayOfbyte; 
    int i = paramArrayOfbyte.length;
    byte b;
    for (b = 1; paramArrayOfbyte[b] == 0 && b < i; b++);
    byte[] arrayOfByte = new byte[i - b];
    System.arraycopy(paramArrayOfbyte, b, arrayOfByte, 0, i - b);
    return arrayOfByte;
  }
  
  public static byte[] xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfByte[b] = (byte)(paramArrayOfbyte1[b] ^ paramArrayOfbyte2[b]); 
    return arrayOfByte;
  }
  
  public static String encodeBase16(byte[] paramArrayOfbyte) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfbyte.length * 2);
    for (byte b = 0; b < paramArrayOfbyte.length; b++) {
      byte b1 = paramArrayOfbyte[b];
      char c = (char)(b1 >> 4 & 0xF);
      if (c > '\t') {
        c = (char)(c - 10 + 97);
      } else {
        c = (char)(c + 48);
      } 
      stringBuffer.append(c);
      c = (char)(b1 & 0xF);
      if (c > '\t') {
        c = (char)(c - 10 + 97);
      } else {
        c = (char)(c + 48);
      } 
      stringBuffer.append(c);
    } 
    return stringBuffer.toString();
  }
  
  public static String encodeBase64(byte[] paramArrayOfbyte) {
    String str = null;
    try {
      str = paramArrayOfbyte.toString();
    } catch (Exception exception) {}
    return str;
  }
  
  public static String createPasswordHash(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    return createPasswordHash(paramString1, paramString2, paramString3, paramString4, paramString5, null);
  }
  
  public static String createPasswordHash(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, DigestCallback paramDigestCallback) {
    byte[] arrayOfByte;
    String str = null;
    try {
      if (paramString3 == null) {
        arrayOfByte = paramString5.getBytes();
      } else {
        arrayOfByte = paramString5.getBytes(paramString3);
      } 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      log.error("charset " + paramString3 + " not found. Using platform default.", unsupportedEncodingException);
      arrayOfByte = paramString5.getBytes();
    } 
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(paramString1);
      if (paramDigestCallback != null)
        paramDigestCallback.preDigest(messageDigest); 
      messageDigest.update(arrayOfByte);
      if (paramDigestCallback != null)
        paramDigestCallback.postDigest(messageDigest); 
      byte[] arrayOfByte1 = messageDigest.digest();
      if (paramString2.equalsIgnoreCase("BASE64")) {
        str = encodeBase64(arrayOfByte1);
      } else if (paramString2.equalsIgnoreCase("HEX")) {
        str = encodeBase16(arrayOfByte1);
      } else {
        log.error("Unsupported hash encoding format " + paramString2);
      } 
    } catch (Exception exception) {
      log.error("Password hash calculation failed ", exception);
    } 
    return str;
  }
  
  public static String tob64(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte.toString();
  }
  
  public static byte[] fromb64(String paramString) throws NumberFormatException {
    return paramString.getBytes();
  }
  
  public static boolean hasUnlimitedCrypto() {
    boolean bool = false;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> clazz = classLoader.loadClass("javax.crypto.KeyGenerator");
      Class[] arrayOfClass1 = { String.class };
      Object[] arrayOfObject1 = { "Blowfish" };
      Method method1 = clazz.getDeclaredMethod("getInstance", arrayOfClass1);
      Object object = method1.invoke(null, arrayOfObject1);
      Class[] arrayOfClass2 = { int.class };
      Object[] arrayOfObject2 = { new Integer(256) };
      Method method2 = clazz.getDeclaredMethod("init", arrayOfClass2);
      method2.invoke(object, arrayOfObject2);
      bool = true;
    } catch (Throwable throwable) {
      log.debug("hasUnlimitedCrypto error", throwable);
    } 
    return bool;
  }
  
  public static Object createSecretKey(String paramString, Object paramObject) throws KeyException {
    Class[] arrayOfClass = { paramObject.getClass(), String.class };
    Object[] arrayOfObject = { paramObject, paramString };
    Object object = null;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> clazz = classLoader.loadClass("javax.crypto.spec.SecretKeySpec");
      Constructor<?> constructor = clazz.getDeclaredConstructor(arrayOfClass);
      object = constructor.newInstance(arrayOfObject);
    } catch (Exception exception) {
      throw new KeyException("Failed to create SecretKeySpec from session key, msg=" + exception.getMessage());
    } catch (Throwable throwable) {
      throw new KeyException("Unexpected exception during SecretKeySpec creation, msg=" + throwable.getMessage());
    } 
    return object;
  }
  
  public static Object createCipher(String paramString) throws GeneralSecurityException {
    return Cipher.getInstance(paramString);
  }
  
  public static Object createSealedObject(String paramString, Object paramObject, byte[] paramArrayOfbyte, Serializable paramSerializable) throws GeneralSecurityException {
    SealedObject sealedObject = null;
    try {
      Cipher cipher = Cipher.getInstance(paramString);
      SecretKey secretKey = (SecretKey)paramObject;
      if (paramArrayOfbyte != null) {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfbyte);
        cipher.init(1, secretKey, ivParameterSpec);
      } else {
        cipher.init(1, secretKey);
      } 
      sealedObject = new SealedObject(paramSerializable, cipher);
    } catch (GeneralSecurityException generalSecurityException) {
      throw generalSecurityException;
    } catch (Throwable throwable) {
      throw new GeneralSecurityException("Failed to create SealedObject, msg=" + throwable.getMessage());
    } 
    return sealedObject;
  }
  
  public static Object accessSealedObject(String paramString, Object paramObject1, byte[] paramArrayOfbyte, Object paramObject2) throws GeneralSecurityException {
    Object object = null;
    try {
      Cipher cipher = Cipher.getInstance(paramString);
      SecretKey secretKey = (SecretKey)paramObject1;
      if (paramArrayOfbyte != null) {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfbyte);
        cipher.init(2, secretKey, ivParameterSpec);
      } else {
        cipher.init(2, secretKey);
      } 
      SealedObject sealedObject = (SealedObject)paramObject2;
      object = sealedObject.getObject(cipher);
    } catch (GeneralSecurityException generalSecurityException) {
      throw generalSecurityException;
    } catch (Throwable throwable) {
      throw new GeneralSecurityException("Failed to access SealedObject, msg=" + throwable.getMessage());
    } 
    return object;
  }
}
