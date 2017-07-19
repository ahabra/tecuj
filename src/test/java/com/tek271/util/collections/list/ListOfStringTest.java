package com.tek271.util.collections.list;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import com.tek271.util.reflect.ReflectUtil;

public class ListOfStringTest extends TestCase {
  
  private static final String pTEXT=
    "life \n" +
    "is \n" +
    "a continous\n" +
    "battel";
  
  private static ListOfString createList() {
    return ListOfString.createFromString(pTEXT, "\n");
  }
  
  
  public void testClone() {
    ListOfString list= createList();
    list.valueEquator= "**";
    list.commentStartSingleLine= "//";
    ListOfString clon= (ListOfString) list.clone();
    
    assertNotNull(clon);
    assertEquals(list.size(), clon.size());
    assertEquals(list.toString(), clon.toString());
    assertEquals(list.valueEquator, clon.valueEquator);
    assertEquals(list.commentStartSingleLine, clon.commentStartSingleLine);
  }
  
  public void testReadFromTextFileInContext() throws IOException {
    ListOfString list= new ListOfString();
    list.readFromTextFileInContext("a.txt");
    assertTrue(list.size()>0);
  }
  
  public void testToMap() {
    ListOfString los= new ListOfString();
    los.add("n1", "v1");
    los.add("n2", "v2");
    los.add("n3", "v3");
    
    Map map= los.toMap();
    assertEquals(3, map.size());
    assertEquals("v1", map.get("n1"));
    assertEquals("v2", map.get("n2"));
    assertEquals("v3", map.get("n3"));
    
    Iterator it= map.entrySet().iterator();
    Entry e= (Entry) it.next();
    assertEquals("n1", e.getKey());
    assertEquals("v1", e.getValue());
    
    e= (Entry) it.next();
    assertEquals("n2", e.getKey());
    assertEquals("v2", e.getValue());

    e= (Entry) it.next();
    assertEquals("n3", e.getKey());
    assertEquals("v3", e.getValue());
  }
  
}
