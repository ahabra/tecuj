package com.tek271.util.collections.map;

import junit.framework.*;
import com.tek271.util.thread.ThreadUtility;

public class OrderedMapOfTimestampTest extends TestCase {

  private Times times;
  private OrderedMapOfTimestamp map;

  private static class Times {
    final Long t1, t2, t3, t4;

    Times() {
      t1= new Long( System.currentTimeMillis() );
      ThreadUtility.sleepMillis(100);
      t2= new Long( System.currentTimeMillis() );
      ThreadUtility.sleepMillis(100);
      t3= new Long( System.currentTimeMillis() );
      ThreadUtility.sleepMillis(100);
      t4= new Long( System.currentTimeMillis() );
    }
  }

  public void setUp() {
    init();
  }

  public void init() {
    map= new OrderedMapOfTimestamp();
    times= new Times();
    map.put("1", times.t1 );
    map.put("2", times.t2 );
    map.put("3", times.t3 );
    map.put("4", times.t4 );
  }


  public void testGetFirstValue() {
    assertEquals(times.t1.longValue(), map.getFirstValue() );
  }

  public void testGetLastValue() {
    assertEquals(times.t4.longValue(), map.getLastValue() );
  }

  public void testRemoveAfter() {
    map.removeAfter( times.t2.longValue(), true );
    assertEquals(1, map.size());

    init();
    map.removeAfter( times.t2.longValue(), false );
    assertEquals(2, map.size());

    init();
    map.removeAfter( times.t1.longValue(), true );
    assertEquals(0, map.size());
  }

  public void testRemoveBefore() {
    map.removeBefore(times.t1.longValue(), false);
    assertEquals(4, map.size() );

    init();
    map.removeBefore(times.t4.longValue(), true);
    assertEquals(0, map.size() );

    init();
    map.removeBefore(times.t2.longValue(), false);
    assertEquals(3, map.size() );

    init();
    map.removeBefore(times.t2.longValue(), true);
    assertEquals(2, map.size() );

  }

  public void testRemoveFirst() {
    map.removeFirst();
    assertEquals(3, map.size() );
    assertEquals(times.t2.longValue(),  map.getFirstValue() );
  }

  public void testRemoveLast() {
    map.removeLast();
    assertEquals(3, map.size() );
  }

  public void testOrder() {
    map.put("2");
    String key= (String) map.lastKey();
    assertEquals("2", key);
  }

}
