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

package com.tek271.util.db;

import java.sql.*;
import java.io.*;
import javax.naming.*;
import com.tek271.util.log.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.io.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.exception.ExceptionUtil;

/**
 Generic database library methods.<p>The motivation for creating this library:</p>
 <ol>
   <li>Simplify JDBC access, including handling exceptions and logging.</li>
   <li>Avoid adding a new abstraction or a framework that requires
   hours of learning or may impact performance.</li>
   <li>Do not try to remove SQL from the picture, just make it easier
   to issue SQL queries.</li>
   <li>If a feature is not supported by this library, the programmer
   can use her/his favorite method in conjunction with this library.</li>
 </ol>
 <p>All the methods are:</p>
 <ol>
   <li>Static.</li>
   <li>Take a parameter of type <code>com.tek271.util.log.ILogger</code> that allow 
       logging errors when errors are possible. You can use 
       <code>SimpleConsoleLogger.LOGGER</code> for logging to console.</li>
   <li>Handle and log exceptions so the user does not have to do try/catch.</li>
   <li>When possible, return a value indicating failure. (<i>usually
   false or null</i>)</li>
 </ol>
 <p>Some interesting features are:</p>
 <ol>
   <li>Overloaded <code>close()</code> to close Connection, Statement, and ResultSet.</li>
   <li>Given a resultset, close it, its Statement, and Connection using <code>closeAll()</code>.</li>
   <li>Open either a standard JDBC connection, or get a connection from
   a JNDI lookup, used typically in J2EE environments.</li>
   <li>Get a row from a ResultSet as either an array or as a List.</li>
   <li>Increment the value of a numeric column in a table.</li>
   <li>Get the next value of an Oracle sequencer. (This does not mean
   that you can use Oracle only with this library, but that this method
   is only valid on an Oracle database.).</li>
   <li>Several <code>readCellAsXXX()</code> methods
   that read a given column at a given row from a given table, where
   the value can be int, long, or String.</li>
   <li>Read a column from a table into a ListOfString.</li>
   <li>Read one row from a table into either an array or a ListOfString.</li>
   <li>Run a SELECT statement and put the result into a RowList object.</li>
   <li>Run a method in a transaction using the <code>IExecutable</code> interface.</li>
   <li>Update the value of a given column in a table.</li>
   <li>Overloaded <code>write()</code> and <code>writeAndGetCount()</code> methods that run
   write queries (insert, update, or delete).</li>
   <li>Insert or update blobs using the <code>writeLongString()</code> method.</li>
   <li>And more ...</li>
 </ol>

 <p><b>Example</b>:<br>
 The following example opens a db connection, reads a value from a
 table, then closes the db connection.</p>
 <pre>ILogger log= SimpleConsoleLogger.LOGGER;  <i>// log errors to console</i>
String driver= &quot;oracle.jdbc.driver.OracleDriver&quot;;
String url= &quot;jdbc:oracle:thin:@server1.tek271.com&quot;;
String user= &quot;coolDude&quot;;
String password= &quot;password&quot;;
Connection con= DbUtil.getConnectionJdbc(log, driver, url, user, password);
<b>if</b> (con==<b>null</b>) <b>return</b>; <i>     // error happened and logged</i>

String sql= &quot;select name from employee where id=5&quot;;
String name= DbUtil.readCellAsString(log, sql, con);
<b>if</b> (name==<b>null</b>) { 
&nbsp;&nbsp;<i>// error happened and logged</i>
&nbsp;&nbsp;DbUtil.close(log, con);
&nbsp;&nbsp;<b>return</b>;
}

System.out.println(&quot;Name is &quot; + name);
DbUtil.close(log, con);</pre>

 In a typical application you may put the code to open connection in a
 separate method. Note the simplification here over standard JDBC calls;
 you do not have to do <b>try/catch</b>, and that logging is done
 automatically.

 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra, Doug Estep.
 * @version 1.0
 */
public class DbUtil {
  private final static String pCLASS_NAME= "com.tek271.util.db.DbUtil";

  private final static String pTABLE_NAME = "aTableName";
  private final static String pCOLUMN_NAME = "aColumnName";
  private final static String pSQL_SELECT = "SELECT ";
  private final static String pSQL_FROM = " FROM ";
  private final static String pSQL_WHERE = "WHERE ";
  private final static String pSQL_UPDATE = "UPDATE ";
  private final static String pSQL_SET = " SET ";

/** Do not call this constructor. Allows extending the class. */
  public DbUtil() {}

// general methods

  /** Assert that aCondition is true **/
  private static void assertArg(final boolean aCondition,
                                final String aArgName,
                                final String aArgValue) {
    if (aCondition) return;
    throw new IllegalArgumentException("Invalid argument (" + aArgName + "=" +
                                       aArgValue + ")" );
  }  // assertArg

/** Put an error message on the log and on System.err */
  private static void error(final ILogger aLogger, final String aMethod,
                            final String aMessage, final Exception aException) {
    ExceptionUtil.error(aLogger, pCLASS_NAME, aMethod, aMessage, aException);
  }

  /**
   * Build a Select statement to read a column from a table
   * @param aTableName Name of table
   * @param aColumnName Name of column
   * @param aWhere The where clause
   * @return The SQL select statement
   */
  private static String buildSelectQuery(final String aTableName,
                                         final String aColumnName,
                                         final String aWhere) {
    assertArg(!StringUtility.isBlank(aTableName), pTABLE_NAME, aTableName);
    assertArg(!StringUtility.isBlank(aColumnName), pCOLUMN_NAME, aColumnName);
    StringBuffer buf = new StringBuffer(64);
    buf.append(pSQL_SELECT).append(aColumnName);
    buf.append(pSQL_FROM).append(aTableName);
    appendWhere(buf, aWhere);
    return buf.toString();
  }  // buildSelectQuery

  /** Append a where clause to a StringBuffer. */
  private static void appendWhere(final StringBuffer aBuf, final String aWhere) {
    if (StringUtility.isBlank(aWhere)) return;
    String w= StringUtility.defaultString(aWhere).trim();
    if (w.length()==0) return;
    aBuf.append(' ');
    if (! StringUtility.startsWithIgnoreCase(w, pSQL_WHERE))
      aBuf.append(pSQL_WHERE);
    aBuf.append(w);
  } // appendWhere

  /** Create an Update sql query to update one column */
  private static String buildUpdateQuery(final String aTableName,
                                         final String aColumnName,
                                         final String aColumnValue,
                                         final boolean aIsQuoted,
                                         final String aWhere) {
    assertArg(!StringUtility.isBlank(aTableName), pTABLE_NAME, aTableName);
    assertArg(!StringUtility.isBlank(aColumnName), pCOLUMN_NAME, aColumnName);
    SqlWrite q= new SqlWrite();
    q.setQueryType(SqlWrite.UPDATE);
    q.setTableName(aTableName);
    q.setWhere(aWhere);
    q.addColumn(aColumnName, aColumnValue, aIsQuoted);
    return q.getSql();
  }  // buildSelectQuery

