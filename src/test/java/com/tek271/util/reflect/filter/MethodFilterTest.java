package com.tek271.util.reflect.filter;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class MethodFilterTest extends TestCase {
  private MethodFilter methodFilter= new MethodFilter();

  public static class C1 {
    public void m10() {}
  }
  
  public static class C2 extends C1 {
    public void m20() {}
  }
  
  public void testExcludeSuperClasses() {
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C2.class);
    assertEquals(1, methods.length);
    assertEquals("m20", methods[0].getName());
    
    methodFilter.excludeSuperClasses= false;
    methods= methodFilter.getMethods(C2.class);
    assertTrue( methods.length > 1);
  }

  public void testAddExcludedSuperClass() {
    methodFilter.excludeSuperClasses= false;
    Method[] methods= methodFilter.getMethods(C2.class);
    assertTrue(methods.length > 2);

    methodFilter.addExcludedSuperClass(Object.class);
    methods= methodFilter.getMethods(C2.class);
    assertEquals(2, methods.length);
  }
  
  public static class C3 extends C2 {
    public void m30() {}
    public void m10() {}
  }
  
  public void testOverridden() {
    methodFilter.excludeOverridden= true;
    methodFilter.addExcludedSuperClass(Object.class);
    Method[] methods= methodFilter.getMethods(C3.class);
    assertEquals(3, methods.length);
  }
  
  public static class C4 {
    private void m40() {}
    protected void m41() { m40(); }
  }
  
  public void testPrivate() {
    methodFilter.excludePrivate= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C4.class);
    assertEquals(1, methods.length);
    assertEquals("m41", methods[0].getName());
  }
  
  public void testProtected() {
    methodFilter.excludeProtected= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C4.class);
    assertEquals(1, methods.length);
    assertEquals("m40", methods[0].getName());
  }
  
  public static class C5 {
    void m50() {}
    public void m51() {}
  }
  
  public void testDefaultScope() {
    methodFilter.excludeDefaultScope= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C5.class);
    assertEquals(1, methods.length);
    assertEquals("m51", methods[0].getName());
  }
  
  public void testPublic() {
    methodFilter.excludePublic= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C5.class);
    assertEquals(1, methods.length);
    assertEquals("m50", methods[0].getName());
  }
  
  public abstract static class C6 {
    abstract void m60();
    public void m61() {}
  }
  
  public void testAbstract() {
    methodFilter.excludeAbstract= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C6.class);
    assertEquals(1, methods.length);
    assertEquals("m61", methods[0].getName());
  }

  public static class C7 {
    final void m70() {}
    static void m71() {}
  }

  public void testFinal() {
    methodFilter.excludeFinal= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C7.class);
    assertEquals(1, methods.length);
  }

  public void testStatic() {
    methodFilter.excludeStatic= true;
    methodFilter.excludeSuperClasses= true;
    Method[] methods= methodFilter.getMethods(C7.class);
    assertEquals(1, methods.length);
  }
  
  public class C8 {
    void setName() {}
    void doName() {}
  }
  
  public void testNamePattern() {
    methodFilter.excludeSuperClasses= true;
    methodFilter.excludeNamePattern= "set.*";
    Method[] methods= methodFilter.getMethods(C8.class);
    assertEquals(1, methods.length);

    methodFilter.excludeNamePattern= ".*Name";
    methods= methodFilter.getMethods(C8.class);
    assertEquals(0, methods.length);
  }
  
  public class C9 {
    void m90() {}
    int m91() { return 0; }
  }
  
  public void testReturnType() {
    methodFilter.excludeSuperClasses= true;
    methodFilter.addExcludedReturnType(void.class);
    Method[] methods= methodFilter.getMethods(C9.class);
    assertEquals(1, methods.length);
    assertEquals("m91", methods[0].getName());
    
    methodFilter.addExcludedReturnType(int.class);
    methods= methodFilter.getMethods(C9.class);
    assertEquals(0, methods.length);
  }
  
}
