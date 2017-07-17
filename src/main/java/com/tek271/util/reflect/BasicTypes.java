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

package com.tek271.util.reflect;

import org.apache.commons.lang.ArrayUtils;

/**
 * Utility class that define primitive types, their wrappers, and other basic types
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class BasicTypes {

  private static class PrimtiveWrapper implements IPrimitiveWrapper {
    private Class primitive;
    private String primitiveDeclaration;
    private Class wrapper;
    private String wrapperDeclaration;
    private boolean isNumeric;

    PrimtiveWrapper(Class primitve, String primitiveDeclaration, 
         Class wrapper, String wrapperDeclaration, boolean isNumeric) {
      this.primitive= primitve;
      this.primitiveDeclaration= primitiveDeclaration;
      this.wrapper= wrapper;
      this.wrapperDeclaration= wrapperDeclaration;
      this.isNumeric= isNumeric;
    }
    
    public Class getPrimitive() { return primitive; }
    public String getPrimitiveDeclaration() { return primitiveDeclaration; }
    public Class getWrapper() { return wrapper; }
    public String getWrapperDeclaration() { return wrapperDeclaration; }
    public boolean isNumeric() { return isNumeric; }
  }
  
  public static final IPrimitiveWrapper[] PRIMITIVES_WRAPPERS = {
    new PrimtiveWrapper(boolean.class, "boolean", Boolean.class, "Boolean", false),
    new PrimtiveWrapper(byte.class, "byte", Byte.class, "Byte", true),
    new PrimtiveWrapper(char.class, "char", Character.class, "Character", false),
    new PrimtiveWrapper(short.class, "short", Short.class, "Short", true),
    new PrimtiveWrapper(int.class, "int", Integer.class, "Integer", true),
    new PrimtiveWrapper(long.class, "long", Long.class, "Long", true),
    new PrimtiveWrapper(float.class, "float", Float.class, "Float", true),
    new PrimtiveWrapper(double.class, "double", Double.class, "Double", true),
  };

  public static final Class[] PRIMITIVES = {
    boolean.class, byte.class, char.class,  short.class,
    int.class,     long.class, float.class, double.class,
  };
  
  public static final Class[] WRAPPERS = {
    Boolean.class, Byte.class, Character.class,  Short.class,
    Integer.class, Long.class, Float.class,      Double.class,
  };
  
  public static final Class[] PRIMITIVES_NUMERIC = {
    byte.class, short.class, int.class,     
    long.class, float.class, double.class,
  };
  
  public static final Class[] WRAPPERS_NUMERIC = {
    Byte.class, Short.class, Integer.class, 
    Long.class, Float.class, Double.class,
  };
  
/** Check if the given class is for a primitive data type */  
  public static boolean isPrimitive(Class clazz) {
    if (clazz==null) return false;
    return ArrayUtils.contains(PRIMITIVES, clazz);
  }
  
/** Check if the given type is for a primitive numeric type */  
  public static boolean isPrimitiveNumeric(Class clazz) {
    if (clazz==null) return false;
    for (int i=0,n=PRIMITIVES_WRAPPERS.length; i<n; i++) {
      IPrimitiveWrapper pw= PRIMITIVES_WRAPPERS[i];
      if (pw.isNumeric() && pw.getPrimitive()==clazz) return true;
    }
    return false;
  }
  
/** Check if the given class is for a wrapper data type */  
  public static boolean isWrapper(Class clazz) {
    if (clazz==null) return false;
    return ArrayUtils.contains(WRAPPERS, clazz);
  }

/** Check if the given object's class is for a wrapper data type */  
  public static boolean isWrapper(Object value) {
    if (value==null) return false;
    return isWrapper(value.getClass());
  }

  /** Check if the given class is for a wrapper data type */  
  public static boolean isWrapperNumeric(Class clazz) {
    if (clazz==null) return false;
    for (int i=0,n=PRIMITIVES_WRAPPERS.length; i<n; i++) {
      IPrimitiveWrapper pw= PRIMITIVES_WRAPPERS[i];
      if (pw.isNumeric() && pw.getWrapper()==clazz) return true;
    }
    return false;
  }
  
  /** Check if the given class is for a wrapper data type */  
  public static boolean isWrapperNumeric(Object value) {
    if (value==null) return false;
    return isWrapperNumeric(value.getClass());
  }
  
  
/** Check if the given class is for String.class */  
  public static boolean isString(Class clazz) {
    if (clazz==null) return false;
    return clazz.equals( String.class );
  }
  
/** Check if the given value's class is for String.class */  
  public static boolean isString(Object value) {
    if (value==null) return false;
    return isString(value.getClass());
  }
  
/** Get the primitive class which corresponds to the given wrapper */
  public static Class toPrimitive(Class wrapperClass) {
    if (wrapperClass==null) return null;
    for (int i=0, n=PRIMITIVES_WRAPPERS.length; i<n; i++) {
      if (wrapperClass==PRIMITIVES_WRAPPERS[i].getWrapper()) {
        return PRIMITIVES_WRAPPERS[i].getPrimitive();
      }
    }
    return null;
  }
  
  /** Get the wrapper class which corresponds to the given primitive */
  public static Class toWrapper(Class primitiveClass) {
    if (primitiveClass==null) return null;
    for (int i=0, n=PRIMITIVES_WRAPPERS.length; i<n; i++) {
      if (primitiveClass==PRIMITIVES_WRAPPERS[i].getPrimitive()) {
        return PRIMITIVES_WRAPPERS[i].getWrapper();
      }
    }
    return null;
  }
  
  
}
