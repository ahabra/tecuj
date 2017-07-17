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

/**
 * Implements the ILogger interface method log(level, msg).
 * This makes it easier for classes that implement the ILogger interface.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public abstract class AbstractLogger implements ILogger {

  public void log(final int aLevel, final String aMsg) {
    log(aLevel, aMsg, null);
  }


  private static final String pDEBUG= "DEBUG";
  private static final String pINFO= "INFO";
  private static final String pWARNING= "WARNING";
  private static final String pERROR= "ERROR";
  private static final String pUNKNOWN= "UnknownLevel";

/**
 * Map the debug level integer to a string.
 * @param aLevel int
 * @return String DEBUG, INFO, WARNING, ERROR, or UnknownLevel.
 */
  protected String mapLevel(final int aLevel) {
    switch (aLevel) {
      case ILogger.DEBUG: return pDEBUG;
      case ILogger.INFO: return pINFO;
      case ILogger.WARNING: return pWARNING;
      case ILogger.ERROR: return pERROR;
      default: return pUNKNOWN;
    }
  } // getLevel


}  // AbstractLogger
