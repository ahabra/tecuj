package com.tek271.util.collections.map;

import java.util.*;
import junit.framework.*;
import com.tek271.util.thread.ThreadUtility;

public class TimedLruMapTest extends TestCase {
  private static final long pTTL= 300;
  private static final int pMAX_SIZE= 4;
  private TimedLruMap pMap;

  protected void setUp() throws Exception {
    super.setUp();
    init();
  }

  protected void tearDown() throws Exception {
    pMap = null;
    super.tearDown();
  }

  private void init() {
    pMap= new TimedLruMap(pTTL, pMAX_SIZE);
    pMap.put("1", "a");
    pMap.put("2", "b");
    pMap.put("3", "c");
    pMap.put("4", "d");
  }

  public void testMaxSize() {
    pMap.put("5", "e");
    assertEquals(pMAX_SIZE, pMap.size() );
    assertEquals(null, pMap.get("1"));
  }

  public void testTTL() {
    ThreadUtility.sleepMillis(400);
    pMap.removeExpired();
    assertEquals(0, pMap.size());
    assertTrue(pMap.isEmpty());
  }

  public void testIterator() {
    ThreadUtility.sleepMillis(400);
    Iterator i= pMap.entrySet().iterator();
    assertEquals(false, i.hasNext());
  }

  public void testSizeAndTTL() {
    pMap.remove("4");
    ThreadUtility.sleepMillis(200);
    pMap.put("5", "e");
    ThreadUtility.sleepMillis(200);
    assertEquals(1, pMap.size());
  }

}