  /**
   * Number of columns in a result set.
   * @param aResultSet Result set to inspect.
   * @return Number of columns, 0 if an error occur
   */
  public static int columnCount(final ResultSet aResultSet) {
    if (aResultSet==null) return 0;
    try {
      return aResultSet.getMetaData().getColumnCount();
    }
    catch (SQLException ex) {}
    return 0;
  } // columnCount

  /**
   * Extract the names of the columns in the given result set.
   * @param aResultSet Result set to inspect.
   * @return Names of columns as an array, null if an exception occur.
   */
  public static String[] columnNames(final ResultSet aResultSet) {
    if (aResultSet==null) return null;
    try {
      ResultSetMetaData md = aResultSet.getMetaData();
      String[] cn = new String[md.getColumnCount()];
      for (int i=0, n= cn.length; i<n; i++) {
        cn[i] = md.getColumnName(i+1);
//        cn[i] = md.getColumnLabel(i+1);
      }
      return cn;
    }
    catch (SQLException ex) {}  // intentional: no need for logging here
    return null;
  } // getColumnNames

  /**
   * Extract the names of the columns in the given result set.
   * @param aResultSet Result set to inspect.
   * @return Names of columns as a list, null if an exception occur.
   */
  public static ListOfString columnNamesAsList(final ResultSet aResultSet) {
    if (aResultSet==null) return null;
    String cn[]= columnNames(aResultSet);
    if (cn==null) return null;
    return new ListOfString(cn);
  } // columnNamesAsList

// open and close methods

/**
 * Create a standard JDBC connection, logging errors to aLogger.
 * @param aLogger A logger used when errors occur.
 * @param aDriver jdbc driver, e.g.: oracle.jdbc.driver.OracleDriver
 * @param aUrl Url to the db. e.g.: jdbc:oracle:thin:@server1.tek271.com
 * @param aUser A db user name. e.g.: coolDude
 * @param aPassword Password of user.
 * @return A standard db connection object, null if an error occur.
 */
  public static Connection getConnectionJdbc(final ILogger aLogger,
                                             final String aDriver,
                                             final String aUrl,
                                             final String aUser,
                                             final String aPassword) {
    java.util.Properties prop = new java.util.Properties();
    prop.put("user", aUser);
    prop.put("password", aPassword);
    try {
      Class.forName(aDriver);
      return DriverManager.getConnection(aUrl, prop);
    }
    catch (Exception ex) {
      error(aLogger, "getConnectionJdbc", "Failed to create db connection.", ex);
      return null;
    }
  } // getConnectionJdbc

/**
 * Create a standard JDBC connection, logging errors to aLogger.
 * @param aLogger A logger used when errors occur.
 * @param aConfigFileName The path to a properties file that contain the following
 * items: <ol>
 * <li><b>db.driver</b>: The database driver, e.g.: oracle.jdbc.driver.OracleDriver
 * <li><b>db.url</b>: Url to the db. e.g.: jdbc:oracle:thin:@server1.tek271.com
 * <li><b>db.user</b>: A db user name. e.g.: coolDude
 * <li><b>db.password</b>: Password of user.
 * </ol>
 * @return A standard db connection object, null if an error occur.
 */
  public static Connection getConnectionJdbc(final ILogger aLogger,
                                             final String aConfigFileName) {
    ConfigFile cf;
    try {
      cf=new ConfigFile(aConfigFileName);
    } catch (IOException e) {
      error(aLogger, "getConnectionJdbc", "Failed to read database configuration file.", e);
      return null;
    }

    String driver= cf.getValue("db.driver");
    String url= cf.getValue("db.url");
    String user= cf.getValue("db.user");
    String password= cf.getValue("db.password");
    return getConnectionJdbc(aLogger, driver, url, user, password);
  }  // getConnectionJdbc

  /**
   * Create a JNDI Jdbc connection, usually pooled, logging errors to aLogger.
   * @param aLogger A logger used when errors occur.
   * @param aJndiJdbcUrl The Jndi Jdbc connection string used to do a JNDI lookup.
   *   E.g.: weblogic.jdbc.jts.commercePool
   * @return A JDBC connection object, null if an error occur.
   */
  public static Connection getConnectionJndi(final ILogger aLogger,
                                             final String aJndiJdbcUrl) {
    Connection con;
    Context ctx;
    javax.sql.DataSource ds;
    try {
      ctx = new InitialContext();
      ds = (javax.sql.DataSource) ctx.lookup(aJndiJdbcUrl);
      con = ds.getConnection();
    }
    catch (Exception ex) {
      error(aLogger, "getConnectionJndi", "Failed to create JNDI db connection.", ex);
      con= null;
    }
    finally {
      ds= null;
      ctx= null;
    }
    return con;
  }  // getConnectionJndi

  /**
   * Create a statement based on the given connection, logging errors to aLogger.
   * @param aLogger A logger used when errors occur.
   * @param aConnection An established connection. If aConnection is null, this method
   * will return null.
   * @return A JDBC statement, null if an error occur.
   */
  public static Statement getStatement(final ILogger aLogger,
                                       final Connection aConnection) {
    if (aConnection==null) return null;
    try {
      return aConnection.createStatement();
    }
    catch (SQLException ex) {
      error(aLogger, "getStatement", "Failed.", ex);
      return null;
    }
  } // getStatement

/**
 *
 * Create a prepared statement based on the given connection, logging errors to aLogger.
 * @param aLogger A logger used when errors occur.
 * @param aConnection An established connection. If aConnection is null, this method
 * will return null.
 * @param aSql An sql statement with parameters.
 * @return A JDBC prepared statement, null if an error occur.
 */
  public static PreparedStatement getPreparedStatement(final ILogger aLogger,
                                                       final Connection aConnection,
                                                       final String aSql) {
    if (aConnection==null) return null;
    try {
      return aConnection.prepareStatement(aSql);
    }
    catch (SQLException ex) {
      error(aLogger, "getPreparedStatement", "Failed.", ex);
      return null;
    }
  } // getPreparedStatement

/**
 * Creates a PreparedStatement and sets the arguments. All errors are logged.
 * <br>author Doug Estep.
 * @param aConnection An active db connection.
 * @param aLogger a Logger to log errors.
 * @param aSql An sql statement with parameters.
 * @param aArgs An array of objects to set to the PreparedStatement.  The
 * PreparedStatement will be filled in in the order of the array. Client is responsible
 * for matching the argument array with the arguments in the SQL command.
 * @return A PreparedStatement ready to execute.
 */
  public static PreparedStatement getPreparedStatement(final ILogger aLogger,
                                                       final Connection aConnection,
                                                       final String aSql,
                                                       final Object[] aArgs) {
    PreparedStatement stm = getPreparedStatement(aLogger, aConnection, aSql);
    if (stm == null) return null;
    if (!populatePreparedStatement(aLogger, stm, aArgs)) return null;
    return stm;
  } // getPreparedStatement

/**
 * Sets the arguments to the PreparedStatement.  All errors are logged.
 * <br>author Doug Estep.
 * @param aLogger a Logger to log errors.
 * @param aStm The PreparedStatement to set.
 * @param aArgs An array of objects to set to the PreparedStatement.  The
 * PreparedStatement will be filled in in the order of the array. Client is responsible
 * for matching the argument array with the arguments in the SQL command.
 * @return Returns true if successful; false if not.
 */
  public static boolean populatePreparedStatement(final ILogger aLogger,
                                                  final PreparedStatement aStm,
                                                  final Object[] aArgs) {
    String method= "populatePreparedStatement";
    if (aStm == null) return false;
    try {
      for (int i = 0, n = aArgs.length; i < n; i++) {
        aStm.setObject(i + 1, aArgs[i]);
      }
    } catch (Exception e) {
      error(aLogger, method, "Setting prepared statement parameters", e);
      return false;
    }
    return true;
  } // populatePreparedStatement


