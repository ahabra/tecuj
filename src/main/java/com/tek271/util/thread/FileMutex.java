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

package com.tek271.util.thread;

import java.io.*;
import com.tek271.util.io.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.log.*;
import com.tek271.util.exception.ExceptionUtil;

/**
 * <p>A mutual-exclusion-lock based on the existance of a file in the file system</p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class FileMutex extends AbstractMutex {
  private static final String pCLASS_NAME= FileMutex.class.getName();
  private String pFileName;
  private ILogger pLogger;

/**
 * Create a mutual-exclusion-lock based on the given file
 * @param aFileName String mutual-exclusion-lock file
 * @param aLogger ILogger Logger to be used if errors happen. If null, no errors will
 *        be logged.
 */
  public FileMutex(final String aFileName, final ILogger aLogger) {
    pFileName= aFileName;
    pLogger= aLogger;
  }

/**
 * Create a mutual-exclusion-lock based on the given file
 * @param aFileName String mutual-exclusion-lock file
 */
  public FileMutex(final String aFileName) {
    this(aFileName, null);
  }

  private void error(final String aMethod, final String aMsg, final Throwable aThrowable) {
    ExceptionUtil.error(pLogger, pCLASS_NAME, aMethod, aMsg, aThrowable);
  }

/**
 * get the value of the mutual-exclusion-lock
 * @return boolean true if mutual-exclusion-lock is raised (file exists), return false if
 * mutual-exclusion-lock is lowered (file does not exist)
 */
  public synchronized boolean getValue() {
    return FileIO.exists(pFileName);
  }

/**
 * setValue the value of the mutual-exclusion-lock
 * @param aIsRaised boolean if true
 * @return boolean true if the operation was succeful, false if not
 */
  public synchronized boolean setValue(boolean aIsRaised) {
    if (! aIsRaised) {
      FileIO.delete(pFileName);
      return true;
    }

    if (getValue()) return true;  // already exist

    try {
      FileIO.write(pFileName, StringUtility.EMPTY );
      return true;
    } catch (IOException e) {
      if (pLogger != null) {
        error("setValue", "Cannot delete logger file", e);
      }
      return false;
    }
  }  // setValue

}  // FileMutex
