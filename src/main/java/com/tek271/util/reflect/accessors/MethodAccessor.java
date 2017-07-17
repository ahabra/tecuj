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
import java.util.ArrayList;
import java.util.List;

import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.string.StringUtility;

public class MethodAccessor {

/**
 * Get an array of all methods of all scopes including methods of super classes,
 * where method name starts with the given prefix.
 * @param cls The class to get its methods.
 * @param prefix The prefix for method names, if null, then all methods are returned.
 * @return an array of Method objects.
 */  
  public static Method[] getAllMethods(Class cls, final String prefix) {
    List list= new ArrayList();
    while (cls != null) {
      // note: using getMethods() will not work because it returns only public methods 
      Method[] methods= cls.getDeclaredMethods();
      for (int i=0, n=methods.length; i<n; i++) {
        Method m= methods[i];
        if (prefix==null || m.getName().startsWith(prefix)) {
          list.add(m);
        }
      }
      cls= cls.getSuperclass();
    }
    int n= list.size();
    Method[] r= new Method[n];
    for (int i=0; i<n; i++) {
      r[i]= (Method) list.get(i);
    }
    return r;
  }
  
/**
 * Get an array of all methods of all scopes including methods of super classes.
 * @param cls The class to get its methods.
 * @return an array of Method objects.
 */  
  public static Method[] getAllMethods(Class cls) {
    return getAllMethods(cls, null);
  }
  
/**
 * Search an array of Method for the given method name and parameter types.
 * @param methods Array of methods to search
 * @param methodName name of method to find
 * @param parameterTypes ordered array of method parameters
 * @return -1 if not found.
 */
  public static int indexOfMethod(final Method[] methods, 
                                  final String methodName,
                                  final Class[] parameterTypes) {
    if (methods==null) throw new NullPointerException("methods is null");
    if (StringUtility.isBlank(methodName)) throw new NullPointerException("methods is null or empty");
    
    for (int i=0, n= methods.length; i<n; i++) {
      Method m= methods[i];
      if (! methodName.equals(m.getName())) continue;
      Class[] formal= m.getParameterTypes();
      boolean eq= isEqualArray(parameterTypes, formal);
      if (eq) return i;
    }
    return -1;
  }
  
  private static boolean isEqualArray(final Class[] actual, final Class[] formal) {
    if (actual==formal) return true;
    int n1= Math.max(ArrayUtilities.length(actual), 0);
    int n2= Math.max(ArrayUtilities.length(formal), 0);
    if (n1==0 && n2==0) return true;
    if (n1==0 && n2==0) return false;
    if (n1 != n2) return false;

    for (int i=0; i<n1; i++) {
      Class f= formal[i];
      Class a= actual[i];
      if (f==a) continue;
      if (!f.isPrimitive()) return false;
      // now f is primitive
      if (a.isPrimitive()) return false;
      // check if a is a wrapper class for f
      if (f==boolean.class && a==Boolean.class) continue;
      if (f==byte.class && a==Byte.class) continue;
      if (f==char.class && a==Character.class) continue;
      if (f==double.class && a==Double .class) continue;
      if (f==float.class && a==Float.class) continue;
      if (f==int.class && a==Integer.class) continue;
      if (f==long.class && a==Long.class) continue;
      if (f==short.class && a==Short.class) continue;
      return false;
    }
    return true;
  }
  
/** get an array that represents the type of each item in the args array */  
  public static Class[] getTypes(final Object[] args) {
    if (args==null) return null;
    int n= args.length;
    Class[] r= new Class[n];
    for (int i=0; i<n; i++) {
      if (args[i]==null) r[i]=null;
      else r[i]= args[i].getClass();
    }
    return r;
  }
  
/**
 * Invoke the given method on the given target object passing the given arguments.
 * @param target The object where the method is to be invoked.
 * @param method the method to be invoked
 * @param args an array of arguments to be passed to the method.
 * @return the value returned by the method, or null if error.
 */  
  public static Object invoke(final Object target, final Method method,
                              final Object[] args) {
    boolean isAccessible= method.isAccessible();
    if (!isAccessible) {
      method.setAccessible(true);
    }

    try {
      return method.invoke(target, args);
    } catch (Exception e) {
      throw new RuntimeException("Could not invoke " + method.getName()+ ". " + e.getMessage(), e);
    } finally {
      if (!isAccessible) {
        method.setAccessible(false);
      }
    }
  }
  
  public static Object invoke(final Object target, final Method[] methods,
                              final String methodName,
                              final Object[] args) {
    Class[] parameterTypes= getTypes(args);
    int i= indexOfMethod(methods, methodName, parameterTypes);
    if (i<0) return null;
    
    Method method= methods[i];
    return invoke(target, method, args);
  }
  
  public static Object invoke(final Object target, final String methodName,
                              final Object[] args) {
    Method[] methods= getAllMethods(target.getClass());
    return invoke(target, methods, methodName, args);
  }
  
  public static Object invoke(final Object target, final String methodName) {
    return invoke(target, methodName, null);
  }
  
  
}
