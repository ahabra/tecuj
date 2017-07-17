package com.tek271.util.io;

import junit.framework.TestCase;

public class FileIOTest extends TestCase {

  public void testPackageToPath() {
    String actual= FileIO.packageToPath(FileIOTest.class);
    String expected= "com/tek271/util/io/";
    assertEquals(expected, actual);
  }

}