  /** Close a result set, logging errors to aLogger. If success return true. */
  public static boolean close(final ILogger aLogger, final ResultSet aResultSet){
    if (aResultSet==null) return true;
    try {
      aResultSet.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "close", "ResultSet Failed.", ex);
      return false;
    }
  }  // close rs

  /** Close a statement, logging errors to aLogger. If success return true. */
  public static boolean close(final ILogger aLogger, final Statement aStatement){
    if (aStatement==null) return true;
    try {
      aStatement.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "close", "Statement Failed.", ex);
      return false;
    }
  }  // close statement

  /** Close a connection, logging errors to aLogger. If success return true. */
  public static boolean close(final ILogger aLogger, final Connection aConnection){
    if (aConnection==null) return true;
    try {
      aConnection.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "close", "Connection Failed.", ex);
      return false;
    }
  } // close connection

  /** Close resultset, statement, and connection. If success return true. */
  public static boolean closeAll(final ILogger aLogger, final ResultSet aResultSet){
    if (aResultSet==null) return true;
    try {
      Statement stm = aResultSet.getStatement();
      Connection con = stm.getConnection();
      aResultSet.close();
      stm.close();
      con.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "closeAll", "ResultSet, Statement, and Connection failed.", ex);
      return false;
    }
  }  // closeAll

  /** Close statement, and connection. If success return true. */
  public static boolean closeAll(final ILogger aLogger, final Statement aStatement){
    if (aStatement==null) return true;
    try {
      Connection con = aStatement.getConnection();
      aStatement.close();
      con.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "closeAll", "Statement, and Connection failed.", ex);
      return false;
    }
  }  // closeAll

  /** Close a result set and its statement, logging errors to aLogger. If success return true. */
  public static boolean closeRsAndStatement(final ILogger aLogger, final ResultSet aResultSet){
    if (aResultSet==null) return true;
    try {
      Statement stm = aResultSet.getStatement();
      aResultSet.close();
      stm.close();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "closeRsAndStatement", "Failed.", ex);
      return false;
    }
  }  // closeRsAndStatement

  /**
   * Execute an SQL statement that does not return a value, typically insert, update
   * or delete.
   * @param aLogger A logger used when errors occur.
   * @param aSql A valid SQL statement. If aSql is blank, nothing will happen and the
   * method returns false.
   * @param aStatement An open JDBC Statement. If null, nothing will happen and the
   * method returns false.
   * @return true if success, false if not.
   */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final Statement aStatement) {
    if (aStatement == null) return false;
    if (StringUtility.isBlank(aSql)) return false;
    try {
      aStatement.executeUpdate(aSql);
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "write", "Failed. Query=\n" + aSql, ex);
      return false;
    }
  }  // write

  /**
   * Execute an SQL statement that does not return a value, typically insert, update
   * or delete.
   * @param aLogger A logger used when errors occur.
   * @param aSql A valid SQL statement. If aSql is blank, nothing will happen and the
   * method returns false.
   * @param aConnection An open connection. If null, nothing will happen and the
   * method returns false.
   * @return true if success, false if not.
   */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final Connection aConnection) {
    if (aConnection==null) return false;
    Statement stm= getStatement(aLogger, aConnection);
    boolean r= write(aLogger, aSql, stm);
    close(aLogger, stm);
    return r;
  } // write

/**
 * Executes a PreparedStatement.  All errors are logged.
 * <br>author: Doug Estep, Abdul Habra
 * @param aLogger a Logger to log errors.
 * @param aStm A PreparedStatement ready to execute.
 * @return Returns the number of rows affected.  Returns -1 if errors occurred.
 */
  public static int write(final ILogger aLogger, final PreparedStatement aStm) {
    String method= "write";
    try {
      return aStm.executeUpdate();
    } catch (Exception e) {
      error(aLogger, method, e.toString(), e);
      return -1;
    }
  } // write

/**
 * Execute an SQL statement that does not return a value, typically insert, update
 * or delete.  This method uses a PreparedStatement to execute the SQL statement. All
 * errors are logged.
 * <br>author: Doug Estep
 * @param aLogger a Logger to log errors.
 * @param aSql An sql statement with parameters.
 * @param aArgs An array of objects to set to the PreparedStatement.  The
 * PreparedStatement will be filled in in the order of the array. Client is responsible
 * for matching the argument array with the arguments in the SQL command.
 * @param aConnection Connection An active db connection.
 * @return true if success, false if not.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final Object[] aArgs,
                              final Connection aConnection) {
    int rows = writeAndGetCount(aLogger, aSql, aArgs, aConnection);
    if (rows <1) return false;
    return true;
  }  // write


/**
 * Execute an SQL statement that does not return a value, typically insert, update
 * or delete, and return the number of effected rows.
 * @param aLogger A logger used when errors occur.
 * @param aSql A valid SQL statement. If aSql is blank, nothing will happen and the
 * method returns -1.
 * @param aStatement An open JDBC Statement. If null, nothing will happen and the
 * method returns -1.
 * @return -1 if error, or # of effected rows.
 */
  public static int writeAndGetCount(final ILogger aLogger,
                                     final String aSql,
                                     final Statement aStatement) {
    if (aStatement == null) return -1;
    if (StringUtility.isBlank(aSql)) return -1;
    try {
      return aStatement.executeUpdate(aSql);
    }
    catch (SQLException ex) {
      error(aLogger, "writeAndGetCount", "Failed. Query=\n" + aSql, ex);
      return -1;
    }
  }  // writeAndGetCount

