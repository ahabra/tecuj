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

import com.tek271.util.exception.ExceptionUtil;
import com.tek271.util.io.Net;
import com.tek271.util.log.ILogger;
import com.tek271.util.string.*;

/**
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * Utility methods to handle Blobs in db tables.<p>
 * Note that: <ol>
 * <li>This library uses 1 as the starting index of query parameters to stay
 * similar to JDBC (albeit it is goofy).</li>
 * <li>This library uses integers to represent sizes of blobs which will restrict
 * the size of the blob to the max represented by Integer.MAX_VALUE. </li>
 * <li>It does not make sense to create classes that implement the Blob
 * interface outside the context of a JDBC driver. The Blob api requires direct
 * connection to the db BLOB fields.</li>
 * </ol>
 * @author Abdul Habra
 * @version 1.0
 */
public class BlobUtil {
  private final static String pCLASS_NAME= "com.tek271.util.db.BlobUtil";

/** Do not call this constructor. Allows extending the class. */
  public BlobUtil() {}

/** Log an error message */
  private static void error(final ILogger aLogger, final String aMethod,
                            final String aMessage, final Exception aException) {
    ExceptionUtil.error(aLogger, pCLASS_NAME, aMethod, aMessage, aException);
  }

/**
 * Write the data available through the stream to the Blob field using the given
 * prepared statement.
 * @param aLogger ILogger For error logging.
 * @param aSql String The sql statement. Used here for logging errors only.
 * @param aStatement PreparedStatement The prepared statement to write to db.
 * @param aParamIndex int The index of the blob parameter in the prepared
 *        statement, starting at 1.
 * @param aStream InputStream a byte stream to read from
 * @param aLength int Number of bytes to read from the stream.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final PreparedStatement aStatement,
                              final int aParamIndex,
                              final InputStream aStream,
                              final int aLength) {
    boolean ok= true;
    try {
      aStatement.setBinaryStream(aParamIndex, aStream, aLength);
      aStatement.executeUpdate();
    } catch (SQLException ex) {
      ok= false;
      error(aLogger, "write", "Failed. Query=\n" + aSql, ex);
    }
    return ok;
  }  // write

/**
 * Write the byte array data to the Blob field using the given prepared statement.
 * @param aLogger ILogger For error logging.
 * @param aSql String The sql statement. Used here for logging errors only.
 * @param aStatement PreparedStatement The prepared statement to write to db.
 * @param aParamIndex int The index of the blob parameter in the prepared
 *        statement, starting at 1.
 * @param aData byte[] a byte array of data to be written to blob field.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean write(final ILogger aLogger,
                              final String aSql,
                              final PreparedStatement aStatement,
                              final int aParamIndex,
                              final byte[] aData) {
    InputStream stream= new ByteArrayInputStream(aData);
    boolean r= write(aLogger, aSql, aStatement, aParamIndex, stream, aData.length);
    IOUtils.closeQuietly(stream);
    return r;
  }  // write

/**
 * Write the data available through the stream to the Blob field using the given sql
 * statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aStream InputStream a byte stream to read from
 * @param aLength int Number of bytes to read from the stream.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCell(final Connection aConnection,
                                  final ILogger aLogger,
                                  final String aPreparedSql,
                                  final InputStream aStream,
                                  final int aLength) {
    PreparedStatement stm= DbUtil.getPreparedStatement(aLogger, aConnection, aPreparedSql);
    if (stm==null) return false;

    boolean ok= write(aLogger, aPreparedSql, stm, 1, aStream, aLength);
    DbUtil.close(aLogger, stm);
    return ok;
  }  // writeCell

/**
 * Write aData to a Clob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aData The large string to write to the Clob field.
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCell(final Connection aConnection,
                                  final ILogger aLogger,
                                  final String aPreparedSql,
                                  final byte[] aData) {
    InputStream stream= new ByteArrayInputStream(aData);
    boolean r= writeCell(aConnection, aLogger, aPreparedSql, stream, aData.length);
    IOUtils.closeQuietly(stream);
    return r;
  }  // writeCell

/**
 * Write the contents of the given file to the blob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aFile File The file to be written to the blob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromFile(final Connection aConnection,
                                          final ILogger aLogger,
                                          final String aPreparedSql,
                                          final File aFile) {
    int n= (int) aFile.length();
    boolean r= false;
    try {
      InputStream is=new FileInputStream(aFile);
      r=writeCell(aConnection, aLogger, aPreparedSql, is, n);
      is.close();
    } catch (IOException ex) {
      r= false;
      error(aLogger, "writeFromFile", "Cannot read file.", ex);
    }
    return r;
  }  // writeCellFromFile()

/**
 * Write the contents of the given file to the blob field using the given sql statement.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aPath String The file to be written to the blob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromFile(final Connection aConnection,
                                          final ILogger aLogger,
                                          final String aPreparedSql,
                                          final String aPath) {
    File f= new File(aPath);
    return writeCellFromFile(aConnection, aLogger, aPreparedSql, f);
  }  // writeCellFromFile()

/**
 * Write the contents of the given URL to the blob field using the given sql statement.
 * The url is first read into memory then written to the blob, so if the size of data
 * read is large (compared with available memory), this method may not be appropriate.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aUrl URL The URL whose content to be written to the blob field
 * @return boolean true if success, false if not. Errors are logged.
 */
  public static boolean writeCellFromUrl(final Connection aConnection,
                                         final ILogger aLogger,
                                         final String aPreparedSql,
                                         final URL aUrl) {
    byte[] data= Net.urlToByteArray(aLogger, aUrl);
    return writeCell(aConnection, aLogger, aPreparedSql, data);
  }  // writeCellFromUrl()

/**
 * Write the contents of the given URL to the blob field using the given sql statement.
 * The url is first read into memory then written to the blob, so if the size of data
 * read is large (compared with available memory), this method may not be appropriate.
 * @param aConnection Connection An active db connection
 * @param aLogger ILogger For error logging.
 * @param aPreparedSql String A sql statement with ONLY one ? mark for the blob parameter.
 * @param aUrl String The URL whose content to be written to the blob field
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
 * Get an inputStream from the Blob column whose index or name is given in method parameters.
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet A result set which contains a Blob in its current row
 * @param aBlobColumnIndex int Index of the Blob column, -ve means name will be used.
 * @param aBlobColumnName String Name of Blob column, null means index will be used.
 * @return InputStream to the Blob. Return null if error.
 *         If index is -ve and name is null, an error is logged and null is returned.
 *         If both index and name are specified, index will be used.
 */
  private static InputStream getInputStreamFromRs(final ILogger aLogger,
                                                  final ResultSet aResultSet,
                                                  final int aBlobColumnIndex,
                                                  final String aBlobColumnName) {
    String method= "getInputStreamFromRs";
    try {
      if (aBlobColumnIndex > 0) {
        return aResultSet.getBinaryStream(aBlobColumnIndex);
      } else if (StringUtility.isNotBlank(aBlobColumnName) ) {
        return aResultSet.getBinaryStream(aBlobColumnName);
      } else {
        error(aLogger, method, "No Blob column index or name passed", null);
        return null;
      }
    } catch (SQLException ex) {
      error(aLogger, method, "Cannot access a Blob cell", ex);
      return null;
    }
  }  // getInputStreamFromRs

