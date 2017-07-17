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

package com.tek271.util.log;

import org.apache.commons.logging.Log;
/**
 * <p>Implements the ILogger interface using the commons logging logger.</p>
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class CommonsLoggingAdapter extends AbstractLogger {
  private Log pLog;

/**
 * Creates an ILogger object which uses the given commons logging logger.
 * @param aLog Log the commons logging logger to be used.
 */
  public CommonsLoggingAdapter(final Log aLog) {
    pLog= aLog;
  }

  /**
   * Log using the commons logging logger
   * @param aLevel int Log levels: DEBUG, INFO, WARNING, ERROR as defined in ILogger
   * @param aMsg String message to be logged
   * @param aThrowable Throwable cause of
   */
  public void log(int aLevel, String aMsg, Throwable aThrowable) {
    switch(aLevel) {
      case ILogger.DEBUG: pLog.debug(aMsg, aThrowable); break;
      case ILogger.INFO: pLog.info(aMsg, aThrowable); break;
      case ILogger.WARNING: pLog.warn(aMsg, aThrowable); break;
      case ILogger.ERROR: pLog.error(aMsg, aThrowable); break;
      default:
        pLog.fatal("BUG: CommonsLoggingAdapter.log() is passed a bad level value (" +
                   aLevel + ") " + aMsg, aThrowable);
    }
  }

}
