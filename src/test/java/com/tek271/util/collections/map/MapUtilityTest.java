package com.tek271.util.collections.map;

import junit.framework.TestCase;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtilityTest extends TestCase {

  public void testToMap() {
    Object[] aKeys = {"name", "age", "ssn"};
    Object[] aValues = {"abdul", "40", "123456789"};

    Map map = MapUtility.toMap(aKeys, aValues);
    assertEquals(3, map.size());

    map= MapUtility.toMap(null, null);
    assertEquals(0, map.size());

    map= MapUtility.toMap(null, aValues);
    assertEquals(0, map.size());

    map= MapUtility.toMap(aKeys, null);
    assertEquals(3, map.size());

    map= MapUtility.toMap(aKeys, new Object[] {"abdul", "40"});
    assertEquals(3, map.size());
    assertNull( map.get("ssn") );
  }

  public void testToMap2DArray() {
    Map map;
    Object[][] kv= null;
    assertEquals(0, MapUtility.toMap(kv).size());

    kv = new Object[][] {
    };
    assertEquals(0, MapUtility.toMap(kv).size());

    kv = new Object[][] {
        {},
    };
    assertEquals(0, MapUtility.toMap(kv).size());

    kv = new Object[][] {
        {"name"},
    };
    map = MapUtility.toMap(kv);
    assertEquals(1, map.size());
    assertNull( map.get("name") );

    kv = new Object[][] {
        {"name", "abdul"},
    };
    map = MapUtility.toMap(kv);
    assertEquals(1, map.size());
    assertEquals("abdul", map.get("name") );

    kv = new Object[][] {
        {"name", "abdul", "junk"},
        {"age", "40"},
    };
    map = MapUtility.toMap(kv);
    assertEquals(2, map.size());
    assertEquals("abdul", map.get("name") );
    assertEquals("40", map.get("age"));
  }

  public void testLinkedHashMap() {
    LinkedHashMap map= new LinkedHashMap();
    map.put("1", "a");
    map.put("2", "b");
    for (Iterator i= map.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry entry= (Map.Entry) i.next();
      System.out.println(entry.getKey() + "= " + entry.getValue());
    }
    System.out.println("------------");
  }

  public void testLinkedMap() {
    LinkedMap map= new LinkedMap();
    map.put("1", "a");
    map.put("2", "b");
    for (MapIterator i = map.mapIterator(); i.hasNext(); ) {
      i.next();
      System.out.println(i.getKey() + "= " + i.getValue() );
    }

  }

}
