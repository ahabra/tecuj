package com.tek271.util.reflect.accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.tek271.util.collections.CollectionUtility;

public class BeanAccessorTest extends TestCase {

  private static class MyTest {
    private int pAge;
    String name;
    
    public void setAge(int aAge) { pAge= aAge; }
    public int getAge() { return pAge; }
  }
  
  
  public void testSet() {
    MyTest mt= new MyTest();
    
    BeanAccessor.set(mt, "age", new Integer(55));
    assertEquals(55, mt.getAge() );
  }

  public void testGet() {
    MyTest mt= new MyTest();
    mt.setAge(20);
    Integer age= (Integer) BeanAccessor.get(mt, "age");
    assertEquals(mt.getAge() , age.intValue() );
  }
  
  public void testField() {
    MyTest mt= new MyTest();
    String abdul= "Abdul";
    
    BeanAccessor.set(mt, "name", abdul);
    assertEquals(abdul, mt.name);
    
    String name= (String)BeanAccessor.get(mt, "name");
    assertEquals(abdul, name);
  }
  
  public void testMap() {
    MyTest mt= new MyTest();
    String name= "Abdul";
    Integer age= new Integer(70);
    
    Map map= new HashMap();
    map.put("name", name);
    map.put("age", age);
    
    BeanAccessor.set(mt, map);
    assertEquals(name, mt.name);
    assertEquals(age.intValue(), mt.getAge());
  }

  public void testGetMap() {
    MyTest mt= new MyTest();
    mt.setAge(70);
    mt.name= "Abdul";
    String[] props= {"age", "name", "notThere"};
    Map map= BeanAccessor.get(mt, props);
    
    assertNotNull(map);
    assertEquals(props.length, map.size());
    assertEquals(mt.name, map.get("name") );
    assertEquals(mt.getAge(), ((Integer) map.get("age")).intValue() );
  }

  public void testGetMapCollection() {
    MyTest mt= new MyTest();
    mt.setAge(70);
    mt.name= "Abdul";
    String[] props= {"age", "name"};
    List list= new ArrayList();
    CollectionUtility.addAll(list, props);
    
    Map map= BeanAccessor.get(mt, list);
    
    assertNotNull(map);
    assertEquals(2, map.size());
    assertEquals(mt.name, map.get("name") );
    assertEquals(mt.getAge(), ((Integer) map.get("age")).intValue() );
  }
  
}
