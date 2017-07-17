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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.tek271.util.collections.CollectionUtility;
import com.tek271.util.string.StringUtility;
import org.apache.commons.collections4.CollectionUtils;

public class FieldAccessor {

  /**
   * Get an array of all fields of all scopes including fields of super classes.
   * @param cls The class to get its fields.
   * @return an array of Field objects
   */
  public static Field[] getAllFields(Class cls) {
    List list= new ArrayList();
    while (cls != null) {
      Field[] f= cls.getDeclaredFields();
      CollectionUtils.addAll(list, f);
      cls= cls.getSuperclass();
    }
    int n= list.size();
    Field[] r= new Field[n];
    for (int i=0; i<n; i++) {
      r[i]= (Field) list.get(i);
    }
    return r;
  }
  
  /** Search an array of Field for the given field name. return -1 if not found */
  public static int indexOfField(final Field[] fields, final String fieldName) {
    if (fields==null) throw new NullPointerException("fields is null");
    if (StringUtility.isBlank(fieldName)) throw new NullPointerException("fieldName is null or empty");
    
    for (int i=0, n= fields.length; i<n; i++) {
      if (StringUtility.equals(fields[i].getName(), fieldName)) return i;
    }
    return -1;
  }

  /**
   * Set the value of the given field on the given target object. This method tries
   * a best-effort, if an exception is thrown, it will be ignored.
   * @return true if success, false if failed.
   */
  public static boolean setFieldValue(final Object target, final Field field, final Object value) {
    checkNulls(target, field);
    try {
      field.set(target, value);
      return true;
    } catch (Exception e) {
      // e.printStackTrace();
      return false;
    }
  }
  
  private static void checkNulls(final Object target, final Field field) {
    if (field==null) throw new NullPointerException("field is null");
    int mods= field.getModifiers();
//    if ( Modifier.isPrivate( mods ) ) {
//      throw new IllegalArgumentException("Cannot access a private field");
//    }
    if (target==null && !Modifier.isStatic(mods)) {
      throw new NullPointerException("target object is null with a non-static field");
    }
  }
  
  /**
   * Get the value of the given field on the given target object. This method tries
   * a best-effort, if an exception is thrown, it will be ignored.
   * @return the value of the field, or null if error.
   */
  public static Object getFieldValue(final Object target, final Field field) {
    checkNulls(target, field);
    try {
      return field.get(target);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Set the value of the field with the given name on the given target object. 
   * This method tries a best-effort, if an exception is thrown, it will be ignored.
   * @return true if success, false if failed.
   */
  public static boolean setFieldValue(final Object target, final Field[] fields, 
                                      final String fieldName, final Object value) {
    int i= indexOfField(fields, fieldName);
    if (i==-1) return false;

    return setFieldValue(target, fields[i], value);
  }

  /**
   * Get the value of the field with the given name on the given target object. 
   * This method tries a best-effort, if an exception is thrown, it will be ignored.
   * @return the value of the field, or null if error.
   */
  public static Object getFieldValue(final Object target, final Field[] fields, 
                                     final String fieldName) {
    int i = indexOfField(fields, fieldName);
    if (i == -1) return null;

    return getFieldValue(target, fields[i]);
  }
  
  /**
   * Set the value of the field with the given name on the given target object.
   * @param target the object to set its field
   * @param cls the class of the target object
   * @param fieldName name of field
   * @param value the new value
   * @return true if success, false if failed.
   */
  public static boolean setFieldValue(final Object target, final Class cls, 
                                      final String fieldName, final Object value) {
    if (cls==null) throw new NullPointerException("class is null");
    Field[] fields= getAllFields(cls);
    return setFieldValue(target, fields, fieldName, value);
  }
  
  /**
   * Get the value of the field with the given name on the given target object.
   * @param target the object to set its field
   * @param cls the class of the target object
   * @param fieldName name of field
   * @return the value of the field, or null if error.
   */
  public static Object getFieldValue(final Object target, final Class cls,
                                     final String fieldName) {
    if (cls == null) throw new NullPointerException("class is null");
    Field[] fields = getAllFields(cls);
    return getFieldValue(target, fields, fieldName);
  }
  
  /**
   * Set the value of the field with the given name on the given target object.
   * @param target the object to set its field
   * @param fieldName name of field
   * @param value the new value
   * @return true if success, false if failed.
   */
  public static boolean setFieldValue(final Object target,
                                      final String fieldName, final Object value) {
    Class cls= target.getClass();
    return setFieldValue(target, cls, fieldName, value);
  }
  
  /**
   * Get the value of the field with the given name on the given target object.
   * @param target the object to set its field
   * @param fieldName name of field
   * @return the value of the field, or null if error.
   */
  public static Object getFieldValue(final Object target, final String fieldName) {
    Class cls = target.getClass();
    return getFieldValue(target, cls, fieldName);
  }
    
}
