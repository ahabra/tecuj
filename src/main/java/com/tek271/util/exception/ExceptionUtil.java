/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra
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

package com.tek271.util.exception;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.tek271.util.xml.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.log.*;
import com.tek271.util.collections.list.*;

/**
Generic static methods to manage exceptions, including:<ol>
 <li>Build consistent debug or error messages.</li>
 <li>Log a debug, warning, or error message to an ILogger object.</li>
 <li>Convert an exception stack trace to a string.</li>
 <li>Print an exception's stack trace to console, an <code>OutputStream</code>, or a
   <code>Writer</code> object. This is useful to  print the stack
   trace to a web page during development.</li>
</ol>
 * <p>Copyright (c) 2003 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 * <p><b>History</b><ol>
 * <li>2005.10.28: Added asList() </li>
 * </ol></p>
 */
public class ExceptionUtil {

  public static void main(String[] args) {
    SQLException ex1= new SQLException("Abdul's test-A");
    SQLException ex2= new SQLException("Abdul's test-B");
    ex1.setNextException(ex2);
    printSqlException(ex1);
  }

  private ExceptionUtil() {}

  /** Print aEx and all its sub-exceptions to System.err */
  public static void printSqlException(final SQLException aEx) {
    printSqlException(aEx, System.err);
  }

  /** Print aEx and all its sub-exceptions to aStream */
  public static void printSqlException(final SQLException aEx, final OutputStream aStream) {
    PrintWriter pw = new PrintWriter(aStream);
    printSqlExImpl(aEx, pw);
  }

  /** Print aEx and all its sub-exceptions to aWriter */
  public static void printSqlException(final SQLException aEx, final Writer aWriter) {
    PrintWriter pw = new PrintWriter(aWriter);
    printSqlExImpl(aEx, pw);
  } // printSQLException

  /** Print aEx and all its sub-exceptions to aWriter. Implementation. */
  private static void printSqlExImpl(SQLException aEx, PrintWriter aWriter) {
    while (aEx != null) {
      printExImpl(aEx, aWriter);
      aEx = aEx.getNextException();
    }
    aWriter.flush();
  } // printSqlExImpl

  /**
   * Print aException stack trace to aWriter. Will not flush.
   * @param aException Its stack trace will be printed. If null, no action.
   * @param aWriter The device to write to. If null, no action.
   */
  private static void printExImpl(final Throwable aException,
                                  final PrintWriter aWriter) {
    if (aWriter==null) return;
    if (aException==null) return;
    aWriter.println(aException.getMessage());
    aException.printStackTrace(aWriter);
  }  // printExImpl

  /** Print aEx to aStream */
  public static void printException(final Exception aEx, final OutputStream aStream) {
    PrintWriter pw = new PrintWriter(aStream);
    printExImpl(aEx, pw);
    pw.flush();
  }  // printException

  /** Print aEx to aWriter */
  public static void printException(final Exception aEx, final Writer aWriter) {
    PrintWriter pw = new PrintWriter(aWriter);
    printExImpl(aEx, pw);
    pw.flush();
  } // printException

  /** Print aEx to System.err */
  public static void printException(final Exception aEx) {
    printException(aEx, System.err);
  }

  public static void printThrowableList(final List aThrowableList, final Writer aWriter) {
    PrintWriter pw = new PrintWriter(aWriter);
    int count=1;
    for(Iterator i=aThrowableList.iterator(); i.hasNext();) {
      pw.println("-------- Exception #" + count++);
      printExImpl( (Throwable) i.next(), pw);
    }
    pw.flush();
  } // printThrowableList

  /** Get the stacktrace as a string */
  public static String asString(final Throwable aTh) {
    if (aTh==null) return null;
    StringWriter sw= new StringWriter(128);
    PrintWriter pw = new PrintWriter(sw);
    aTh.printStackTrace(pw);
    pw.flush();
    String r= sw.toString();
    pw.close();
    return r;
  } // asString

/** Convert a list of Throwable objects into a string */
  public static String asString(final List aThrowableList) {
    if (aThrowableList==null) return null;
    StringWriter sw= new StringWriter(128 * aThrowableList.size());
    printThrowableList(aThrowableList, sw);
    sw.flush();
    String r= sw.toString();
    try {
      sw.close();
    } catch (IOException ex) {
      r= ex.toString() + StringUtility.NEW_LINE + r;
    }
    return r;
  } // asString()

/** Append given throwable to a ListOfString */
  public static void appendToList(final Throwable aTh, final ListOfString aList) {
    String s= asString(aTh);
    aList.setText(s);
  }

/** Append given throwable list to a ListOfString */
  public static void appendToList(final List aThrowableList, final ListOfString aList) {
    String s= asString(aThrowableList);
    aList.setText(s);
  }

/** Convert given Throwable to a ListOfString */
  public static ListOfString asList(final Throwable aTh) {
    ListOfString r= new ListOfString();
    appendToList(aTh, r);
    return r;
  }

/** Convert given Throwable list to a ListOfString */
  public static ListOfString asList(final List aThrowableList) {
    ListOfString r= new ListOfString();
    appendToList(aThrowableList, r);
    return r;
  }

