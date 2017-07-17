package com.tek271.util.reflect;

import junit.framework.TestCase;

public class BasicTypesTest extends TestCase {

  public void testIsPrimitive() {
    assertTrue( BasicTypes.isPrimitive(int.class) );
    assertFalse( BasicTypes.isPrimitive(Class.class) );
  }

  public void testIsWrapper() {
    assertTrue( BasicTypes.isWrapper(Boolean.class) );
    assertTrue( BasicTypes.isWrapper(Integer.class) );
    assertFalse( BasicTypes.isWrapper(Class.class) );
    assertTrue( BasicTypes.isWrapper( Boolean.TRUE ) );
    assertTrue( BasicTypes.isWrapper( Integer.valueOf(1) ) );
    assertFalse( BasicTypes.isWrapper( "1" ) );
  }

  public void testIsString() {
    assertTrue( BasicTypes.isString(String.class) );
    assertFalse( BasicTypes.isString(int.class) );
    assertTrue( BasicTypes.isString("1") );
    assertFalse( BasicTypes.isString( Boolean.FALSE ) );
  }

  public void testToPrimitive() {
    assertEquals(int.class, BasicTypes.toPrimitive(Integer.class));
    assertEquals(char.class, BasicTypes.toPrimitive(Character.class));
    assertNull(BasicTypes.toPrimitive(null));
    assertNull(BasicTypes.toPrimitive( String.class ));
  }
  
  public void testToWrapper() {
    assertEquals(Long.class, BasicTypes.toWrapper(long.class));
    assertNull( BasicTypes.toWrapper(null) );
    assertNull( BasicTypes.toWrapper(TestCase.class) );
  }
  
  public void testIsPrimitiveNumeric() {
    assertTrue( BasicTypes.isPrimitiveNumeric(int.class) );
    assertTrue( BasicTypes.isPrimitiveNumeric(double.class) );
    assertFalse( BasicTypes.isPrimitiveNumeric(char.class) );
    assertFalse( BasicTypes.isPrimitiveNumeric(Integer.class) );
  }
  
  public void testIsWrapperNumeric() {
    assertFalse( BasicTypes.isWrapperNumeric(null) );
    assertTrue( BasicTypes.isWrapperNumeric(Long.class) );
    assertTrue( BasicTypes.isWrapperNumeric(Float.class) );
    assertFalse( BasicTypes.isWrapperNumeric(int.class) );
    assertFalse( BasicTypes.isWrapperNumeric("") );
    assertTrue( BasicTypes.isWrapperNumeric(Integer.valueOf(1)) );
  }
}
