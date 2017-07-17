package com.tek271.util.reflect.filter;

import junit.framework.TestCase;

public class MethodSignatureTest extends TestCase {

  public void testEquals() {
    MethodSignature ms1= new MethodSignature("name1", null);
    MethodSignature ms2= new MethodSignature("name1", null);
    
    assertEquals(ms1, ms2);
  }
  
}
