package com.tek271.util.collections.set;

import java.util.Set;

import junit.framework.TestCase;

public class SetUtilityTest extends TestCase {

  public void testToSet() {
    String[] array= { "a", "b", "c", "c" };
    
    Set actual= SetUtility.toSet(array);
    assertEquals(3, actual.size());
  }

}