/**
 * Execute an SQL statement that does not return a value, typically insert, update
 * or delete, and return the number of effected rows.
 * @param aLogger A logger used when errors occur.
 * @param aSql A valid SQL statement. If aSql is blank, nothing will happen and the
 * method returns -1.
 * @param aConnection An open connection. If null, nothing will happen and the
 * method returns -1.
 * @return -1 if error, or # of effected rows.
 */
  public static int writeAndGetCount(final ILogger aLogger,
                              final String aSql,
                              final Connection aConnection) {
    if (aConnection==null) return -1;
    Statement stm= getStatement(aLogger, aConnection);
    int r= writeAndGetCount(aLogger, aSql, stm);
    close(aLogger, stm);
    return r;
  } // writeAndGetCount

/**
 * Execute an SQL statement that does not return a value, typically insert, update
 * or delete.  This method uses a PreparedStatement to execute the SQL statement. All
 * errors are logged.
 * <br>author: Doug Estep
 * @param aLogger a Logger to log errors.
 * @param aSql An sql statement with parameters.
 * @param aArgs An array of objects to set to the PreparedStatement.  The
 * PreparedStatement will be filled in in the order of the array. Client is responsible
 * for matching the argument array with the arguments in the SQL command.
 * @param aConnection Connection An active db connection.
 * @return true if success, false if not.
 */
  public static int writeAndGetCount(final ILogger aLogger,
                                     final String aSql,
                                     final Object[] aArgs,
                                     final Connection aConnection) {
      PreparedStatement stm = getPreparedStatement(aLogger, aConnection, aSql, aArgs);
      if (stm == null) return -1;
      return write(aLogger, stm);
  }  // writeAndGetCount

  /**
   * Update the value of a given column on a given table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to update
   * @param aColumnName Column to update
   * @param aColumnValue New column's value
   * @param aIsQuoted Is the value quoted?
   * @param aWhere A where clause to reach the row.
   * @param aStatement An active jdbc statement
   * @return true if success, false if not.
   */
  public static boolean updateColumn(final ILogger aLogger,
                                     final String aTableName,
                                     final String aColumnName,
                                     final String aColumnValue,
                                     final boolean aIsQuoted,
                                     final String aWhere,
                                     final Statement aStatement) {
    String sql= buildUpdateQuery(aTableName, aColumnName, aColumnValue, aIsQuoted, aWhere);
    return write(aLogger, sql, aStatement);
  }  // updateColumn

  /**
   * Update the value of a given column on a given table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to update
   * @param aColumnName Column to update
   * @param aColumnValue New column's value
   * @param aIsQuoted Is the value quoted?
   * @param aWhere A where clause to reach the row.
   * @param aConnection An active jdbc Connection
   * @return true if success, false if not.
   */
  public static boolean updateColumn(final ILogger aLogger,
                                     final String aTableName,
                                     final String aColumnName,
                                     final String aColumnValue,
                                     final boolean aIsQuoted,
                                     final String aWhere,
                                     final Connection aConnection) {
    String sql= buildUpdateQuery(aTableName, aColumnName, aColumnValue, aIsQuoted, aWhere);
    return write(aLogger, sql, aConnection);
  }  // updateColumn

  /**
   * Increment the value of a column in a row in a db table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Name of table.
   * @param aColumnName Name of column.
   * @param aWhere Where clause to reach the row.
   * @param aIncrement How much to change the cell, can be +ve and -ve. If its value
   * is zero, nothing will happen, and true is returned.
   * @param aStatement An open JDBC Statement. If null, nothing will happen and the
   * method returns false.
   * @return true if success, false if not.
   */
  public static boolean incrementCell(final ILogger aLogger,
                                      final String aTableName,
                                      final String aColumnName,
                                      final String aWhere,
                                      final int aIncrement,
                                      final Statement aStatement) {
    if (aIncrement==0) return true;
    if (aStatement == null) return false;

    StringBuffer b= new StringBuffer(64);
    b.append(pSQL_UPDATE).append(aTableName);
    b.append(pSQL_SET);
    b.append(aColumnName).append('=');
    b.append(aColumnName);
    b.append(aIncrement>0 ? '+' : '-');
    b.append(Math.abs(aIncrement));
    appendWhere(b, aWhere);

    return write(aLogger, b.toString(), aStatement);
  }  // incrementCell

  /**
   * Increment the value of a column in a row in a db table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Name of table.
   * @param aColumnName Name of column.
   * @param aWhere Where clause to reach the row.
   * @param aIncrement How much to change the cell, can be +ve and -ve. If its value
   * is zero, nothing will happen, and true is returned.
   * @param aConnection An open connection. If null, nothing will happen and the
   * method returns false.
   * @return true if success, false if not.
   */
  public static boolean incrementCell(final ILogger aLogger,
                                      final String aTableName,
                                      final String aColumnName,
                                      final String aWhere,
                                      final int aIncrement,
                                      final Connection aConnection) {
    if (aIncrement==0) return true;
    if (aConnection==null) return false;
    Statement stm= getStatement(aLogger, aConnection);
    boolean r= incrementCell(aLogger, aTableName, aColumnName, aWhere, aIncrement, stm);
    close(aLogger, stm);
    return r;
  }  // incrementCell


  /**
   * Read a column from the current row in the given result set.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnName Name of column.
   * @return The value of the column, if the value is null an empty string is returned,
   * if an error occur return null.
   */
  public static String getColumnAsString(final ILogger aLogger,
                                         final ResultSet aResultSet,
                                         final String aColumnName) {
    try {
      String r= aResultSet.getString(aColumnName);
      return r==null? StringUtility.EMPTY : r;
    }
    catch (SQLException ex) {
      error(aLogger, "getColumnAsString", "Can not read column " + aColumnName, ex);
      return null;
    }
  }  // getColumnAsString

  /**
   * Read a column from the current row in the given result set.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnName Name of column.
   * @return The value of the column, if the value is null false is returned,
   * if an error occur return false.
   */
  public static boolean getColumnAsBoolean(final ILogger aLogger,
                                           final ResultSet aResultSet,
                                           final String aColumnName) {
    try {
      return aResultSet.getBoolean(aColumnName);
    }
    catch (SQLException ex) {
      error(aLogger, "getColumnAsBoolean", "Can not read column " + aColumnName, ex);
      return false;
    }
  } // getColumnAsBoolean

  /**
   * Read a column from the current row in the given result set.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnName Name of column.
   * @return The value of the column, if the value is null 0 is returned,
   * if an error occur return -1.
   */
  public static long getColumnAsLong(final ILogger aLogger,
                                     final ResultSet aResultSet,
                                     final String aColumnName) {
    try {
      return aResultSet.getLong(aColumnName);
    }
    catch (SQLException ex) {
      error(aLogger, "getColumnAsLong", "Can not read column " + aColumnName, ex);
      return -1;
    }
  } // getColumnAsLong

  /**
   * Read a column from the current row in the given result set.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnName Name of column.
   * @return The value of the column, if the value is null 0 is returned,
   * if an error occur return -1.
   */
  public static int getColumnAsInt(final ILogger aLogger,
                                   final ResultSet aResultSet,
                                   final String aColumnName) {
    try {
      return aResultSet.getInt(aColumnName);
    }
    catch (SQLException ex) {
      error(aLogger, "getColumnAsInt", "Can not read column " + aColumnName, ex);
      return -1;
    }
  } // getColumnAsInt

  /**
   * Read the current row in the resultset as a String array.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnCount Number of columns in the result set.
   * @param aDefault The default value to use when a column is null.
   * @return The current row in the result set as a String array, null if an error occurs.
   */
  public static String[] getRowAsArray(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aColumnCount,
                                       final String aDefault) {
    if (aResultSet==null) return null;
    String[] r= new String[aColumnCount];
    try {
      for (int i=0; i < aColumnCount; i++) {
        r[i] = aResultSet.getString(i + 1);
        if (r[i]==null) r[i]= aDefault;
      }
      return r;
    }
    catch (SQLException ex) {
      error(aLogger, "getRowAsArray", "Reading column from result set failed.", ex);
      return null;
    }
  } // getRowAsArray

  /**
   * Read the current row in the resultset as a String array.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aDefault The default value to use when a column is null.
   * @return The current row in the result set as a String array, null if an error occurs.
   */
  public static String[] getRowAsArray(final ILogger aLogger,
                                final ResultSet aResultSet,
                                final String aDefault) {
    int n=columnCount(aResultSet);
    if (n==0) return null;
    return getRowAsArray(aLogger, aResultSet, n, aDefault);
  }  // getRowAsArray

  /**
   * Read the current row in the resultset as a String array.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @return The current row in the result set as a String array,
   * null if an error occurs. When a column has a null value, it will be replaced by
   * an empty string.
   */
  public static String[] getRowAsArray(final ILogger aLogger,
                                final ResultSet aResultSet) {
    return getRowAsArray(aLogger, aResultSet, StringUtility.EMPTY);
  } // getRowAsArray

  /**
   * Read the current row in the resultset as a ListOfString.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aColumnCount Number of columns in the result set.
   * @param aDefault The default value to use when a column is null.
   * @return The current row in the result set as a String array, null if an error occurs.
   */
  public static ListOfString getRowAsList(final ILogger aLogger,
                                          final ResultSet aResultSet,
                                          final int aColumnCount,
                                          final String aDefault) {
    if (aResultSet==null) return null;
    ListOfString r= new ListOfString();
    String v;
    try {
      for (int i=0; i < aColumnCount; i++) {
        v= aResultSet.getString(i+1);
        if (v==null) v= aDefault;
        r.add(v);
      }
      return r;
    }
    catch (SQLException ex) {
      error(aLogger, "getRowAsList", "Reading column from result set failed.", ex);
      return null;
    }
  } // getRowAsList

  /**
   * Read the current row in the resultset as a ListOfString.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @param aDefault The default value to use when a column is null.
   * @return The current row in the result set as a String array, null if an error occurs.
   */
  public static ListOfString getRowAsList(final ILogger aLogger,
                                          final ResultSet aResultSet,
                                          final String aDefault) {
    int n=columnCount(aResultSet);
    if (n==0) return null;
    return getRowAsList(aLogger, aResultSet, n, aDefault);
  }  // getRowAsList


  /**
   * Read the current row in the resultset as a ListOfString.
   * @param aLogger A logger used when errors occur.
   * @param aResultSet ResultSet to read from.
   * @return The current row in the result set as a String array,
   * null if an error occurs. When a column has a null value, it will be replaced by
   * an empty string.
   */
  public static ListOfString getRowAsList(final ILogger aLogger,
                                          final ResultSet aResultSet) {
    return getRowAsList(aLogger, aResultSet, StringUtility.EMPTY);
  } // getRowAsList


