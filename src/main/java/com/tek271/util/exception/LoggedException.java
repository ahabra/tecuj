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

package com.tek271.util.exception;

import com.tek271.util.log.ILogger;
import com.tek271.util.string.StringUtility;

/**
 * An exception that logs error messages, it will also log the stack trace.
 * <p>Copyright (c) 2004-2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class LoggedException extends Exception {

  public LoggedException(final ILogger aLogger,
                         final String aMessage) {
    super(aMessage);
    log(aLogger, aMessage);
  }

  public LoggedException(final ILogger aLogger) {
    super();
    log(aLogger, null);
  }

  private void log(final ILogger aLogger,
                   final String aMessage) {
    String msg= StringUtility.defaultString(aMessage);
    aLogger.log(ILogger.ERROR, msg, this);
  }

}  // LoggedException
