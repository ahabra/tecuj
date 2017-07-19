package com.tek271.util.security;

import junit.framework.*;
import com.tek271.util.string.StringUtility;

public class EncryptionTest extends TestCase {

	private static final String PASSWORD = "1234567Ab";

	public void testEncryption() throws Exception {
		checkEncDec("123456789012345678901234567890");
		checkEncDec("gdsalgfa ldq89bd a;jf ;j fh;fh");
		checkEncDec("");
  }

  private void checkEncDec(String val) throws Exception {
		String en= Encryption.encrypt(val, PASSWORD);
		String de= Encryption.decrypt(en, PASSWORD);
		assertEquals(val, de);
	}

}
