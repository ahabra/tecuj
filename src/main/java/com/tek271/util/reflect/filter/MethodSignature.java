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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates the signature of a method, ie, its name and parameters types.
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class MethodSignature {
  private String name;
  private Class[] parameterTypes;
  
  public MethodSignature(String name, Class[] parameterTypes) {
    this.name= name;
    this.parameterTypes= parameterTypes;
  }
  
  public MethodSignature(Method method) {
    this.name= method.getName();
    this.parameterTypes= method.getParameterTypes();
  }
  
/** Check if this MethodSignature is equal to the given obj */  
  public boolean equals(Object obj) {
    if (obj==null) return false;
    if (obj==this) return true;
    if (!(obj instanceof MethodSignature)) return false;
    MethodSignature another= (MethodSignature) obj;
    if (! StringUtils.equals(name, another.name)) return false;
    if (! ArrayUtils.isEquals(parameterTypes, another.parameterTypes)) return false;
    return true;
  }
  
  public int hashCode() {
    return name.hashCode();
  }
  
  public String toString() {
    return name + ArrayUtils.toString(parameterTypes);
  }

}
