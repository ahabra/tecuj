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

import java.lang.reflect.*;

/**
 * Dynamically creates a bean that implements a given interface and back its property
 * values using the IBeanValueAccessor interface.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * <p>Company: </p>
 * @author Abdul Habra
 * @version 1.0
 */
public abstract class AbstractBeanBuilder {

/**
 * An interface that must be implemented by subclasses of AbstractBeanBuilder.
 * It provides the mechanism to get and set the bean property values using the
 * name of the property.
 */
  public interface IBeanValueAccessor {
    /** Get the value of the property */
    Object getPropertyValue(Object aKey);

    /** Set the value of the property */
    void setPropertyValue(Object aKey, Object aValue);
  }

  private Object pBean;
  protected IBeanValueAccessor pBeanValueAccessor;

  private class Handler implements InvocationHandler {
    public Object invoke(Object aProxy, Method aMethod, Object[] aArgs) {
      char[] chars= aMethod.getName().toCharArray();
      int length= chars.length;
      if (length < 3) return null;
      char c0= chars[0];
      char c1= chars[1];
      char c2= chars[2];

      if (c0=='i' && c1=='s') {
        chars[2]= Character.toLowerCase( c2 );
        String k = new String(chars, 2, length-2) ;
        return pBeanValueAccessor.getPropertyValue(k);
      }

      if (length < 4) return null;
      char c3= chars[3];
      if (c0=='g' && c1=='e' && c2=='t') {
        chars[3]= Character.toLowerCase( c3 );
        String k = new String(chars, 3, length-3) ;
        return pBeanValueAccessor.getPropertyValue(k);
      }

      if (c0=='s' && c1=='e' && c2=='t') {
        chars[3]= Character.toLowerCase( c3 );
        String k = new String(chars, 3, length-3) ;
        pBeanValueAccessor.setPropertyValue(k, aArgs[0]);
      }
      return null;
    }
  }  // class Handler

/** Get the bean which implements the interface */
  public Object getBean() {
    return pBean;
  }

/** Create a bean that implements the given interface. */
  protected void createBean(Class aInterface) {
    Class[] classes= { aInterface };
    Class proxyClass = Proxy.getProxyClass( aInterface.getClassLoader(), classes);
    Object[] handlers= { new Handler() };
    Class[] handlerClass= { InvocationHandler.class };
    try {
      Constructor constructor=proxyClass.getConstructor(handlerClass);
      pBean=constructor.newInstance(handlers);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }


}  // AbstractDynamicBean
