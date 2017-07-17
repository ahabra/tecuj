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

package com.tek271.util.log;

import java.util.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.exception.ExceptionUtil;

/**
 * An ILogger implementation that Decorates another ILogger by manitaining a list of
 * logged events. The list can be accessed at any time.
 * Remember that if you dump the list contents to some output device, you should
 * clear it afterwords.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ListLogger extends AbstractLogger {
  private ILogger pLogger;
  private List pList;

/** Create an adapter for ILogger, the adapter will maintain a list of logged events */
  public ListLogger(final ILogger aLogger) {
    pLogger= aLogger;
    pList= new ArrayList();
  }

/**
 * Log an event, this implements th ILogger interface, it adds the log event to a list
 * then log with the logger passed to the constructor of this class.
 */
  public void log(final int aLevel, final String aMsg, final Throwable aThrowable) {
    pList.add( new LogItem(aLevel, aMsg, aThrowable) );
    pLogger.log(aLevel, aMsg, aThrowable);
  } // log

/** get a list of LogItem objects */
  public List getList() {
    return pList;
  }

/** clear the log list */
  public void listClear() {
    pList.clear();
  }

/** get the log list's size */
  public int listSize() {
    return pList.size();
  }

/** Copy the log list into a ListOfString object */
  public ListOfString toListOfString(final boolean aIsIncludeStacktrace) {
    int n= pList.size();
    ListOfString r= new ListOfString(n);
    for (int i=0; i<n; i++) {
      LogItem item= (LogItem) pList.get(i);
      r.add( item.toString(aIsIncludeStacktrace) );
    }
    return r;
  }  // toListOfString

  public String toString(final boolean aIsIncludeStacktrace) {
    return toListOfString(aIsIncludeStacktrace).toString();
  }

  public String toString() {
    return toString(true);
  }

/** a log item */
  public class LogItem {
    public int level;
    public String message;
    public Throwable throwable;

    public LogItem() {}

    public LogItem(final int aLevel, final String aMsg, final Throwable aThrowable) {
      level= aLevel;
      message= aMsg;
      throwable= aThrowable;
    }

    public String toString(final boolean aIsIncludeStacktrace) {
      StringBuffer sb= new StringBuffer(128);
      sb.append( mapLevel(level) ).append(": ").append(message);
      if (throwable != null) {
        sb.append(StringUtility.NEW_LINE);
        if (aIsIncludeStacktrace) sb.append( ExceptionUtil.asString(throwable) );
        else sb.append( throwable.getMessage() );
      }
      return sb.toString();
    }

    public String toString() {
      return toString(true);
    }
  }  // LogItem

}  // ListLogger
