package com.tek271.util.collections.array;

import java.util.ArrayList;
import java.util.Collection;

import com.tek271.util.collections.CollectionUtility;

import junit.framework.TestCase;

public class ArrayUtilitiesTest extends TestCase {

  public void testToArrayOfStringObjectArray() {
    Object[] objs= {"hello", "world", new Integer(44), Boolean.FALSE };
    String[] strs= ArrayUtilities.toArrayOfString(objs);
    assertNotNull(strs);
    assertEquals(objs.length, strs.length);
    assertEquals(objs[0], strs[0]);
    assertEquals(objs[1], strs[1]);
    assertEquals(objs[2].toString(), strs[2]);
    assertEquals(objs[3].toString(), strs[3]);
  }

  public void testToArrayOfStringCollection() {
    Object[] objs= {"hello", "world", new Integer(44), Boolean.FALSE };
    Collection col= new ArrayList();
    CollectionUtility.addAll(col, objs);
    String[] strs= ArrayUtilities.toArrayOfString(col);
    assertNotNull(strs);
    assertEquals(objs.length, strs.length);
    assertEquals(objs[0], strs[0]);
    assertEquals(objs[1], strs[1]);
    assertEquals(objs[2].toString(), strs[2]);
    assertEquals(objs[3].toString(), strs[3]);
  }

}
