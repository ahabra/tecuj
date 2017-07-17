/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2005  Abdul Habra
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

import com.tek271.util.exception.ExceptionUtil;

/**
 * <p>Provide info/debug/warn/error methods for a client class</p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ClassLogger {
  private ILogger pLogger;
  private String pClassName;

/**
 * Create a helper class for logging
 * @param aLogger ILogger The actual ILogger to be used
 * @param aClassName String Name of client class
 */
  public ClassLogger(final ILogger aLogger, final String aClassName) {
    pLogger= aLogger;
    pClassName= aClassName;
  }

/** Log a info msg */
  public void info(final String aMethod, final String aMsg) {
    ExceptionUtil.info(pLogger, pClassName, aMethod, aMsg);
  }

/** Log a debug msg */
  public void debug(final String aMethod, final String aMsg) {
    ExceptionUtil.debug(pLogger, pClassName, aMethod, aMsg);
  }

/** Log a warning msg */
  public void warn(final String aMethod, final String aMsg) {
    ExceptionUtil.warn(pLogger, pClassName, aMethod, aMsg);
  }

/** Log an error msg */
  public void error(final String aMethod, final String aMsg) {
    ExceptionUtil.error(pLogger, pClassName, aMethod, aMsg);
  }

/** Log an error msg */
  public void error(final String aMethod, final String aMsg, Throwable aThrowable) {
    ExceptionUtil.error(pLogger, pClassName, aMethod, aMsg, aThrowable);
  }

} // ClassLogger
