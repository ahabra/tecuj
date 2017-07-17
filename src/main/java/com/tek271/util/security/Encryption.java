package com.tek271.util.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

import com.tek271.util.string.StringUtility;

/**
 * A simple encryption program to encrypt and decrypt strings using a given password.
 * It helps encrypting password columns in db tables. Uses DES algorithm.
 * <p>Copyright (c) 2007 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Encryption {
  private static final String pCHAR_ENCODING= "UTF8";
  private static final String pALGORITHM= "DES";

  private Cipher pCipherEncryption;
  private Cipher pCipherDecryption;

/** Create an Encryption object using the given password */
  public Encryption(String aPassword) throws GeneralSecurityException {
    aPassword= StringUtility.defaultString(aPassword);
    SecretKey key= makeKey(aPassword);
    pCipherEncryption= createCipher(key, true);
    pCipherDecryption= createCipher(key, false);
  }

/** Encrypt the given value */
  public String encrypt(String aValue)
                throws GeneralSecurityException, UnsupportedEncodingException  {
    aValue= StringUtility.defaultString(aValue);
    byte[] ba= encrypt(pCipherEncryption, aValue);
    ba= Base64.encodeBase64(ba);
    return new String(ba);
  }

/** Decrypt the give value */
  public String decrypt(String aEncryptedValue)
                throws GeneralSecurityException, UnsupportedEncodingException {
    aEncryptedValue= StringUtility.defaultString(aEncryptedValue);
    byte[] ba= Base64.decodeBase64(aEncryptedValue.getBytes());
    ba= decrypt(pCipherDecryption, ba);
    return new String(ba, pCHAR_ENCODING);
  }

  private static SecretKey makeKey(final String aPassword) throws GeneralSecurityException {
    KeySpec keys= new DESKeySpec(aPassword.getBytes());
    SecretKeyFactory keyFactory= SecretKeyFactory.getInstance(pALGORITHM);
    return keyFactory.generateSecret(keys);
  }

  private static Cipher createCipher(final SecretKey aKey, final boolean aIsEncrypt)
                        throws GeneralSecurityException {
    Cipher cipher= Cipher.getInstance(pALGORITHM);
    int mode= aIsEncrypt? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
    cipher.init(mode, aKey);
    return cipher;
  }

  private static byte[] encrypt(final Cipher aCipher, final String aValue)
                 throws GeneralSecurityException, UnsupportedEncodingException {
    byte[] ba= aValue.getBytes(pCHAR_ENCODING);
    ba= aCipher.doFinal(ba);
    return ba;
  }

  private static byte[] decrypt(final Cipher aCipher, final byte[] aEncryptedValue)
                 throws GeneralSecurityException {
    byte[] ba= aCipher.doFinal(aEncryptedValue);
    return ba;
  }

/** Encrypt the given value using the given aPassword */
  public static final String encrypt(final String aValue, final String aPassword)
                      throws GeneralSecurityException, UnsupportedEncodingException  {
    Encryption en= new Encryption(aPassword);
    return en.encrypt(aValue);
  }

/** Decrypt the given encrypted value using the given aPassword */
  public static final String decrypt(final String aValue, final String aPassword)
                      throws GeneralSecurityException, UnsupportedEncodingException  {
    Encryption en= new Encryption(aPassword);
    return en.decrypt(aValue);
  }


}
