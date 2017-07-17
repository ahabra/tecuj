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
import java.io.*;
import java.net.*;
import org.apache.commons.io.*;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.exception.ExceptionUtil;
import com.tek271.util.io.Net;
import com.tek271.util.log.ILogger;
import com.tek271.util.string.*;

/**
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * Utility methods to Clobs in db tables.<p>
 * Note that: <ol>
 * <li>This library uses 1 as the starting index of query parameters to stay
 * similar to JDBC (albeit it is goofy).</li>
 * <li>This library uses integers to represent sizes of clobs which will restrict
 * the size of the clob to the max represented by Integer.MAX_VALUE. </li>
 * <li>It does not make sense to create classes that implement the Clob
 * interfaces outside the context of a JDBC driver. The Clob api requires direct
 * connection to the db CLOB fields.</li>
 * </ol>
 * @author Abdul Habra
 * @version 1.0
 */
public class ClobUtil {
  private final static String pCLASS_NAME= "com.tek271.util.db.ClobUtil";

/** Do not call this constructor. Allows extending the class. */
  public ClobUtil() {}

/** Log an error message */
  private static void error(final ILogger aLogger, final String aMethod,
                            final String aMessage, final Exception aException) {
    ExceptionUtil.error(aLogger, pCLASS_NAME, aMethod, aMessage, aException);
  }

/**
 * Write the data available through the Reader to the Clob field using the given
 * prepared statement.
 * @param aLogger ILogger For error logging.
 * @param aSql String The sql statement. Used here for logging errors only.
 * @param aStatement PreparedStatement The prepared statement to write to db.
 * @param aParamIndex int The index of the Clob parameter in the prepared
 *        statement, starting at 1.
 * @param aReader Reader a character reader to read from
 * @param aLength int Number of chars to read from the reader.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final PreparedStatement aStatement,
                              final int aParamIndex,
                              final Reader aReader,
                              final int aLength) {
    boolean ok= true;
    try {
      aStatement.setCharacterStream(aParamIndex, aReader, aLength);
      aStatement.executeUpdate();
    } catch (SQLException ex) {
      ok= false;
      error(aLogger, "write", "Failed. Query=\n" + aSql, ex);
    }
    return ok;
  }  // write

/**
 * Write the char array data to the Clob field using the given prepared statement.
 * @param aLogger ILogger For error logging.
 * @param aSql String The sql statement. Used here for logging errors only.
 * @param aStatement PreparedStatement The prepared statement to write to db.
 * @param aParamIndex int The index of the clob parameter in the prepared
 *        statement, starting at 1.
 * @param aData char[] a character array of data to be written to Clob field.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final PreparedStatement aStatement,
                              final int aParamIndex,
                              final char[] aData) {
    Reader rdr= new CharArrayReader(aData);
    boolean r= write(aLogger, aSql, aStatement, aParamIndex, rdr, aData.length);
    IOUtils.closeQuietly(rdr);
    return r;
  }  // write

/**
 * Write the given string data to the Clob field using the given prepared statement.
 * @param aLogger ILogger For error logging.
 * @param aSql String The sql statement. Used here for logging errors only.
 * @param aStatement PreparedStatement The prepared statement to write to db.
 * @param aParamIndex int The index of the clob parameter in the prepared
 *        statement, starting at 1.
 * @param aData String a string of data to be written to Clob field.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final PreparedStatement aStatement,
                              final int aParamIndex,
                              final String aData) {
    Reader rdr= new  StringReader(aData);
    boolean r= write(aLogger, aSql, aStatement, aParamIndex, rdr, aData.length());
    IOUtils.closeQuietly(rdr);
    return r;
  }  // write


/**
 * Write the data available through the reader to the Clob field using the given sql
 * statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aReader Reader a character stream to read from.
 * @param aLength int Number of chars to read from the stream.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCell(final Connection aConnection,
                                  final ILogger aLogger,
                                  final String aPreparedSql,
                                  final Reader aReader,
                                  final int aLength) {
    PreparedStatement stm= DbUtil.getPreparedStatement(aLogger, aConnection, aPreparedSql);
    if (stm==null) return false;

    boolean ok= write(aLogger, aPreparedSql, stm, 1, aReader, aLength);
    DbUtil.close(aLogger, stm);
    return ok;
  }  // writeCell

/**
 * Write aData to a Clob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aData The large string to write to the Clob field.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCell(final Connection aConnection,
                                  final ILogger aLogger,
                                  final String aPreparedSql,
                                  final String aData) {
    Reader rdr= new StringReader(aData);
    boolean r= writeCell(aConnection, aLogger, aPreparedSql, rdr, aData.length());
    IOUtils.closeQuietly(rdr);
    return r;
  }  // writeCell

/**
 * Write the contents of the given file to the clob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aFile File The file to be written to the clob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromFile(final Connection aConnection,
                                          final ILogger aLogger,
                                          final String aPreparedSql,
                                          final File aFile) {
    int n= (int) aFile.length();
    boolean r= false;
    try {
      Reader rdr=new FileReader(aFile);
      r=writeCell(aConnection, aLogger, aPreparedSql, rdr, n);
      rdr.close();
    } catch (IOException ex) {
      r= false;
      error(aLogger, "writeFromFile", "Cannot read file.", ex);
    }
    return r;
  }  // writeCellFromFile()


/**
 * Write the contents of the given file to the clob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aPath String The file to be written to the clob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromFile(final Connection aConnection,
                                          final ILogger aLogger,
                                          final String aPreparedSql,
                                          final String aPath ) {
    File f= new File(aPath);
    return writeCellFromFile(aConnection, aLogger, aPreparedSql, f);
  }  // writeCellFromFile()

/**
 * Write the contents of the given URL to the clob field using the given sql statement.
 * The url is first read into memory then written to the clob, so if the size of data
 * read is large (compared with available memory), this method may not be appropriate.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aUrl URL The URL whose content to be written to the clob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromUrl(final Connection aConnection,
                                         final ILogger aLogger,
                                         final String aPreparedSql,
                                         final URL aUrl) {
    String data= Net.urlToString(aLogger, aUrl);
    return writeCell(aConnection, aLogger, aPreparedSql, data);
  }  // writeCellFromUrl()


/**
 * Write the contents of the given URL to the clob field using the given sql statement.
 * The url is first read into memory then written to the clob, so if the size of data
 * read is large (compared with available memory), this method may not be appropriate.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the clob parameter.
 * @param aUrl String The URL whose content to be written to the clob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromUrl(final Connection aConnection,
                                         final ILogger aLogger,
                                         final String aPreparedSql,
                                         final String aUrl) {
    URL url= Net.createUrl(aLogger, aUrl);
    if (url==null) return false;
    return writeCellFromUrl(aConnection, aLogger, aPreparedSql, url);
  }  // writeCellFromUrl()

/**
 * Get a Clob cell as a ListOfString from the current row in the ResultSet. The result set
 * should be available to read from (next() must have been called)
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int Index of a Clob column
 * @param aClobColumnName String Name of Clob column
 * @return ListOfString null if an error occur.
 */
  private static ListOfString getCellAsList(final ILogger aLogger,
                                            final ResultSet aResultSet,
                                            final int aClobColumnIndex,
                                            final String aClobColumnName) {
    String method= "getCellAsList";
    Reader rdr= getReaderFromRs(aLogger, aResultSet, aClobColumnIndex, aClobColumnName);
    if (rdr==null) return null;

    ListOfString list= new ListOfString();
    try {
      list.readFromReader(rdr);  // will close reader
    } catch (IOException ex) {
      error(aLogger, method, "Cannot read from a Clob cell", ex);
      return null;
    }
    return list;
  }  // private getCellAsList

/**
 * Get a Clob cell as a ListOfString from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int Index of a Clob column
 * @return ListOfString null if an error occur.
 */
  public static ListOfString getCellAsList(final ILogger aLogger,
                                           final ResultSet aResultSet,
                                           final int aClobColumnIndex) {
    return getCellAsList(aLogger, aResultSet, aClobColumnIndex, null);
  }  // getCellAsList

/**
 * Get a Clob cell as a ListOfString from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String Name of a Clob column
 * @return ListOfString null if an error occur.
 */
  public static ListOfString getCellAsList(final ILogger aLogger,
                                           final ResultSet aResultSet,
                                           final String aClobColumnName) {
    return getCellAsList(aLogger, aResultSet, -1, aClobColumnName);
  }  // getCellAsList

/**
 * Get a Reader from the Clob column whose index or name is given in method parameters.
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet A result set which contains a Clob in its current row
 * @param aClobColumnIndex int The index of the Clob column, -ve means name will be used.
 * @param aClobColumnName String Name of Clob column, null means index will be used.
 * @return Reader the Reader to the Clob. Return null if error.
 *         If index is -ve and name is null, an error is logged and null is returned.
 *         If both index and name are specified, index will be used.
 */
  private static Reader getReaderFromRs(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aClobColumnIndex,
                                        final String aClobColumnName) {
    String method= "getReaderFromRs";
    try {
      if (aClobColumnIndex > 0) {
        return aResultSet.getCharacterStream(aClobColumnIndex);
      } else if (StringUtility.isNotBlank(aClobColumnName) ) {
        return aResultSet.getCharacterStream(aClobColumnName);
      } else {
        error(aLogger, method, "No Clob column index or name passed", null);
        return null;
      }
    } catch (SQLException ex) {
      error(aLogger, method, "Cannot access a Clob cell", ex);
      return null;
    }
  }  // getReaderFromRs

