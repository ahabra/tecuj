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
package com.tek271.util.reflect.filter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.tek271.util.reflect.ReflectUtil;
import com.tek271.util.string.StringUtility;

/**
 * Provides a list of methods on a class filtered by different criteria.
 * To use the class, set the exclude properties, then call getMethods().
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class MethodFilter {
  public boolean excludeSuperClasses= false;
  public boolean excludeOverridden = false;
  public boolean excludePrivate= false;
  public boolean excludeProtected= false;
  public boolean excludeDefaultScope= false;
  public boolean excludePublic= false;
  public boolean excludeAbstract= false;
  public boolean excludeFinal= false;
  public boolean excludeStatic= false;
  
  public String excludeNamePattern;

  private Set excludedSuperClasses= new HashSet();
  private Set excludedReturnTypes= new HashSet();

  public void addExcludedSuperClass(Class excludedSuper) {
    excludedSuperClasses.add(excludedSuper);
  }

  public Set getExcludedSuperClasses() {
    return excludedSuperClasses;
  }
  
  public void addExcludedReturnType(Class excludedReturnType) {
    excludedReturnTypes.add(excludedReturnType);
  }
  
  public Set getExcludedReturnTypes() {
    return excludedReturnTypes;
  }
  
/**
 * Get the methods of the given type filtering out what was excluded in the
 * exclude properties.
 * @param type a Class object to get its methods.
 * @return an array of Method objects. If type==null, returns null.
 */  
  public Method[] getMethods(Class type) {
    if (type==null) return null;
    List list= excludeSuperClasses? getMethodsOfClass(type) : getMethodsOfClassAndSupers(type);
    checkOverridden(list);
    removeExcluded(list);
    
    Method[] m= new Method[list.size()];
    list.toArray(m);
    return m;
  }

/** Remove from the list methods which have been overridden by a subclass */
  private void checkOverridden(List list) {
    if (!excludeOverridden) return;
    
    Set signatures= new HashSet();
    for (Iterator i= list.iterator(); i.hasNext(); ) {
      Method method= (Method) i.next();
      MethodSignature ms= new MethodSignature(method);
      if (signatures.contains(ms)) {
        i.remove();
      } else {
        signatures.add(ms);
      }
    }
  }
  
/** Get a list of all methods on the given type and its super classes */  
  private List getMethodsOfClassAndSupers(Class type) {
    List result= getMethodsOfClass(type);
    List supers= getSuperclasses(type);
    
    for (Iterator i=supers.iterator(); i.hasNext();) {
      Class spr= (Class) i.next();
      List superMethods= getMethodsOfClass(spr);
      if (CollectionUtils.isNotEmpty(superMethods)) {
        result.addAll(superMethods);
      }
    }
    return result;
  }

/** Get the superclasses excluding what's in excludedSuperClasses */ 
  private List getSuperclasses(Class type) {
    List supers= ReflectUtil.getSuperclasses(type);
    supers.removeAll(excludedSuperClasses);
    return supers;
  }
  
  private List getMethodsOfClass(Class type) {
    if (type==null) return null;
    List result= new LinkedList();

    Method[] methods= type.getDeclaredMethods();
    for (int i=0, n=methods.length; i<n; i++ ) {
        result.add(methods[i]);
    }
    return result;
  }
  
/** Remove excluded methods from the given list */  
  private void removeExcluded(List list) {
    for (Iterator i= list.iterator(); i.hasNext(); ) {
      Method method= (Method) i.next();
      if (isExcluded(method)) {
        i.remove();
      }
    }
  }

/** Check if the method is excluded */  
  private boolean isExcluded(Method method) {
    if (isExcludedByModifier(method)) return true; 
    if (StringUtility.isNotBlank(excludeNamePattern)) {
      String name= method.getName();
      if (name.matches(excludeNamePattern)) return true;
    }
    
    if (excludedReturnTypes.contains(method.getReturnType())) return true;
    return false;
  }

/** Check if the method is excluded based on it modifiers */  
  private boolean isExcludedByModifier(Method method) {
    int mod= method.getModifiers();
    if (excludePrivate &&  Modifier.isPrivate(mod)) return true;
    if (excludeProtected &&  Modifier.isProtected(mod)) return true;
    if (excludeDefaultScope &&  ReflectUtil.isDefaultScope(mod)) return true;
    if (excludePublic &&  Modifier.isPublic(mod)) return true;
    if (excludeAbstract &&  Modifier.isAbstract(mod)) return true;
    if (excludeFinal &&  Modifier.isFinal(mod)) return true;
    if (excludeStatic &&  Modifier.isStatic(mod)) return true;
    return false;
  }
  

}