/**
 * Execute a prepared statement that returns a resultSet
 * @param aLogger ILogger A logger used when errors occur.
 * @param aStatement PreparedStatement a populated prepared statement
 * @return ResultSet The read resultset, null if an error occur.
 */
  public static ResultSet read(final ILogger aLogger, final PreparedStatement aStatement) {
    try {
      return aStatement.executeQuery();
    } catch (SQLException ex) {
      error(aLogger, "read", "PreparedStatement read failed.", ex);
      return null;
    }
  }

  /**
   * Run an sql statement that returns a ResultSet.
   * @param aLogger A logger used when errors occur.
   * @param aSql An SQL statement, e.g. Select
   * @param aStatement A connected Jdbc Statement.
   * @return The read resultset, null if an error occur.
   */
  public static ResultSet read(final ILogger aLogger,
                               final String aSql,
                               final Statement aStatement){
    if (aStatement == null) return null;
    if (StringUtility.isBlank(aSql)) return null;
    try {
      return aStatement.executeQuery(aSql);
    }
    catch (SQLException ex) {
      error(aLogger, "read", "Query failed:\n" + aSql, ex);
      return null;
    }
  } // read

  /**
   * Run an sql statement that returns a ResultSet.
   * <p><b>Important</b>:User of this method must close the <b>Statement</b> associated with
   * the returned ResultSet when done using the ResultSet. The method
   * closeRsAndStatement(ResultSet) may be used.</p>
   * @param aLogger A logger used when errors occur.
   * @param aSql An SQL statement, e.g. Select
   * @param aConnection A connected Jdbc connection.
   * @return The read resultset, null if an error occur.
   */
  public static ResultSet read(final ILogger aLogger,
                               final String aSql,
                               final Connection aConnection){
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    return read(aLogger, aSql, stm);
  }  // read

