/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra, Doug Estep
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
import java.util.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.string.*;

/**
 * Extends a <code>RowList</code> by adding JDBC-related methods,
 * including the ability to construct from a <code>ResultSet</code>.
 * @version 1.0
 * @since 1.0
 * @author Doug Estep, Abdul Habra
 */
public class DBRowList extends RowList {

  private String pTableName = StringUtility.EMPTY;
  private String pDatabaseName = StringUtility.EMPTY;
  private String pSchemaName = StringUtility.EMPTY;

/**
 * Constructs a <code>DBRowList</code>, setting the number of columns for each row.
 * @since 1.0
 */
  public DBRowList(final int aColumnCount) {
    super(aColumnCount);
  }

/**
 * Constructs a <code>DBRowList</code> and populates the column names of
 * the <code>DBRowList</code> with the contents of the List.
 * <p> Column names will be stored in the <code>DBRowList</code> sorted ascending.
 * <p> All column names within the List will be converted to upper case.
 * @param aColumnNames A List of column names.
 * @since 1.0
 */
  public DBRowList(final List aColumnNames) {
    super(aColumnNames);
  }

  /**
   * Constructs a <code>DBRowList</code> and populates the first element,
   * represented by a Map, within the <code>DBRowList</code>. The Map
   * should contain an entry for each column name / column value to be used to
   * initialize the first row within the <code>DBRowList</code>.
   * <p>
   * Column names will be stored in the <code>DBRowList</code> sorted
   * ascending.
   * <p>
   * All column names within the Map will be converted to upper case.
   * <p>
   *
   * @param aRow
   *            A Map containing the column name / column value pairs to
   *            populate the first row of the <code>DBRowList</code>.
   * @throws IllegalArgumentException
   *             Thrown if the number of columns in the Map do not match the
   *             number of columns in the <code>DBRowList</code> or if the
   *             column names in the Map do not exist in the
   *             <code>DBRowList</code>.
   * @since 1.0
   */
  public DBRowList(final Map aRow) {
    super(aRow);
  }

  /**
   * Constructs a <code>DBRowList</code> from the rows of a
   * <code>ResultSet</code>.
   *
   * @param result
   *            The <code>ResultSet</code> to construct a
   *            <code>DBRowList</code> from.
   * @throws SQLException
   *             Thrown if the <code>ResultSet</code> could not be read.
   * @since 1.0
   */
  public DBRowList(final ResultSet result) throws SQLException {
    super(extractColumnNames(result));
    copy(result);
  }

  private void copy(final ResultSet result) throws SQLException {
    int columnCount= getColumnCount();
    while (result.next()) {
      add(copyRow(result, columnCount));
    }
  }  // create


  private static List extractColumnNames(final ResultSet result) throws SQLException {
    ListOfString r= DbUtil.columnNamesAsList(result);
    if (r==null) {
      throw new SQLException("Cannot read column names from a result set.");
    }
    return r;
  }  // extractColumnNames

  private static Object[] copyRow(final ResultSet aResult,
                                  final int aColumnCount) throws SQLException {
    Object[] row=new Object[aColumnCount];
    for (int i=0; i<aColumnCount; i++) {
      row[i]=aResult.getObject(i+1);
    }
    return row;
  }

  /**
   * Gets the value of the designated column in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>Timestamp</code> in the Java programming language.
   * <p>
   * Rows are numbered from 0. The <code>columnName</code> value is
   * converted to uppercase, making this operation case insensitive.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnName
   *            The name of the column to get.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
   * @throws ClassCastException
   *             Thrown if the column cannot be casted to a
   *             <code>Timestamp</code>.
   * @throws IllegalArgumentException
   *             Thrown if the column name does not exist in the
   *             <code>RowList</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public Timestamp getTimestamp(final int rowIndex, final String columnName) {
    return ( (Timestamp) getObject(rowIndex, columnName));
  }

  /**
   * Gets the value of the designated column,identified by the
   * <code>columnIndex</code>, in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>java.sql.Timestamp</code> in the Java programming language.
   * <p>
   * Rows are numbered from 0.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnIndex
   *            The index of the column to get.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code> or
   *             <code>columnIndex</code> is out of range.
   *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
   * @throws ClassCastException
   *             Thrown if the column cannot be casted to a
   *             <code>java.sql.Timestamp</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public Timestamp getTimestamp(final int rowIndex, final int columnIndex) {
    return (Timestamp) getObject(rowIndex, columnIndex);
  }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue Timestamp The default value to use if the column does not exist.
 * @return Timestamp the column value, or aDefaultValue if the column does not exist.
 */
  public Timestamp getTimestamp(final int aRowIndex,
                                final String aColumnName,
                                final Timestamp aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getTimestamp(aRowIndex, aColumnName);
  }

  /**
   * Gets the value of the designated column in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>java.sql.Date</code> in the Java programming language.
   * <p>
   * Rows are numbered from 0. The <code>columnName</code> value is
   * converted to uppercase, making this operation case insensitive.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnName
   *            The name of the column to get.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
   * @throws ClassCastException
   *             Thrown if the column cannot be casted to a
   *             <code>java.sql.Date</code>.
   * @throws IllegalArgumentException
   *             Thrown if the column name does not exist in the
   *             <code>RowList</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public java.sql.Date getSqlDate(final int rowIndex, final String columnName) {
    return ( (java.sql.Date) getObject(rowIndex, columnName));
  }

  /**
   * Gets the value of the designated column,identified by the
   * <code>columnIndex</code>, in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>java.sql.Date</code> in the Java programming language.
   * <p>
   * Rows are numbered from 0.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnIndex
   *            The index of the column to get.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code> or
   *             <code>columnIndex</code> is out of range.
   *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
   * @throws ClassCastException
   *             Thrown if the column cannot be casted to a
   *             <code>java.sql.Date</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public java.sql.Date getSqlDate(final int rowIndex, final int columnIndex) {
    return (java.sql.Date) getObject(rowIndex, columnIndex);
  }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue java.sql.Date The default value to use if the column does not exist.
 * @return java.sql.Date the column value, or aDefaultValue if the column does not exist.
 */
  public java.sql.Date getSqlDate(final int aRowIndex,
                                final String aColumnName,
                                final java.sql.Date aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getSqlDate(aRowIndex, aColumnName);
  }


/**
 * Gets the name of the table.
 * @return Returns the name of the table. Can return <code>""</code>.
 * @since 1.0
 */
  public String getTableName() {
    return pTableName;
  }

/**
 * Sets the name of the table to the RowList.
 * @param tableName The tableName to set.
 * @since 1.0
 */
  public void setTableName(final String tableName) {
    this.pTableName = tableName;
  }

/**
 * Gets the schema name of the database that this row represents.
 * @return The schemaNamae name. Can return <code>""</code>.
 */
  public String getSchemaName() {
    return pSchemaName;
  }

/**
 * Sets the schema name of the database that this row represents. The
 * <code>schemaName</code> value is converted to uppercase, making this
 * operation case insensitive.
 * @param string The schema name. Can be <code>null</code>.
 */
  public void setSchemaName(final String string) {
    this.pSchemaName = string;
  }

/**
 * Gets the database name of the database that this row represents.
 * @return The database name. Can return <code>null</code>.
 */
  public String getDatabaseName() {
    return pDatabaseName;
  }

  /**
   * Sets the database name of the database that this row represents. The
   * <code>databaseName</code> value is converted to uppercase, making this
   * operation case insensitive.
   * @param string The database name. Can be <code>null</code>.
   */
  public void setDatabaseName(final String string) {
    this.pDatabaseName = string;
  }


}
