package com.tek271.util.thread;

import junit.framework.TestCase;

public class OsExceutorTest extends TestCase {

  public void testDir() {
    OsExecutor e= new OsExecutor(true, true);
    e.run("dir");
    assertFalse( e.isError() );
  }
  
  public void testBad() {
    OsExecutor e= new OsExecutor(true, true);
    e.run("dir1_9ebad");
    assertTrue( e.isError() );
  }
  
  
}