/**
 * Convert a result set into a rowlist, logging errors if they occur.
 * @param aLogger ILogger
 * @param aResultSet ResultSet
 * @return DBRowList the data as a DBRowList, null if error.
 */
  public static DBRowList readRowList(final ILogger aLogger,
                                      final ResultSet aResultSet) {
    try {
      return new DBRowList(aResultSet);
    } catch (SQLException ex) {
      error(aLogger, "readRowList", "Failed to create DBRowList.", ex);
      return null;
    }
  }  // readRowList

  /**
   * Run an Sql query that returns a ResultSet and convert it to a DBRowList.
   * @param aLogger A logger used when errors occur.
   * @param aSql An SQL statement, e.g. Select
   * @param aStatement A connected Jdbc Statement.
   * @return The read DBRowList, null if an error occur.
   */
  public static DBRowList readRowList(final ILogger aLogger,
                                      final String aSql,
                                      final Statement aStatement) {
    ResultSet rs = read(aLogger, aSql, aStatement);
    if (rs==null) return null;

    DBRowList r= readRowList(aLogger, rs);
    close(aLogger, rs);
    return r;
  } // readRowList

  /**
   * Run an Sql query that returns a ResultSet and convert it to a DBRowList.
   * @param aLogger A logger used when errors occur.
   * @param aSql An SQL statement, e.g. Select
   * @param aConnection A connected Jdbc Connection.
   * @return The read DBRowList, null if an error occur.
   */
  public static DBRowList readRowList(final ILogger aLogger,
                                      final String aSql,
                                      final Connection aConnection) {
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    DBRowList r= readRowList(aLogger, aSql, stm);
    close(aLogger, stm);
    return r;
  } // readRowList

/**
* Run an Sql query formatted for a PreparedStatement, using the arguments
* contained within aArgs, get a ResultSet, and convert it to a DBRowList.
* <br>author: Doug Estep
* @param aLogger A logger used when errors occur.
* @param aSql An SQL statement, e.g. Select
* @param aArgs Arguments to supply to the PreparedStatement.
* @param aConnection A connected Jdbc Connection.
* @return The read DBRowList, null if an error occur.
*/
  public static DBRowList readRowList(final ILogger aLogger,
                                      final String aSql,
                                      final Object[] aArgs,
                                      final Connection aConnection) {
    PreparedStatement stm = getPreparedStatement(aLogger, aConnection, aSql);
    if (stm==null) return null;

    if (!populatePreparedStatement(aLogger, stm, aArgs)) {
      close(aLogger, stm);
      return null;
    }

    ResultSet rs=read(aLogger, stm);
    if (rs==null) {
      close(aLogger, stm);
      return null;
    }

    DBRowList r=DbUtil.readRowList(aLogger, rs);
    close(aLogger, rs);
    close(aLogger, stm);
    return r;
  }  // readRowList

/**
 * Read a single column from db and return its values as a list,
 * @param aLogger A logger used when errors occur.
 * @param aSql The sql query to run.
 * @param aStatement An active JDBC statement.
 * @return The column values as a ListOfString, where null values are replaced by
 * an emplty string, returns null if an error occurs.
 */
  public static ListOfString readColumn(final ILogger aLogger,
                                        final String aSql,
                                        final Statement aStatement) {
    if (aStatement==null) return null;
    ResultSet rs = read(aLogger, aSql, aStatement);
    if (rs==null) return null;

    ListOfString list = new ListOfString();
    try {
      while (rs.next()) {
        list.add( StringUtility.defaultString(rs.getString(1)) );
      } // while
      return list;
    }
    catch (SQLException ex) {
      error(aLogger, "readColumn", "Failed reading from ResultSet.", ex);
      return null;
    }
    finally {
      close(aLogger, rs);
    }
  } // readColumn

/**
 * Read a single column from db and return its values as a list,
 * @param aLogger A logger used when errors occur.
 * @param aSql The sql query to run.
 * @param aConnection An active JDBC connection.
 * @return The column values as a ListOfString, where null values are replaced by
 * an emplty string, returns null if an error occurs.
 */
  public static ListOfString readColumn(final ILogger aLogger,
                                        final String aSql,
                                        final Connection aConnection) {
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    ListOfString r = readColumn(aLogger, aSql, stm);
    close(aLogger, stm);
    return r;
  } // readColumn

  /**
   * Read a single column from db and return its values as a list,
   * @param aLogger A logger used when errors occur.
   * @param aTableName Name of table to read from
   * @param aColumnName Name of column to read
   * @param aWhere A where clause.
   * @param aStatement An active JDBC statement.
   * @return The column values as a ListOfString, where null values are replaced by
   * an emplty string, returns null if an error occurs.
   */
  public static ListOfString readColumn(final ILogger aLogger,
                                        final String aTableName,
                                        final String aColumnName,
                                        final String aWhere,
                                        final Statement aStatement) {
    if (aStatement==null) return null;
    String sql= buildSelectQuery(aTableName, aColumnName, aWhere);
    return readColumn(aLogger, sql, aStatement);
  } // readColumn

  /**
   * Read a single column from db and return its values as a list.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Name of table to read from
   * @param aColumnName Name of column to read
   * @param aWhere A where clause.
   * @param aConnection An active JDBC connection.
   * @return The column values as a ListOfString, where null values are replaced by
   * an emplty string, returns null if an error occurs.
   */
  public static ListOfString readColumn(final ILogger aLogger,
                                        final String aTableName,
                                        final String aColumnName,
                                        final String aWhere,
                                        final Connection aConnection) {
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    ListOfString r = readColumn(aLogger, aTableName, aColumnName, aWhere, stm);
    close(aLogger, stm);
    return r;
  } // readColumn


