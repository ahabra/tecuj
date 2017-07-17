package com.tek271.util.reflect;

import java.util.List;

import junit.framework.TestCase;

public class ReflectUtilTest extends TestCase {

  public void testGetPackageDirectoryOfClass() {
    String dir= ReflectUtil.getPackageDirectoryOfClass(String.class);
    assertEquals("java/lang", dir);
  }

  public void testGetSuperclasses() {
    List supers= ReflectUtil.getSuperclasses(String.class);
    assertEquals(1, supers.size());
  }
  
}
