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
import java.io.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.exception.*;

/**
 * <p>A throwable that can have multiple stack traces.</p>
 * <p>Copyright (c) 2004  Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
class MultiThrowable extends Throwable {
  private ListOfString pStackTrace;

  public MultiThrowable(final String aMessage, final List aThrowableList) {
    super(aMessage);
    // Convert given Throwable list to a ListOfString
    pStackTrace= ExceptionUtil.asList(aThrowableList);
  }

  public void printStackTrace() {
    System.err.print(pStackTrace);
  }

  public void printStackTrace(PrintStream aStream) {
    for (int i=0, n= pStackTrace.size(); i<n; i++) {
      aStream.println(pStackTrace.getItem(i) );
    }
  }

  public void printStackTrace(PrintWriter aWriter) {
    for (int i=0, n= pStackTrace.size(); i<n; i++) {
      aWriter.println(pStackTrace.getItem(i) );
    }
  }


}  // MultiThrowable