/** Read the current row from the resultset then close it */
  private static String[] readAndClose(final ILogger aLogger,
                                       final ResultSet aResultSet) {
    if (aResultSet==null) return null;

    String[] r;
    if (!moveNext(aLogger, aResultSet)) {
      r= new String[] {};
    } else {
      r=getRowAsArray(aLogger, aResultSet);
    }

    close(aLogger, aResultSet);
    return r;
  }

 /**
  * Read a row from db, i.e. run the given sql query and return the first row of
  * the result set.
  * @param aLogger A logger used when errors occur.
  * @param aSql The SQL query to run
  * @param aStatement An active jdbc statement.
  * @return The read row as an array of strings, null values are converted
  * to empty string, return null if an error occur.
  */
  public static String[] readOneRow(final ILogger aLogger,
                                    final String aSql,
                                    final Statement aStatement) {
    ResultSet rs = read(aLogger, aSql, aStatement);
    return readAndClose(aLogger, rs);
  } // readOneRow

  /**
   * Read a row from db, i.e. run the given sql query and return the first row of
   * the result set.
   * @param aLogger A logger used when errors occur.
   * @param aSql The SQL query to run
   * @param aConnection An active jdbc connection.
   * @return The read row as an array of strings, null values are converted
   * to empty string, return null if an error occur.
   */
  public static String[] readOneRow(final ILogger aLogger,
                                    final String aSql,
                                    final Connection aConnection) {
    ResultSet rs= read(aLogger, aSql, aConnection);
    return readAndClose(aLogger, rs);
  } // readOneRow


  /** Read the current row from the resultset then close it */
  private static ListOfString readListAndClose(final ILogger aLogger,
                                               final ResultSet aResultSet) {
    if (aResultSet==null) return null;
    ListOfString r;
    if (!moveNext(aLogger, aResultSet)) {
      r= new ListOfString();
    } else {
      r= getRowAsList(aLogger, aResultSet);
    }
    close(aLogger, aResultSet);
    return r;
  }

 /**
  * Read a row from db, i.e. run the given sql query and return the first row of
  * the result set.
  * @param aLogger A logger used when errors occur.
  * @param aSql The SQL query to run
  * @param aStatement An active jdbc statement.
  * @return The read row as a list of strings, null values are converted
  * to empty string, return null if an error occur.
  */
  public static ListOfString readOneRowAsList(final ILogger aLogger,
                                              final String aSql,
                                              final Statement aStatement) {
    ResultSet rs= read(aLogger, aSql, aStatement);
    return readListAndClose(aLogger, rs);
  }

  /**
   * Read a row from db, i.e. run the given sql query and return the first row of
   * the result set.
   * @param aLogger A logger used when errors occur.
   * @param aSql The SQL query to run
   * @param aConnection An active jdbc connection.
   * @return The read row as a list of strings, null values are converted
   * to empty string, return null if an error occur.
   */
  public static ListOfString readOneRowAsList(final ILogger aLogger,
                                              final String aSql,
                                              final Connection aConnection) {
    ResultSet rs= read(aLogger, aSql, aConnection);
    return readListAndClose(aLogger, rs);
  }


/**
 * Read a single value from a single row using the given select statement.
 * @param aLogger A logger used when errors occur.
 * @param aSql The SQL query to read the value.
 * @param aStatement An active jdbc statement.
 * @return The value of the cell, null if an error occur, if no row satisfy the
 * where clause, return an empty string.
 */
  public static String readCellAsString(final ILogger aLogger,
                                        final String aSql,
                                        final Statement aStatement) {
    if (aStatement==null || StringUtility.isBlank(aSql)) return null;
    ResultSet rs = read(aLogger, aSql, aStatement);
    if (rs==null) return null;

    try {
      String r= StringUtility.EMPTY;
      if (rs.next())  r= StringUtility.defaultString(rs.getString(1));
      return r;
    }
    catch (SQLException ex) {
      error(aLogger, "readCellAsString", "Failed reading from ResultSet.", ex);
      return null;
    }
    finally {
      close(aLogger, rs);
    }
  }  // readCellAsString

/**
 * Read a single value from a single row using the given select statement.
 * @param aLogger A logger used when errors occur.
 * @param aSql The SQL query to read the value.
 * @param aConnection An active jdbc connection.
 * @return The value of the cell, null if an error occur, if no row satisfy the
 * where clause, return an empty string.
 */
  public static String readCellAsString(final ILogger aLogger,
                                        final String aSql,
                                        final Connection aConnection) {
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    String r= readCellAsString(aLogger, aSql, stm);
    close(aLogger, stm);
    return r;
  }  // readCellAsString


  /**
   * Read a single value from a single row in a table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aStatement An active jdbc statement.
   * @return The value of the cell, null if an error occur, if no row satisfy the
   * where clause, return an empty string.
   */
  public static String readCellAsString(final ILogger aLogger,
                                        final String aTableName,
                                        final String aColumnName,
                                        final String aWhere,
                                        final Statement aStatement) {
    String sql= buildSelectQuery(aTableName, aColumnName, aWhere);
    return readCellAsString(aLogger, sql, aStatement);
  }  // readCellAsString

  /**
   * Read a single value from a single row in a table.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aConnection An active jdbc connection.
   * @return The value of the cell, null if an error occur, if no row satisfy the
   * where clause, return an empty string.
   */
  public static String readCellAsString(final ILogger aLogger,
                                        final String aTableName,
                                        final String aColumnName,
                                        final String aWhere,
                                        final Connection aConnection) {
    if (aConnection==null) return null;
    Statement stm= getStatement(aLogger, aConnection);
    String r= readCellAsString(aLogger, aTableName, aColumnName, aWhere, stm);
    close(aLogger, stm);
    return r;
  }  // readCellAsString


  /**
   * Read a single value from a single row in a table as an integer.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aStatement An active jdbc statement.
   * @return The value of the cell, -1 if an error occur, if no row satisfy the
   * where clause, return 0.
   */
  public static int readCellAsInt(final ILogger aLogger,
                                  final String aTableName,
                                  final String aColumnName,
                                  final String aWhere,
                                  final Statement aStatement) {
    String s= readCellAsString(aLogger, aTableName, aColumnName, aWhere, aStatement);
    if (s==null) return -1;
    if (s.length()==0) return 0;
    if (! StringUtility.isNumeric(s)) return -1;
    return Integer.parseInt(s);
  }  // readCellAsInt

  /**
   * Read a single value from a single row in a table as an integer.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aConnection An active jdbc Connection.
   * @return The value of the cell, -1 if an error occur, if no row satisfy the
   * where clause, return 0.
   */
  public static int readCellAsInt(final ILogger aLogger,
                                  final String aTableName,
                                  final String aColumnName,
                                  final String aWhere,
                                  final Connection aConnection) {
    if (aConnection==null) return -1;
    Statement stm= getStatement(aLogger, aConnection);
    int r= readCellAsInt(aLogger, aTableName, aColumnName, aWhere, stm);
    close(aLogger, stm);
    return r;
  }  // readCellAsInt

