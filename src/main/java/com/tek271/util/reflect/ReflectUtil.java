/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2005  Abdul Habra
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

import java.lang.reflect.Modifier;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tek271.util.log.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.io.*;
import com.tek271.util.string.*;

/**
 * Utility methods that handle dynamic class introspections
 * <p>Copyright: (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ReflectUtil {

  public ReflectUtil() {}

  private static void error(final ILogger aLogger,
                            final String aErrMsg,
                            final Exception aException) {
    aLogger.log(ILogger.ERROR, aErrMsg, aException);
  }  // error

/**
 * Get the default system class loader.
 * @return ClassLoader
 */
  public static ClassLoader getSystemClassLoader() {
    return ClassLoader.getSystemClassLoader();
  }

/**
 * Get a class loader which loads from the given path or JAR file.
 * @param aLogger ILogger a Logger to log errors.
 * @param aPathOrJar String If ends with a slash then this is a path, otherwise it is
 *        a JAR file.
 * @return URLClassLoader Class loader
 */
  public static URLClassLoader getFileLoader(final ILogger aLogger,
                                             final String aPathOrJar) {
    URL url;
    try {
      url=new URL("file:" + aPathOrJar);
    } catch (MalformedURLException ex) {
      error(aLogger, aPathOrJar + " is not a valid directory or JAR file", ex);
      return null;
    }

    URL[] urlArray = new URL[]{ url };
    URLClassLoader loader = new URLClassLoader(urlArray);
    return loader;
  } // getFileLoader


  private static final String pERR_NOT_FOUND= "Cannot find class: ";

/**
 * Load a class using the given loader.
 * @param aLogger ILogger a Logger to log errors.
 * @param aClassName String The full qualified class name to be created.
 * @param aLoader ClassLoader Loader to load the class
 * @return Class The Class object of the loaded class. Null if error.
 */
  public static Class loadClass(final ILogger aLogger,
                                final String aClassName,
                                final ClassLoader aLoader) {
    try {
      return Class.forName(aClassName, true, aLoader);
    } catch (ClassNotFoundException ex) {
      error(aLogger, pERR_NOT_FOUND + aClassName, ex);
      return null;
    }
  }  // loadClass

/**
 * Load a class using the default system loader.
 * @param aLogger ILogger a Logger to log errors.
 * @param aClassName String The full qualified class name to be created.
 * @return Class The Class object of the loaded class. Null if error.
 */
  public static Class loadClass(final ILogger aLogger,
                                final String aClassName) {
    return loadClass(aLogger, aClassName,  getSystemClassLoader());
  }  // loadClass

  private static final String pERR_CONSTRUCT= "Failed to construct an object of class ";
  private static final String pERR_ACCESS= "Class or its no-param constructor not accessible: ";

/**
 * Load an object using the given class loader then create the object.
 * @param aLogger ILogger a Logger to log errors.
 * @param aClassName String The full qualified class name to be created.
 * @param aLoader ClassLoader Loader to load the class
 * @return Object The created object. return null if failed.
 */
  public static Object createObject(final ILogger aLogger,
                                    final String aClassName,
                                    final ClassLoader aLoader) {
    Class cls= loadClass(aLogger, aClassName, aLoader);
    if (cls==null) return null;

    try {
      return cls.newInstance();
    } catch (InstantiationException ex) {
      error(aLogger, pERR_CONSTRUCT + aClassName, ex);
    } catch (IllegalAccessException ex) {
      error(aLogger, pERR_ACCESS + aClassName, ex);
    }
    return null;
  }  // createObject

/**
 * Create an object of the given class name. Errors are logged.
 * @param aLogger a Logger to log errors.
 * @param aClassName The full qualified class name to be created.
 * @return The created object. return null if failed.
 */
  public static Object createObject(final ILogger aLogger,
                                    final String aClassName) {
    return createObject(aLogger, aClassName, getSystemClassLoader());
  }  // createObject

/**
 * Load and create an object
 * @param aLogger ILogger a Logger to log errors.
 * @param aPathOrJar String path to a directory which contains the class (if ends
 *        with a slash) or name of a JAR file.
 * @param aClassName String The full qualified class name to be created.
 * @return Object The created object. return null if failed.
 */
  public static Object loadAndCreateObject(final ILogger aLogger,
                                           final String aPathOrJar,
                                           final String aClassName) {
    ClassLoader loader= getFileLoader(aLogger, aPathOrJar);
    if (loader==null) return null;
    return createObject(aLogger, aClassName, loader);
  } // loadAndCreateObject

  /** Get the current system class loader */
  public static ClassLoader classLoader() {
    return Thread.currentThread().getContextClassLoader();
  }  // classLoader

  private static final String pRE_DOT_CLASS= ".*\\.class";

/** get a list of .class files in the given path */
  public static ListOfString getClassesInPath(String aPath) {
    aPath= FileIO.cleanPathEnd(aPath);
    boolean isIncludeFiles= true;
    boolean isIncludeDirs= false;
    boolean isListFullName= true;
    boolean isIncludeSubDirs= true;

    ListOfString r= FileIO.list(aPath, pRE_DOT_CLASS,
                                isIncludeFiles, isIncludeDirs,
                                isListFullName, isIncludeSubDirs);
    if (r==null) return null;

    // remove the starting path string and areplace slashes with dot
    int pathLen= StringUtility.length(aPath);

    for (int i=0, n=r.size(); i<n; i++) {
      String cls= r.getItem(i);
      cls= StringUtility.substring(cls, pathLen);
      cls= cls.replace('/', '.');
      r.set(i, cls);
    }

    return r;
  } // getClassesInPath

/**
 * Get the directory of a class according to its package, for example for the 
 * class java.lang.String the return value is java/lang/util 
 */
  public static String getPackageDirectoryOfClass(Class clazz) {
    if (clazz==null) { return StringUtils.EMPTY; }
    
    Package pack= clazz.getPackage();
    if (pack==null)  { return StringUtils.EMPTY; }
    
    String name=  pack.getName();
    if (StringUtils.isEmpty(name))   {  return StringUtils.EMPTY; }
    
    name= StringUtils.replaceChars(name, '.', '/');
    return name;
  }

  /** Get a list of the super classes of the given type */  
  public static List getSuperclasses(Class type) {
    if (type==null) return null;
    List list= new ArrayList();
    while (true) {
      type= type.getSuperclass();
      if (type==null) break;
      list.add(type);
    }
    return list;
  }

  public static boolean isDefaultScope(int modifiers) {
    if (Modifier.isPrivate(modifiers)) return false;
    if (Modifier.isProtected(modifiers)) return false;
    if (Modifier.isPublic(modifiers)) return false;
    return true;
  }
  
}  // ReflectUtil
