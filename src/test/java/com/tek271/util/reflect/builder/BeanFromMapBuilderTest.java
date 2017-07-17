package com.tek271.util.reflect.builder;

import java.util.*;
import junit.framework.*;
import com.tek271.util.collections.map.MapUtility;

public class BeanFromMapBuilderTest extends TestCase {

  public interface IPerson {
    String getName();
    void setName(String name);

    Integer getAge();
    void setAge(Integer age);

    String getSsn();
    void setSsn(String ssn);
  }

  private static final Object[][] ARRAY = {
      {"name", "abdul"},
      {"age", new Integer(40)},
      {"ssn", "123456789"},
  };
  public static final Map MAP= MapUtility.toMap(ARRAY);

  public void testCreateBean() throws Exception {
    AbstractBeanBuilder bean= new BeanFromMapBuilder(IPerson.class);
    IPerson person= (IPerson) bean.getBean();
    assertNotNull(person);
  }

  public void testSetProperties() throws Exception {
    IPerson person= (IPerson) BeanFromMapBuilder.create(IPerson.class, MAP);

    assertEquals("abdul", person.getName() );
    assertEquals(new Integer(40), person.getAge());
    assertEquals("123456789", person.getSsn());

    person.setName("aaa");
    assertEquals("aaa", person.getName() );
  }

}

