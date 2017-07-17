/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2008  Abdul Habra.
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
package com.tek271.util.reflect.accessors;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tek271.util.collections.array.ArrayUtilities;

public class BeanAccessor {
  
  private static String setter(final String propertyName) {
    return "set" + Character.toUpperCase(propertyName.charAt(0)) + 
           propertyName.substring(1);
  }
  
  private static String getter(final String propertyName) {
    return "get" + Character.toUpperCase(propertyName.charAt(0)) + 
           propertyName.substring(1);
  }
  
/**
 * Call a setter method that can take multiple arguments, when there is 
 * a single argument, a field update can be used.
 * @param target The object/bean to update its property.
 * @param propertyName Name of property.
 * @param args and array of arguments to be used with a setter method. If
 * this array contains a single item, the propertyName can refer to a field.
 * @return true if success, false if not.
 */  
  public static boolean set(final Object target, 
                            final String propertyName,
                            final Object[] args) {
    Method[] methods= MethodAccessor.getAllMethods(target.getClass());
    Class[] parameterTypes= MethodAccessor.getTypes(args);
    String methodName= setter(propertyName);
    int i= MethodAccessor.indexOfMethod(methods, methodName, parameterTypes);
    if (i>=0) {
      return MethodAccessor.invoke(target, methods[i], args) != null;
    }
    
    Object value= args==null? null : args[0];
    return FieldAccessor.setFieldValue(target, propertyName, value);
  }

/**
 * Update a property using either a setter or a field.
 * @param target The object/bean to update its property.
 * @param propertyName Name of property.
 * @param value The value to put in the property.
 * @return true if success, false if not.
 */  
  public static boolean set(final Object target, 
                            final String propertyName,
                            final Object value) {
    Object[] values= {value};
    return set(target, propertyName, values);
  }

/**
 * Set the properties -including setters and fields- of the target object
 * using the given map.
 * @param target The object/bean to update its properties.
 * @param map the key in the map will match a property name, and its value is
 *        used to update the property's value.
 */  
  public static void set(final Object target, final Map map) {
    Method[] methods= MethodAccessor.getAllMethods(target.getClass());

    for (Iterator it= map.entrySet().iterator(); it.hasNext();) {
      Map.Entry entry= (Map.Entry) it.next();
      String propertyName= (String) entry.getKey();
      Object value= entry.getValue();
      Class[] parameterTypes= null;
      if (value!= null) parameterTypes= new Class[] {value.getClass()};
      String methodName= setter(propertyName);
      int dx= MethodAccessor.indexOfMethod(methods, methodName, parameterTypes);
      if (dx>=0) {
        MethodAccessor.invoke(target, methods[dx], new Object[] {value});
      } else {
        FieldAccessor.setFieldValue(target, propertyName, value);
      }
    }
  }
  
/** 
 * Get the value of the given property (getter or field) from the given bean.
 * @param target The object/bean to get the value of its property.
 * @param propertyName Name of property. If it is a getter method, the method
 * is assumed to be without parameters.
 * @return The value of the property. Null if failed.
 */  
  public static Object get(final Object target, final String propertyName) {
    Method[] methods= MethodAccessor.getAllMethods(target.getClass());
    return get(target, propertyName, methods);
  }

/** 
 * Get the value of the given property (getter or field) from the given bean.
 * @param target The object/bean to get the value of its property.
 * @param propertyName Name of property. If it is a getter method, the method
 * is assumed to be without parameters.
 * @param methods Array of methods of the target.
 * @return The value of the property. Null if failed.
 */  
  public static Object get(final Object target, 
                           final String propertyName,
                           final Method[] methods) {
    String methodName= getter(propertyName);
    int i= MethodAccessor.indexOfMethod(methods, methodName, null);
    if (i>=0) {
      return MethodAccessor.invoke(target, methods[i], null);
    }
    return FieldAccessor.getFieldValue(target, propertyName);
  }
  
/**
 * Get the values of the properties of the given target as a map.
 * @param target The object/bean to get the value of its properties.
 * @param propertyNames An array of properties names.
 * @return A map of (propertyName, value) pairs.
 */  
  public static Map get(final Object target, String[] propertyNames) {
    Map map= new HashMap();
    Method[] methods= MethodAccessor.getAllMethods(target.getClass());

    for (int i=0, n=propertyNames.length; i<n; i++) {
      String propertyName= propertyNames[i];
      Object value= get(target, propertyName, methods);
      map.put(propertyName, value);
    }
    return map;
  }
  
/**
 * Get the values of the properties of the given target as a map.
 * @param target The object/bean to get the value of its properties.
 * @param propertyNames A collection of properties names.
 * @return A map of (propertyName, value) pairs.
 */  
  public static Map get(final Object target, Collection propertyNames) {
    String[] props= ArrayUtilities.toArrayOfString(propertyNames); 
    return get(target, props);
  }
  
}
