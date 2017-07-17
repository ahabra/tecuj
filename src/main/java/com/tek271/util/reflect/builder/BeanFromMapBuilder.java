/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2006  Abdul Habra
www.tek271.com

This file is part of TECUJ.

TECUJ is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; version 2.

TECUJ is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with TECUJ; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

You can contact the author at ahabra@yahoo.com
*/

package com.tek271.util.reflect.builder;
import java.util.*;

/**
 * Dynamically creats a bean that implements a given interface and back its property
 * values using a Map object. An example which uses this class:
 * <pre>
import java.util.*;
import com.tek271.util.collections.map.MapUtility;

 ...

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

  public void testSetProperties() throws Exception {
    IPerson person= (IPerson) BeanFromMapBuilder.create(IPerson.class, MAP);

    System.out.println(person.getName());   // abdul
    System.out.println(person.getAge());    // 40
    System.out.println(person.getSsn());    // 123456789

    person.setName("aaa");
    System.out.println(person.getName() );  // aaa
  }
 * </pre>
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class BeanFromMapBuilder extends AbstractBeanBuilder {

  private class BeanValueAccessor implements AbstractBeanBuilder.IBeanValueAccessor {
    public Object getPropertyValue(Object aKey) {
      return pMap.get(aKey);
    }

    public void setPropertyValue(Object aKey, Object aValue) {
      pMap.put(aKey, aValue);
    }
  }

  private Map pMap;

/**
 * Creates a bean which implements the given interface, backed by the given map.
 * To get the bean call the getBean() method.
 */
  public BeanFromMapBuilder(Class aInterface, Map aValues) {
    pBeanValueAccessor= new BeanValueAccessor();
    setMap(aValues);
    super.createBean(aInterface);
  }

/**
 * Creates a bean wich implements the given interface.
 * To get the bean call the getBean() method.
 */
  public BeanFromMapBuilder(Class aInterface) {
    this(aInterface, new HashMap() );
  }

/** Set the map which will back the values used by the dynamic bean */
  public void setMap(Map aValues) {
    pMap= aValues;
  }

/**
 * A helpur method to create a bean which implements the given interface, backed by
 * the given map.
 * @param aInterface Class The class of the interface to implement.
 * @param aValues Map the map which backs the bean.
 * @return Object A bean which implements aInterface and is backed by aValues.
 */
  public static Object create(Class aInterface, Map aValues) {
    BeanFromMapBuilder bfm= new BeanFromMapBuilder(aInterface, aValues);
    return bfm.getBean();
  }

}  // BeanFromMap class