  private static byte[] getCellAsBytes(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aBlobColumnIndex,
                                       final String aBlobColumnName) {
    String method= "getCellAsBytes";
    InputStream is= getInputStreamFromRs(aLogger, aResultSet, aBlobColumnIndex, aBlobColumnName);
    if (is==null) return null;

    byte[] r;
    try {
      r= IOUtils.toByteArray(is);
    } catch (IOException ex) {
      error(aLogger, method, "Cannot read from a Blob cell", ex);
      return null;
    }
    return r;
  }  // private getCellAsBytes

/**
 * Get a Blob cell as a byte array from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnIndex int index of Blob column
 * @return byte[] null if an error occur.
 */
  public static byte[] getCellAsBytes(final ILogger aLogger,
                                      final ResultSet aResultSet,
                                      final int aBlobColumnIndex) {
    return getCellAsBytes(aLogger, aResultSet, aBlobColumnIndex, null);
  }  // getCellAsBytes

/**
 * Get a Blob cell as a byte array from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnName String Name of Blob column
 * @return byte[] null if an error occur.
 */
  public static byte[] getCellAsBytes(final ILogger aLogger,
                                      final ResultSet aResultSet,
                                      final String aBlobColumnName) {
    return getCellAsBytes(aLogger, aResultSet, -1, aBlobColumnName);
  }  // getCellAsBytes

