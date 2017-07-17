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
 Interface of a generic logger. Used to create logging classes that implement this
 interface. The interface has only one method <code>log()</code> that
 is overloaded once. Some classes (E.g. <code>DbUtil</code>) in this
 library use this interface to log error message.
 <p>To use this interface you should implement it in one of your classes and pass
 an object of your class.
 * <p>Copyright (c) 2003 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 * <p><b>History</b><ol>
 * <li>2005.10.28: Added LEVELS and LEVELS_STRING array </li>
 * </ol></p>
 */
public interface ILogger {
  public static final int DEBUG = 1;
  public static final int INFO = 2;
  public static final int WARNING = 3;
  public static final int ERROR = 4;

  public static final int[] LEVELS= {DEBUG, INFO, WARNING, ERROR};
  public static final String[] LEVELS_STRING= {"DEBUG", "INFO", "WARNING", "ERROR"};

  public void log(int aLevel, String aMsg);
  public void log(int aLevel, String aMsg, Throwable aThrowable);

}  // ILogger