  private static final String pMETHOD_END = "(): ";

  private static String buildMsg(final String aClass,
                                 final String aMethod,
                                 final String aMsg,
                                 final String aXml) {
    StringBuffer buf= new StringBuffer(256);
    // buf.append(aPrefix).append(' ');
    buf.append(aClass).append('.').append(aMethod).append(pMETHOD_END);
    buf.append(aMsg);
    if (aXml!=null) {
      buf.append(StringUtility.NEW_LINE);
      XmlUtil.tagCData(buf, aXml, 4);
    }
    return buf.toString();
  }  // buildErrMsg


  /** Build an error message from the given parameters. Put aXml in a CData section. */
  public static String buildErrMsg(final String aClass,
                                   final String aMethod,
                                   final String aMsg,
                                   final String aXml) {
    return buildMsg(aClass, aMethod, aMsg, aXml);
  }  // buildErrMsg

  /** Build an error message from the given parameters */
  public static String buildErrMsg(final String aClass, final String aMethod, final String aMsg) {
    return buildMsg(aClass, aMethod, aMsg, null);
  }  // buildErrMsg

  /** Build a debug message from the given parameters. Put aXml in a CData section. */
  public static String buildDebugMsg(final String aClass,
                                     final String aMethod,
                                     final String aMsg,
                                     final String aXml) {
    return buildMsg(aClass, aMethod, aMsg, aXml);
  }  // buildErrMsg

  /** Build a debug message from the given parameters */
  public static String buildDebugMsg(final String aClass, final String aMethod, final String aMsg) {
    return buildMsg(aClass, aMethod, aMsg, null);
  }  // buildErrMsg


/**
 * Log an error msg.
 * @param aLogger An object that implemets ILogger interface.
 * @param aClassName The class where the error was generated.
 * @param aMethod The method where the error was generated.
 * @param aMsg The error msg to log.
 */
  public static void error(final ILogger aLogger,
                           final String aClassName,
                           final String aMethod,
                           final String aMsg) {
    aLogger.log(ILogger.ERROR, buildErrMsg(aClassName, aMethod, aMsg));
  }  //error

/**
 * Log an error msg with a long XML part that will be inside a CData section.
 * @param aLogger An object that implemets ILogger interface.
 * @param aClassName The class where the error was generated.
 * @param aMethod The method where the error was generated.
 * @param aMsg The error msg to log.
 * @param aXml
 */
  public static void error(final ILogger aLogger,
                           final String aClassName,
                           final String aMethod,
                           final String aMsg,
                           final String aXml) {
    aLogger.log(ILogger.ERROR, buildErrMsg(aClassName, aMethod, aMsg, aXml));
  }  //error

/**
 * Log an error msg with the stacktrace of a Throwable
 * @param aLogger An object that implemets ILogger interface.
 * @param aClassName The class where the error was generated.
 * @param aMethod The method where the error was generated.
 * @param aMsg The error msg to log.
 * @param aThrowable Will log its stack trace.
 */
  public static void error(final ILogger aLogger,
                           final String aClassName,
                           final String aMethod,
                           final String aMsg,
                           final Throwable aThrowable) {
    aLogger.log(ILogger.ERROR, buildErrMsg(aClassName, aMethod, aMsg), aThrowable);
  }  //error

/** Write a debug msg to the logger */
  public static void debug(final ILogger aLogger,
                           final String aClassName,
                           final String aMethod,
                           final String aMsg) {
    aLogger.log(ILogger.DEBUG, buildDebugMsg(aClassName, aMethod, aMsg));
  }  //debug

/** Write a debug msg to the logger */
  public static void debug(final ILogger aLogger,
                           final String aClassName,
                           final String aMethod,
                           final String aMsg,
                           final String aXml) {
    aLogger.log(ILogger.DEBUG, buildDebugMsg(aClassName, aMethod, aMsg, aXml));
  }  //error

/** Write a debug msg to the logger */
  public static void warn(final ILogger aLogger,
                          final String aClassName,
                          final String aMethod,
                          final String aMsg) {
    aLogger.log(ILogger.WARNING , buildDebugMsg(aClassName, aMethod, aMsg));
  }  //debug

/** Write an info msg to the logger */
  public static void info(final ILogger aLogger,
                          final String aClassName,
                          final String aMethod,
                          final String aMsg) {
    aLogger.log(ILogger.INFO, buildMsg(aClassName, aMethod, aMsg, null));
  }  // info

  
}  // ExceptionUtil
