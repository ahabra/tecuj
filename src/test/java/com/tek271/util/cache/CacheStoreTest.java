package com.tek271.util.cache;

import junit.framework.*;
import com.tek271.util.thread.ThreadUtility;

public class CacheStoreTest extends TestCase {
  private static final int pMAX_SIZE= 4;
  private static final int pTTL= 1;
  private static final boolean pCREATE_IFNOT_FOUND= true;

  private ICacheStore cacheStore = null;

  protected void setUp() throws Exception {
    super.setUp();
    init();
  }

  protected void tearDown() throws Exception {
    cacheStore.close();
    cacheStore = null;
    StoreFactory.removeAllStores();
    super.tearDown();
  }

  private void init() {
    cacheStore= createStore();
  }

  private static ICacheStore createStore() {
    ICacheStore cs= StoreFactory.getStore("abdul", "store1", pMAX_SIZE, pTTL, pCREATE_IFNOT_FOUND);
    cs.put("1", "a");
    cs.put("2", "b");
    cs.put("3", "c");
    cs.put("4", "d");
    return cs;
  }

  public void testEquals() {
    assertTrue(cacheStore.equals(cacheStore));

    ICacheStore cs= createStore();
    assertTrue(cacheStore.equals(cs));
  }

  public void testSize() {
    assertEquals(pMAX_SIZE, cacheStore.size());
    cacheStore.put("5", "e");
    assertEquals(pMAX_SIZE, cacheStore.size());
  }

  public void testTTL() {
    ThreadUtility.sleepMillis(200);
    cacheStore.put("5", "e");
    ThreadUtility.sleepMillis(2000);
    cacheStore.put("6", "f");
    assertEquals(1, cacheStore.size());
  }

  public void testKey() {
    assertEquals(true, cacheStore.containsKey("1"));
    ThreadUtility.sleepMillis(2000);
    assertEquals(false, cacheStore.containsKey("1"));
  }

}
