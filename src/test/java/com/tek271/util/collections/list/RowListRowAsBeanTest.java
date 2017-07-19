package com.tek271.util.collections.list;

import junit.framework.*;
import java.util.Map;
import com.tek271.util.collections.map.MapUtility;

public class RowListRowAsBeanTest extends TestCase {

  public interface IPerson {
    String getName();
    void setName(String name);

    Integer getAge();
    void setAge(Integer age);

    String getSsn();
    void setSsn(String ssn);
  }

  private static final Object[][] PERSON1_ARRAY = {
      {"name", "abdul"},
      {"age", new Integer(40)},
      {"ssn", "123456789"},
  };
  private static final Map PERSON1_MAP= MapUtility.toMap(PERSON1_ARRAY);

  private static final Object[][] PERSON2_ARRAY = {
      {"name", "abdul2"},
      {"age", new Integer(42)},
      {"ssn", "222"},
  };
  private static final Map PERSON2_MAP= MapUtility.toMap(PERSON2_ARRAY);

  public void testRowAsBean() {
    RowList rlist= new RowList(PERSON1_MAP);
    rlist.add(PERSON2_MAP);
    rlist.setRowInterface( IPerson.class );
    IPerson person= (IPerson) rlist.getRowAsBean(0);

    assertEquals("abdul", person.getName() );
    assertEquals(new Integer(40), person.getAge());
    assertEquals("123456789", person.getSsn());

    person.setName("aaa");
    assertEquals("aaa", person.getName() );

    person= (IPerson) rlist.getRowAsBean(1, IPerson.class);
    assertEquals("abdul2", person.getName() );
    assertEquals(new Integer(42), person.getAge());
    assertEquals("222", person.getSsn());

  }


}