/** 2005.02.21: Fixed the return type to int, was incorrectly long */
  public static int readCellAsInt(final ILogger aLogger,
                                   final String aSql,
                                   final Connection aConnection) {
    String r= readCellAsString(aLogger, aSql, aConnection);
    if (r==null) return -1;
    if (r.length()==0) return 0;
    if (! StringUtility.isNumeric(r)) return -1;
    return Integer.parseInt(r);
  }  // readCellAsInt

  /**
   * Read a single value from a single row in a table as a long.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aStatement An active jdbc statement.
   * @return The value of the cell, -1 if an error occur, if no row satisfy the
   * where clause, return 0.
   */
  public static long readCellAsLong(final ILogger aLogger,
                                    final String aTableName,
                                    final String aColumnName,
                                    final String aWhere,
                                    final Statement aStatement) {
    String s= readCellAsString(aLogger, aTableName, aColumnName, aWhere, aStatement);
    if (s==null) return -1;
    if (s.length()==0) return 0;
    if (! StringUtility.isNumeric(s)) return -1;
    return Long.parseLong(s);
  }  // readCellAsLong

  /**
   * Read a single value from a single row in a table as a long.
   * @param aLogger A logger used when errors occur.
   * @param aTableName Table to read from.
   * @param aColumnName Column to read its value
   * @param aWhere A where clause that should select one row.
   * @param aConnection An active jdbc Connection.
   * @return The value of the cell, -1 if an error occur, if no row satisfy the
   * where clause, return 0.
   */
  public static long readCellAsLong(final ILogger aLogger,
                                    final String aTableName,
                                    final String aColumnName,
                                    final String aWhere,
                                    final Connection aConnection) {
    if (aConnection==null) return -1;
    Statement stm= getStatement(aLogger, aConnection);
    long r= readCellAsLong(aLogger, aTableName, aColumnName, aWhere, stm);
    close(aLogger, stm);
    return r;
  }  // readCellAsLong


  public static long readCellAsLong(final ILogger aLogger,
                                    final String aSql,
                                    final Connection aConnection) {
    String r= readCellAsString(aLogger, aSql, aConnection);
    if (r==null) return -1;
    if (r.length()==0) return 0;
    if (! StringUtility.isNumeric(r)) return -1;
    return Long.parseLong(r);
  }  // readCellAsLong


  /**
   * Execute an insert or update statement that has ONE column which will be set as
   * a prepared statement parameter. Usually, the paramter will be a long string that
   * may contain control characters.
   * @param aLogger aLogger A logger used when errors occur.
   * @param aPreparedSql A valid SQL statement with ONE column's value as a ?.
   *        If aPreparedSql is blank, nothing will happen and the method returns false.
   * @param aLongString A string that will be inserted into the statement through
   *        a <b>setString(aParamIndex, aLongString)</b> statement.
   * @param aParamIndex The index of the long string parameter in the prepared
   *        statement, starting at 1.
   * @param aConnection An open connection. If null, nothing will happen and the
   *        method returns false.
   * @return true if success, false if not.
   */
  public static boolean writeLongString(final ILogger aLogger,
                                        final String aPreparedSql,
                                        final String aLongString,
                                        final int aParamIndex,
                                        final Connection aConnection) {
    if (aConnection==null) return false;
    if (StringUtility.isBlank(aLongString)) return false;

    PreparedStatement stm= getPreparedStatement(aLogger, aConnection, aPreparedSql);
    if (stm==null) return false;

    try {
      Reader reader= new StringReader(aLongString);
      stm.setCharacterStream(aParamIndex, reader, aLongString.length());
      stm.executeUpdate();
      return true;
    }
    catch (SQLException ex) {
      error(aLogger, "writeLongString", "Failed. Query=\n" + aPreparedSql , ex);
      return false;
    } finally {
      close(aLogger, stm);
    }
  } // writeLongString


  /**
   * Convert a resultset to a string. Used mainly for debugging.
   * @param aResultSet The ResultSet to convert.
   * @param aColumnSeparator The separator between columns.
   * @param aRowSeparator The separator between rows.
   * @return The string representation of the result set. null if an error occur.
   */
  public static String resultSetToString(final ResultSet aResultSet,
                                         final String aColumnSeparator,
                                         final String aRowSeparator) {
    try {
      DBRowList list = new DBRowList(aResultSet);
      return list.toString(aColumnSeparator, aRowSeparator );
    }
    catch (SQLException ex) {
      return null;
    }
  } // resultSetToString

  /**
   * Convert a resultset to a string. Used mainly for debugging.
   * @param aResultSet The ResultSet to convert.
   * @return The string representation of the result set. null if an error occur.
   * Columns are separated by CommaSpace. Rows are separated by new-line.
   */
  public static String resultSetToString(final ResultSet aResultSet) {
    return resultSetToString(aResultSet, ", ", StringUtility.NEW_LINE);
  } // resultSetToString

  /**
   * Move the ResultSet cursor to next row. Errors are logged.
   * @param aLogger aLogger A logger used when errors occur.
   * @param aResultSet
   * @return true if successful, false if not.
   */
  public static boolean moveNext(final ILogger aLogger, final ResultSet aResultSet) {
    try {
      return aResultSet.next();
    }
    catch (SQLException ex) {
      error(aLogger, "moveNext", "Failed to move cursor to next row.", ex);
      return false;
    }
  }  // moveNext

  /**
   * Get the next sequence id for an Oracle table
   * @param aConnection An open jdbc connection
   * @param aSequenceName The name of the sequence including schema name if needed.
   * @return the next sequence #, -1 if an error occur.
   */
  public static long oracleNextSeq(final ILogger aLogger,
                                   final Connection aConnection,
                                   final String aSequenceName) {
    return readCellAsLong(aLogger,
                          "DUAL",
                          aSequenceName + ".nextval",
                          StringUtility.EMPTY,
                          aConnection);
  }  // oracleNextSeq

  /**
   * Put a schema prefix before the given name.
   * @param aSchema if schema is null or empty, name will be returned.
   * @param aName The name of a db object like a table.
   * @return The name prefixed with the schema if the schema is given.
   */
  public static String schemaPrefix(final String aSchema, final String aName) {
    String s= StringUtility.defaultString(aSchema).trim();
    if (s.length()==0) return aName;
    return s + '.' + aName;
  }  // schemaPrefix()

  private final static String pMYSQL_LAST_ID= "SELECT LAST_INSERT_ID()";

/**
 * MySql specific operation: read the id of the last row inserted into a MySql db.
 * Will run the query: <code>SELECT LAST_INSERT_ID()</code>
 * @param aLogger ILogger
 * @param aConnection Connection
 * @return long the id of the inserted row, -1 if error.
 */
  public static long mySqlReadLastId(final ILogger aLogger,
                                     final Connection aConnection) {
    return readCellAsLong(aLogger, pMYSQL_LAST_ID, aConnection);
  }


/*
  * @param aDriver jdbc driver, e.g.: oracle.jdbc.driver.OracleDriver
  * @param aUrl Url to the db. e.g.: jdbc:oracle:thin:@server1.tek271.com
  * @param aUser A db user name. e.g.: coolDude
  * @param aPassword Password of user.

/** For testing */
  public static void main(String[] args) {
    ILogger log= SimpleConsoleLogger.LOGGER;
    String driver= "oracle.jdbc.driver.OracleDriver";
    String url= "jdbc:oracle:thin:@server1.tek271.com";
    String user= "coolDude";
    String password= "password";
    Connection con= DbUtil.getConnectionJdbc(log, driver, url, user, password);
    if (con==null) return;  // error happened and logged

    String sql= "select name from employee where id=5";
    String name= DbUtil.readCellAsString(log, sql, con);
    if (name==null) return;  // error happened and logged
    System.out.println("Name is " + name);

    DbUtil.close(log, con);
  }

}  // DbUtil
