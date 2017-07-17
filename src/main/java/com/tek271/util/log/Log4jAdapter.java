/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004,2005  Abdul Habra
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

package com.tek271.util.log;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
import com.tek271.util.io.FileIO;

/**
 * <p>Implements the ILogger interface using Log4j </p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Log4jAdapter extends AbstractLogger {
  private final static String pCLASS_NAME= Log4jAdapter.class.getName();

/** The default name of the log4j configuration file */
  private final static String pLOG_CONFIG_FILE="tecuj.log4j.properties";
  private Logger pLog4j;

/** Create an object using the given properties file name */
  public Log4jAdapter(final String aPropertiesFileName) {
    pLog4j= Logger.getLogger(Log4jAdapter.class);
    PropertyConfigurator.configure(aPropertiesFileName);
  }

/** Create a default logger.  */
  public Log4jAdapter() {
    this(pLOG_CONFIG_FILE);
  }

/** Create an adapter for the given Log4j logger */
  public Log4jAdapter(Logger aLog4jLogger) {
    pLog4j= aLog4jLogger;
  }

/**
 * Create an object using the given input stream, useful with servlets.
 * @param aPropertiesInputStream InputStream
 * @param aIsCloseStream boolean If true, close the stream after reading it.
 * @throws IOException if the Properties.load() or InputStream.close() failed.
 */
  public Log4jAdapter(final InputStream aPropertiesInputStream,
                      final boolean aIsCloseStream) throws IOException {
    pLog4j= Logger.getLogger(Log4jAdapter.class);
    Properties prop= new Properties();
    try {
      prop.load(aPropertiesInputStream);
      PropertyConfigurator.configure(prop);
    } finally {
      if (aIsCloseStream) aPropertiesInputStream.close();
    }
  }  // Log4jAdapter

/** Create an object using the given input stream. useful with servlets */
  public Log4jAdapter(final InputStream aPropertiesInputStream) throws IOException {
    this(aPropertiesInputStream, false);
  }

/** Creates an object using the given URL. */ 
  public Log4jAdapter(final URL aConfigUrl) { 
    pLog4j = Logger.getLogger(Log4jAdapter.class); 
    PropertyConfigurator.configure(aConfigUrl);
  }

  public void log(final int aLevel, final String aMsg, final Throwable aThrowable) {
    Level lvl= mapLog4jLevel(aLevel);
    if (aThrowable==null) pLog4j.log(lvl, aMsg);
    else pLog4j.log(lvl, aMsg, aThrowable);
  }

/** Map the ILogger level to log4j level */
  private static Level mapLog4jLevel(final int aLevel) {
    switch (aLevel) {
      case ILogger.DEBUG: return Level.DEBUG;
      case ILogger.ERROR: return Level.ERROR;
      case ILogger.INFO: return Level.INFO;
      case ILogger.WARNING: return Level.WARN;
      default: return Level.OFF;
    }
  }  // mapLog4jLevel


/**
 * Create a Log4jAdapter instance using the given properties file name
 * @param aPropertiesFileName String path and name for the log4j properties file.
 *        The path should be relative to the current context class loader, not
 *        an absolute path. In a servlet, this is the WEB-INF/classes directory.
 * @return ILogger a Log4jAdapter instance. If an error happen, return null, and
 *         write error messages to console.
 */
  public static ILogger create(final String aPropertiesFileName) {
    String method= "create";
    ILogger r= SimpleConsoleLogger.LOGGER;
    ClassLogger clog= new ClassLogger(r, pCLASS_NAME);
    InputStream is= FileIO.readFileToStream(aPropertiesFileName);
    if (is==null) {
      clog.error(method, "Cannot find " + aPropertiesFileName);
      return null;
    }

    try {
      r=new Log4jAdapter(is, true);  // will close stream
    } catch (Exception e) {
      String msg= "Cannot create a Log4J adapter using " + aPropertiesFileName;
      clog.error(method, msg, e);
      return null;
    }
    return r;
  }  // create


}  // Log4jAdapter
