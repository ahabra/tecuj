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

package com.tek271.util.log;

/**
 Simple logger that implements ILogger and writes to console. Usually used for debugging.
 <p>The static member <code>SimpleConsoleLogger.LOGGER</code> can
 be used instead of creating several objects of this class.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.1
 */
public class SimpleConsoleLogger extends AbstractLogger {
  public static final ILogger LOGGER = new SimpleConsoleLogger();

  private static final String pCOLON= ": ";

  synchronized public void log(final int aLevel,
                               final String aMsg,
                               final Throwable aThrowable) {
    System.err.println(mapLevel(aLevel) + pCOLON + aMsg);
    if (aThrowable != null)   aThrowable.printStackTrace(System.err);
  } // log

} // SimpleConsoleLogger
