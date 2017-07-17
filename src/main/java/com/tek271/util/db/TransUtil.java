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

package com.tek271.util.db;

import java.sql.*;
import com.tek271.util.*;
import com.tek271.util.log.*;
import com.tek271.util.exception.ExceptionUtil;

/**
 * Database transactions utilities, including static methods for commit, rollback,
 * setAutoCommit, and a method that executes several db operations as one
 * transaction.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class TransUtil {
  private final static String pCLASS_NAME= "com.tek271.util.db.TransUtil";

/** Do not call this constructor. Allows extending the class. */
  public TransUtil() {}

/** Put an error message on the log and on System.err */
  private static void error(final ILogger aLogger, final String aMethod,
                            final String aMessage, final Exception aException) {
    ExceptionUtil.error(aLogger, pCLASS_NAME, aMethod, aMessage, aException);
  }

/**
 * Execute the code in aExecutable withing a db transaction. If aExecutable.execute()
 * returns true then commit, else rollback. When this method returns, the original
 * state of the connection's auto-commit flag is retained.
 * @param aConnection Connection An active db connection.
 * @param aLogger ILogger A logger to log errors.
 * @param aExecutable IExecutable The implementation of excute is provided by the caller.
 * @return boolean true if succesful, false if not.
 */
  public static boolean transaction(final Connection aConnection,
                                    final ILogger aLogger,
                                    final IExecutable aExecutable) {
    String method= "transaction";
    try {
      boolean orgAutoCommit= aConnection.getAutoCommit();
      aConnection.setAutoCommit(false);
      boolean r= aExecutable.execute();
      if (r) aConnection.commit();
      else aConnection.rollback();
      aConnection.setAutoCommit(orgAutoCommit);
      return r;
    } catch (SQLException e) {
      error(aLogger, method, "Controlling DB transaction commit or rolback failed.", e);
      return false;
    }
  } // transaction


/**
 * Sets the Auto Commit value. Errors are logged.
 * <br>author: Doug Estep
 * @return Returns true if successful; false if not.
 */
  public static boolean setAutoCommit(final ILogger aLogger,
                                      final Connection aConnection,
                                      final boolean aAutoCommit) {
    String method= "setAutoCommit";
    try {
      aConnection.setAutoCommit(aAutoCommit);
    } catch (Exception e) {
      error(aLogger, method, "Setting AutoCommit to " + aAutoCommit, e);
      return false;
    }
    return true;
  }  // setAutoCommit

/**
 * Sets the Auto Commit value to false.  Errors are logged.
 * <br>author: Doug Estep
 * @return Returns true if successful; false if not.
 */
  public static boolean setAutoCommit(final ILogger aLogger,
                                      final Connection aConnection) {
    return setAutoCommit(aLogger, aConnection, false);
  }

/**
 * Issues a commit against the database connection. Errors are logged.
 * <br>author: Doug Estep
 * @return Returns true if successful; false if not.
 */
  public static boolean commit(final ILogger aLogger, final Connection aConnection) {
    String method= "commit";
    try {
      aConnection.commit();
    } catch (Exception e) {
      error(aLogger, method, "Committing a transaction.", e);
      return false;
    }
    return true;
  }  // commit


/**
 * Issues a rollback against the database connection. Errors are logged.
 * <br>author: Doug Estep
 * @return Returns true if successful; false if not.
 */
  public static boolean rollback(final ILogger aLogger, final Connection aConnection) {
    String method= "rollback";
    try {
        aConnection.rollback();
    } catch (Exception e) {
      error(aLogger, method, "Rolling back a transaction.", e);
      return false;
    }
    return true;
  }  // rollback

/**
 * Commit or rollback transaction based on the value of aFlag
 * @param aFlag boolean if true the commit, else rollback
 * @return boolean true if successful; false if not.
 */
  public static boolean commitOrRollback(final ILogger aLogger,
                                         final Connection aConnection,
                                         final boolean aFlag ) {
    if (aFlag) return commit(aLogger, aConnection);
    return rollback(aLogger, aConnection);
  }


}  // TransUtil