  private static boolean copyCellToStream(final ILogger aLogger,
                                          final ResultSet aResultSet,
                                          final int aBlobColumnIndex,
                                          final String aBlobColumnName,
                                          final OutputStream aStream) {
    String method= "copyCellToStream";
    InputStream is= getInputStreamFromRs(aLogger, aResultSet, aBlobColumnIndex, aBlobColumnName);
    if (is==null) return false;

    try {
      IOUtils.copy(is, aStream);
      return true;
    } catch (IOException ex) {
      error(aLogger, method, "Cannot copy from a Blob cell to an output stream", ex);
      return false;
    }
  }  // private copyCellToStream

/**
 * Copy a Blob cell to an output stream from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnIndex int index of blob column
 * @param aStream OutputStream stream to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToStream(final ILogger aLogger,
                                         final ResultSet aResultSet,
                                         final int aBlobColumnIndex,
                                         final OutputStream aStream) {
    return copyCellToStream(aLogger, aResultSet, aBlobColumnIndex, null, aStream);
  }  // copyCellToStream

/**
 * Copy a Blob cell to an output stream from the current row in the ResultSet
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnName String name of blob column
 * @param aStream OutputStream stream to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToStream(final ILogger aLogger,
                                         final ResultSet aResultSet,
                                         final String aBlobColumnName,
                                         final OutputStream aStream) {
    return copyCellToStream(aLogger, aResultSet, -1, aBlobColumnName, aStream);
  }  // copyCellToStream

/**
 * Copy a Blob cell to an output stream from the first row/column in the data read
 * by given sql
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @param aStream OutputStream stream to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToStream(final Connection aConnection,
                                         final ILogger aLogger,
                                         final String aSql,
                                         final OutputStream aStream) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return false;
    if (! DbUtil.moveNext(aLogger, rs)) return false;

    boolean r= copyCellToStream(aLogger, rs, 1, aStream);
    DbUtil.close(aLogger, rs);
    return r;
  }  // copyCellToStream

  private static boolean copyCellToFile(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aBlobColumnIndex,
                                        final String aBlobColumnName,
                                        final File aFile) {
    String method= "copyCellToFile";
    boolean r= false;
    try {
      OutputStream os=new FileOutputStream(aFile);
      r=copyCellToStream(aLogger, aResultSet, aBlobColumnIndex, aBlobColumnName, os);
      os.close();
    } catch (IOException ex) {
      error(aLogger, method, "Cannot write to file.", ex);
    }
    return r;
  }  // private copyCellToFile

  private static boolean copyCellToFile(final ILogger aLogger,
                                        final ResultSet aResultSet,
                                        final int aBlobColumnIndex,
                                        final String aBlobColumnName,
                                        final String aPath) {
    File f= new File(aPath);
    return copyCellToFile(aLogger, aResultSet, aBlobColumnIndex, aBlobColumnName, f);
  }  // private copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnIndex int Lob field index
 * @param aFile the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aBlobColumnIndex,
                                       final File aFile) {
    return copyCellToFile(aLogger, aResultSet, aBlobColumnIndex, null, aFile);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnIndex int Lob field index
 * @param aPath String path of the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final int aBlobColumnIndex,
                                       final String aPath) {
    return copyCellToFile(aLogger, aResultSet, aBlobColumnIndex, null, aPath);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnName String Lob field name.
 * @param aFile File the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final String aBlobColumnName,
                                       final File aFile) {
    return copyCellToFile(aLogger, aResultSet, -1, aBlobColumnName, aFile);
  }  // copyCellToFile

/**
 * Copy a Lob cell to a file
 * @param aLogger ILogger For error logging.
 * @param aResultSet ResultSet
 * @param aBlobColumnName String Lob field name.
 * @param aPath String path of the file to write to
 * @return boolean false if an error occur.
 */
  public static boolean copyCellToFile(final ILogger aLogger,
                                       final ResultSet aResultSet,
                                       final String aBlobColumnName,
                                       final String aPath) {
    return copyCellToFile(aLogger, aResultSet, -1, aBlobColumnName, aPath);
  }  // copyCellToFile

/**
 * Copy a Lob (Blob/Clob) cell to a file from the first row/column in the data
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
 * Copy a Lob (Blob/Clob) cell to a file from the first row/column in the data
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
 * Read the BLOB cell as a byte array from the first row/column in the data
 * read by given sql.
 * @param aConnection Connection an active jdbc connection
 * @param aLogger ILogger For error logging.
 * @param aSql String sql to read a single row with one column.
 * @return String null if an error occur.
 */
  public static byte[] readCellAsBytes(final Connection aConnection,
                                       final ILogger aLogger,
                                       final String aSql) {
    ResultSet rs= DbUtil.read(aLogger, aSql, aConnection);
    if (rs==null) return null;
    if (! DbUtil.moveNext(aLogger, rs)) return null;

    byte[] r= getCellAsBytes(aLogger, rs, 1);
    DbUtil.close(aLogger, rs);
    return r;
  }  // readCellAsBytes

}  // LobUtil