  private static String getCellAsString(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aClobColumnIndex,
                                        final String aClobColumnName) {
    String method= "getCellAsString";
    Reader rdr= getReaderFromRs(aLogger, aResultSet, aClobColumnIndex, aClobColumnName);
    if (rdr==null) return null;

    String r;
    try {
      r= IOUtils.toString(rdr);
    } catch (IOException ex) {
      error(aLogger, method, "Cannot read from a Clob cell", ex);
      return null;
    }
    return r;
  }  // private getCellAsString


/**
 * Get a Clob cell as a String from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int Index of a Clob column
 * @return String null if an error occur.
 */
  public static String getCellAsString(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aClobColumnIndex) {
    return getCellAsString(aLogger, aResultSet, aClobColumnIndex, null);
  }  // getCellAsString

/**
 * Get a Clob cell as a String from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String Name of a Clob column
 * @return String null if an error occur.
 */
  public static String getCellAsString(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final String aClobColumnName) {
    return getCellAsString(aLogger, aResultSet, -1, aClobColumnName);
  }  // getCellAsString

/**
 * Get a Clob cell as a char array from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int index of Clob column
 * @return char[] null if an error occur.
 */
  public static char[] getCellAsChars(final ILogger aLogger,
                                      final ResultSet aResultSet,
                                      final int aClobColumnIndex) {
    String s= getCellAsString(aLogger, aResultSet, aClobColumnIndex);
    return s.toCharArray();
  }  // getCellAsChars

/**
 * Get a Clob cell as a char array from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String name of Clob column
 * @return char[] null if an error occur.
 */
  public static char[] getCellAsChars(final ILogger aLogger,
                                      final ResultSet aResultSet,
                                      final String aClobColumnName) {
    String s= getCellAsString(aLogger, aResultSet, aClobColumnName);
    return s.toCharArray();
  }  // getCellAsChars

