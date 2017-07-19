package com.tek271.util.reflect.accessors;


import java.lang.reflect.Method;

import junit.framework.TestCase;

public class MethodAccessorTest extends TestCase {
  
  private static class MyTestParent {
    private boolean m0() { return true;}
    public boolean m5() { return m0(); } 
  }
  
  private static class MyTest extends MyTestParent {
    public boolean equals() { return false; }
    public int m1() { return 11; }
    public String m2(int p1, boolean p2) {p1= p2==true? p1 : p1;  return "" + p1 *2; }
    public int add(int a, int b) { return a+b; }
  }

  public void testGetAllMethods() {
    Method[] methods= MethodAccessor.getAllMethods(MyTest.class);
    
    int expected= MyTest.class.getDeclaredMethods().length +
                  MyTestParent.class.getDeclaredMethods().length +
                  Object.class.getDeclaredMethods().length;
    assertEquals(expected, methods.length);
  }

  public void testIndexOfMethod() {
    Method[] methods= MethodAccessor.getAllMethods(MyTest.class);

    String methodName= "m2";
    Class[] parameterTypes= {int.class, boolean.class};
    
    int i= MethodAccessor.indexOfMethod(methods, methodName, parameterTypes);
    assertTrue(i>=0);
    Method m= methods[i];
    assertEquals(methodName, m.getName());
  }

  public void testInvokeNoParams() {
    MyTest mt= new MyTest();
    
    Integer r= (Integer) MethodAccessor.invoke(mt, "m1");
    assertNotNull(r);
    assertEquals(mt.m1(), r.intValue());
  }
  
  public void testInvokeWithParams() {
    MyTest mt= new MyTest();
    
    Object[] args= { new Integer(3), Boolean.TRUE};
    String r= (String) MethodAccessor.invoke(mt, "m2", args);
    assertNotNull(r);
    assertEquals(mt.m2(3, true), r);
    
    
    args = new Object[] { new Integer(3), new Integer(5) };
    Integer ri= (Integer) MethodAccessor.invoke(mt, "add", args);
    assertNotNull(ri);
    assertEquals(mt.add(3,5), ri.intValue() );
  }
  
  
}
