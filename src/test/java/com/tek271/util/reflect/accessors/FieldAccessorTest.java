package com.tek271.util.reflect.accessors;

import java.lang.reflect.Field;

import junit.framework.TestCase;

public class FieldAccessorTest extends TestCase {
  
  private static class MyTestParent {
    private boolean booleanField;
    String asString() { return "" + booleanField; }
  }
  
  private static class MyTest extends MyTestParent {
    public int intField;
    public String stringField;
  }
  
  public void testGetAllFields() {
    Field[] fields= FieldAccessor.getAllFields(MyTest.class);
    assertTrue( FieldAccessor.indexOfField(fields, "booleanField")>=0 );
    assertTrue( FieldAccessor.indexOfField(fields, "intField")>=0 );
  }
  
  public void testSetFieldValue() {
    MyTest mt= new MyTest();
    boolean r= FieldAccessor.setFieldValue(mt, "stringField", "hello");
    assertTrue(r);
    assertEquals("hello", mt.stringField);
  }

  public void testGetFieldValueObjectString() {
    MyTest mt= new MyTest();
    mt.intField= 11;
    mt.stringField= "world";
    Integer f= (Integer) FieldAccessor.getFieldValue(mt, "intField");
    assertEquals(mt.intField, f.intValue() );
    String s= (String) FieldAccessor.getFieldValue(mt, "stringField");
    assertEquals(mt.stringField, s);
  }
  
//  public void testGetPrivateField() {
//    MyTestParent mtp= new MyTestParent();
//    Boolean v= (Boolean) FieldAccessor.getFieldValue(mtp, "booleanField");
//    
//  }

}