  private static boolean copyCellToWriter(final ILogger aLogger,
                                          final ResultSet aResultSet,
                                          final int aClobColumnIndex,
                                          final String aClobColumnName,
                                          final Writer aWriter) {
    String method= "copyCellToWriter";
    Reader rd= getReaderFromRs(aLogger, aResultSet, aClobColumnIndex, aClobColumnName);
    if (rd==null) return false;

    try {
      IOUtils.copy(rd, aWriter);
      return true;
    } catch (IOException ex) {
      error(aLogger, method, "Cannot copy from a Clob cell to a Writer", ex);
      return false;
    }
  }  // private copyCellToWriter

/**
 * Copy a Clob cell to a Writer from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int index of Clob column
 * @param aWriter Writer to write to.
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToWriter(final ILogger aLogger,
                                         final ResultSet aResultSet,
                                         final int aClobColumnIndex,
                                         final Writer aWriter) {
    return copyCellToWriter(aLogger, aResultSet, aClobColumnIndex, null, aWriter);
  }  // copyCellToWriter

/**
 * Copy a Clob cell to a Writer from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String name of Clob column
 * @param aWriter Writer to write to.
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToWriter(final ILogger aLogger,
                                         final ResultSet aResultSet,
                                         final String aClobColumnName,
                                         final Writer aWriter) {
    return copyCellToWriter(aLogger, aResultSet, -1, aClobColumnName, aWriter);
  }  // copyCellToWriter

/**
 * Copy a Clob cell to a Writer from the first row/column in the data read by given sql
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @param aWriter Writer to write to.
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToWriter(final Connection aConnection,
                                         final ILogger aLogger,
                                         final String aSql,
                                         final Writer aWriter) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return false;
    if (! DbUtil.moveNext(aLogger, rs)) return false;

    boolean r= copyCellToWriter(aLogger, rs, 1, aWriter);
    DbUtil.close(aLogger, rs);
    return r;
  }  // copyCellToWriter

  private static boolean copyCellToFile(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aClobColumnIndex,
                                        final String aClobColumnName,
                                        final File aFile) {
    String method= "copyCellToFile";
    boolean r;
    try {
      Writer w=new FileWriter(aFile);
      r=copyCellToWriter(aLogger, aResultSet, aClobColumnIndex, aClobColumnName, w);
      w.close();
    } catch (IOException ex) {
      error(aLogger, method, "Cannot write to file.", ex);
      r= false;
    }
    return r;
  }  // private copyCellToFile

  private static boolean copyCellToFile(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aClobColumnIndex,
                                        final String aClobColumnName,
                                        final String aPath) {
    File f= new File(aPath);
    return copyCellToFile(aLogger, aResultSet, aClobColumnIndex, aClobColumnName, f);
  }  // private copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int Lob field index
 * @param aFile the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aClobColumnIndex,
                                       final File aFile) {
    return copyCellToFile(aLogger, aResultSet, aClobColumnIndex, null, aFile);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnIndex int Lob field index
 * @param aPath String path of the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aClobColumnIndex,
                                       final String aPath ) {
    return copyCellToFile(aLogger, aResultSet, aClobColumnIndex, null, aPath);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String Lob field name.
 * @param aFile File the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final String aClobColumnName,
                                       final File aFile ) {
    return copyCellToFile(aLogger, aResultSet, -1, aClobColumnName, aFile);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aClobColumnName String Lob field name.
 * @param aPath String path of the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final String aClobColumnName,
                                       final String aPath ) {
    return copyCellToFile(aLogger, aResultSet, -1, aClobColumnName, aPath);
  }  // copyCellToFile

/**
 * Copy a Clob cell to a file from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger ILogger For error logging.
 * @param aSql String String sql to read a single row with one column.
 * @param aFile File the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final Connection aConnection,
                                       final ILogger aLogger,
                                       final String aSql,
                                       final File aFile) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return false;
    if (! DbUtil.moveNext(aLogger, rs)) return false;

    boolean r= copyCellToFile(aLogger, rs, 1, aFile);
    DbUtil.close(aLogger, rs);
    return r;
  }  // copyCellToFile

/**
 * Copy a Clob cell to a file from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger ILogger For error logging.
 * @param aSql String String sql to read a single row with one column.
 * @param aPath String path of the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final Connection aConnection,
                                       final ILogger aLogger,
                                       final String aSql,
                                       final String aPath) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return false;
    if (! DbUtil.moveNext(aLogger, rs)) return false;

    boolean r= copyCellToFile(aLogger, rs, 1, aPath);
    DbUtil.close(aLogger, rs);
    return r;
  }  // copyCellToFile

/**
 * Read the CLOB cell as a ListOfString from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @return ListOfString null if an error occur.
 */
  public static ListOfString readCellAsList(final Connection aConnection,
                                            final ILogger aLogger,
                                            final String aSql) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return null;
    if (! DbUtil.moveNext(aLogger, rs)) return null;

    ListOfString r= getCellAsList(aLogger, rs, 1);
    DbUtil.close(aLogger, rs);
    return r;
  }  // readCellAsList

/**
 * Read the CLOB cell as a string from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @return String null if an error occur.
 */
  public static String readCellAsString(final Connection aConnection,
                                        final ILogger aLogger,
                                        final String aSql) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return null;
    if (! DbUtil.moveNext(aLogger, rs)) return null;

    String r= getCellAsString(aLogger, rs, 1);
    DbUtil.close(aLogger, rs);
    return r;
  }  // readCellAsString

/**
 * Read the CLOB cell as a char array from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @return char array. null if an error occur.
 */
  public static char[] readCellAsChars(final Connection aConnection,
                                       final ILogger aLogger,
                                       final String aSql) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return null;
    if (! DbUtil.moveNext(aLogger, rs)) return null;

    char[] r= getCellAsChars(aLogger, rs, 1);
    DbUtil.close(aLogger, rs);
    return r;
  }  // readCellAsChars

}  // ClobUtil
