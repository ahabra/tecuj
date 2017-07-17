package com.tek271.util.security;

import junit.framework.*;
import com.tek271.util.string.StringUtility;

public class EncryptionTest extends TestCase {

  public void testEncryption() throws Exception {
    String pwd= "1234567Ab";
    String val= "123456789012345678901234567890";
           val= "gdsalgfa ldq89bd a;jf ;j fh;fh";
           val= "";
    printString("Orginal", val);

    String en= Encryption.encrypt(val, pwd);
    printString("Encrypted", en);

    String de= Encryption.decrypt(en, pwd);
    printString("Decrypted", de);

    assertEquals(val, de);
  }

  private static void printString(String aName, String aValue) {
    System.out.println(aName + "(" + StringUtility.length(aValue) + "): " + aValue );
  }

}
