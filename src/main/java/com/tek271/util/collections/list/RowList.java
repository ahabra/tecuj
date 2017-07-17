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

package com.tek271.util.collections.list;

import java.io.*;
import java.util.*;
import org.apache.commons.lang3.math.NumberUtils;
import com.tek271.util.string.StringUtility;
import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.reflect.builder.*;

/**
 * <p>
 * A <code>RowList</code> represents a growable table of data. A row
 * within the <code>RowList</code> can contain many columns, however,
 * each row in the <code>RowList</code> will contain the same number
 * of columns. A column within a <code>RowList</code> consists of a
 * column name and a column value.
 * <p>
 * A <code>RowList</code> can be populated from a Map, an array of
 * Objects, or a List by invoking the appropriate <code>add</code>
 * method.
 * <p>
 * The <code>RowList</code> class provides <code>getXXX</code>
 * methods for retrieving column values from a row. Values can be
 * retrieved using either the index number of the row, which returns a
 * Map where the key is the column name and the value is the column
 * value, or by supplying the index number and the column name. Rows are
 * numbered from 0.
 * <p>
 * For the <code>getXXX</code> methods, the <code>RowList</code>
 * attempts to convert the underlying data to the Java type specified in
 * the <code>XXX</code> part of the <code>getXXX</code> method and
 * returns a suitable Java value.
 * <p>
 * When a <code>getXXX</code> method is called with a column name and
 * several columns have the same name, the value of the first matching
 * column will be returned.
 * @version 1.0
 * @since 1.0
 * @author Doug Estep, Abdul Habra
 */
public class RowList implements Serializable {

  /**
   * Used by the <code>indexOf</code> methods in finding rows. If set to
   * <code>true</code>, the <code>indexOf</code> method will take the
   * case of the value into consideration. If set to <code>false</code>,
   * the <code>indexOf</code> method will select the row regardless of the
   * case.
   * <p>
   * This value is set to <code>true</code> by default.
   *
   * @since 1.0
   */
  
  private static final long serialVersionUID = 3;
  
  public boolean isCaseSensitive=true;

  public static final String DESC="DESC";
  public static final String ASC="ASC";

  private static final String pDEFAULT_ROW_COUNTER="[ROW_COUNTER]";

  private List pRows=new ArrayList(64);
  private List pColumnNames=new ArrayList();
  private Map pColumnHeaders=new HashMap();
  private int pColumnCount;
  private ColumnDescriptor[] pSortedColumnDescriptors;
  private static final int pSORT_DESC=-1;
  private static final int pSORT_ASC=1;

  // Fields for supporting row counter
  private ListOfString pRowCounter;
  private boolean pIsUseRowCounter=false;
  private String pRowCounterName=pDEFAULT_ROW_COUNTER;
  private int pRowCounterStartIndex=1;

  private Class pRowInterface;
  private transient BeanFromRowBuilder pBeanFromRowBuilder;

  private static class ColumnDescriptor implements Comparable, Serializable {
    String name;
    int index;

    ColumnDescriptor(String aName, int aIndex) {
      name=aName;
      index=aIndex;
    }

    public int compareTo(Object o) {
      ColumnDescriptor target= (ColumnDescriptor) o;
      return name.compareTo(target.name);
    }
    
    public boolean equals(Object o) {
      if (o==null) return false;
      if (this==o) return true;
      if (! (o instanceof ColumnDescriptor) ) return false;
      ColumnDescriptor cd= (ColumnDescriptor) o;
      return StringUtility.equals(name, cd.name);
    }
    
    public int hashCode() {
      return name.hashCode();
    }
    
  } // inner class ColumnDescriptor

  /**
   * Constructs a <code>RowList</code>, setting the number of columns for
   * each row. This constructor will set the column names to the String
   * representation of the column number. Clients that construct this object
   * using this constructor should use the <code>add(List)</code> method to
   * add rows to this <code>RowList</code>.
   * <p>
   * @param aColumnCount The number of columns to initiate the <code>RowList</code> with.
   * @since 1.0
   */
  public RowList(final int aColumnCount) {
    this.pColumnCount=aColumnCount;
    for (int x=0; x<aColumnCount; x++) {
      pColumnNames.add(String.valueOf(x));
    }
    createSortedColumnDescriptors();
  }

  /**
   * Constructs a <code>RowList</code> and populates the column names of the
   * <code>RowList</code> with the contents of the List.
   * <p>
   * Column names will be stored in the <code>RowList</code> sorted
   * ascending, and converted to Upper Case.
   * <p>
   *
   * @param aColumnNames A List of column names.
   * @since 1.0
   */
  public RowList(final List aColumnNames) {
    pColumnCount=aColumnNames.size();
    for (int x=0; x<pColumnCount; x++) {
      String column= (String) aColumnNames.get(x);
      pColumnNames.add(column.toUpperCase());
    }
    createSortedColumnDescriptors();
  }

  /**
   * Constructs a <code>RowList</code> and populates the first element,
   * represented by a Map, within the <code>RowList</code>. The Map should
   * contain an entry for each column name / column value to be used to
   * initialize the first row within the <code>RowList</code>.
   * <p>
   * Column names will be stored in the <code>RowList</code> sorted
   * ascending.
   * <p>
   *
   * @param row
   *            A Map containing the column name / column value pairs to
   *            populate the first row of the <code>RowList</code>.
   * @throws IllegalArgumentException
   *             Thrown if the number of columns in the Map do not match the
   *             number of columns in the <code>RowList</code> or if the
   *             column names in the Map do not exist in the
   *             <code>RowList</code>.
   * @since 1.0
   */
  public RowList(final Map row) {
    pColumnCount=row.size();
    for (Iterator recordIt=row.entrySet().iterator(); recordIt.hasNext(); ) {
      Map.Entry entry= (Map.Entry) recordIt.next();
      String columnName= ( (String) entry.getKey());
      pColumnNames.add(columnName.toUpperCase());
    }
    createSortedColumnDescriptors();
    add(row);
  }

  private void assertRowSize(final int aRowSize) {
    if (aRowSize==pColumnCount)return;

    String msg="Column count in the added row ("+aRowSize+
        ") must match the initial column count("+pColumnCount+")";
    throw new IllegalArgumentException(msg);
  }

  /**
   * Adds a row, represented by a Map, into the <code>RowList</code>. The
   * Map should contain an entry for each column name / column value which
   * makes up the row being added to the <code>RowList</code>.
   * <p>
   *
   * @param row
   *            A Map containing the column name / column value pairs to add
   *            to the <code>RowList</code>.
   * @throws IllegalArgumentException
   *             Thrown if the number of columns in the Map do not match the
   *             number of columns in the <code>RowList</code> or if the
   *             column names in the Map do not exist in the
   *             <code>RowList</code>.
   * @since 1.0
   */
  public void add(final Map row) {
    assertRowSize(row.size());
    Object[] rowData=new Object[pColumnCount];
    for (Iterator it=row.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry entry= (Map.Entry) it.next();
      String columnName= ( (String) entry.getKey()).toUpperCase();
      int colIndex=indexOfColumn(columnName);
      assertColumnIndex(colIndex, columnName);
      rowData[colIndex]=entry.getValue();
    }
    pRows.add(rowData);
  }

  private void assertColumnIndex(final int aColIndex, final String aColName) {
    if (aColIndex>=0)return;

    String msg="Column name="+aColName+
        " does not exist in the initial column names.";
    throw new IllegalArgumentException(msg);
  }

  /**
   * Adds a row to the <code>RowList</code>. The List should contain a
   * value for each column name within the <code>RowList</code>. The
   * positioning of each value with the List must correspond one-to-one with
   * the positioning of their respective SORTED column name.
   * <p>
   *
   * @param values
   *            A List containing the column values. Each element within the
   *            List corresponds to one column value.
   * @throws IllegalArgumentException
   *             Thrown if the number of columns in the List do not match the
   *             number of columns in the <code>RowList</code>.
   * @since 1.0
   */
  public void add(final List values) {
    assertRowSize(values.size());
    Object[] rowData=values.toArray();
    pRows.add(rowData);
    appendToRowCounterList();
  }

  /**
   * Adds a row to the RowList. The array should contain a value for each
   * column name within the RowList. The positioning of each value with the
   * array must correspond one-to-one with the positioning of their respective
   * SORTED column name.
   * <p>
   *
   * @param rowData
   *            An array of objects. Each element within the array corresponds
   *            to one column value.
   * @throws IllegalArgumentException
   *             Thrown if the number of columns in the array do not match the
   *             number of columns in the <code>RowList</code>.
   * @since 1.0
   */
  public void add(final Object[] rowData) {
    assertRowSize(rowData.length);
    pRows.add(rowData);
    appendToRowCounterList();
  }

  /**
   * Deletes the row, identified by <code>aRow</code>, from the RowList.
   * @param aRowIndex The row number.
   */
  public void delete(final int aRowIndex) {
    if (aRowIndex<0||aRowIndex>=size()) {
      return;
    }
    pRows.remove(aRowIndex);
    if (pIsUseRowCounter) pRowCounter.remove(aRowIndex);
  }

  /**
   * Gets the row at the specified index.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @return Returns a Map containing an entry for each column name / column
   *         value found at the row identified by <code>rowIndex</code>. If the
   *         pIsUseRowCounter flag is true, it will return the row counter value.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown when <code>rowIndex</code> is out of range
   *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
   * @since 1.0
   */
  public Map get(final int rowIndex) {
    HashMap map=new HashMap();
    Object[] rowData=getRowData(rowIndex);
    for (int x=0; x<pColumnCount; x++) {
      map.put(pColumnNames.get(x), rowData[x]);
    }
    if (pIsUseRowCounter) {
      map.put(pRowCounterName, pRowCounter.getItem(rowIndex));
    }
    return map;
  }

  /**
   * Gets a row from the RowList. <p>
   * @param rowIndex The index within the <code>RowList</code>.
   * @return Array of objects. Each element corresponds to one column value. Will not
   * return the row counter value.
   * @since 1.0
   */
  public Object[] getRowData(final int rowIndex) {
    return (Object[]) pRows.get(rowIndex);
  }

  /**
   * Determines if the column name exists within the <code>RowList</code>.
   *
   * @param columnName
   *            The column name.
   * @return boolean - Returns <code>true</code> if the column name exists;
   *         <code>false</code> if not.
   * @since 1.0
   */
  public boolean isColumnExist(String columnName) {
    if (columnName==null) {
      return false;
    }
    ColumnDescriptor searchKey=new ColumnDescriptor(columnName.toUpperCase(), 0);
    int i=Arrays.binarySearch(pSortedColumnDescriptors, searchKey);
    return i>=0;
  }

  /**
   * Gets the value of the designated column in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>String</code> in the Java programming language.
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
   * @throws IllegalArgumentException
   *             Thrown if the column name does not exist in the
   *             <code>RowList</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public String getString(int rowIndex, String columnName) {
    return (String) getObject(rowIndex, columnName);
  }

  /**
   * Gets the value of the designated column,identified by the
   * <code>columnIndex</code>, in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>String</code> in the Java programming language.
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
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 1.0
   */
  public String getString(final int rowIndex, final int columnIndex) {
    return (String) getObject(rowIndex, columnIndex);
  }

  /**
   * Gets the value of the designated column,identified by the
   * <code>columnIndex</code>, in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>String</code> in the Java programming language. Has the ability
   * to force the value to be converted toString().
   * <p>
   * Rows are numbered from 0.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnIndex
   *            The index of the column to get.
   * @param isForce
   *            If true, and if the value is not a String object, the
   *            toString() of this value is returned.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code> or
   *             <code>columnIndex</code> is out of range.
   *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 2.0
   */
  public String getString(final int rowIndex, final int columnIndex,
                          final boolean isForce) {
    if (!isForce)return getString(rowIndex, columnIndex);
    Object v=getObject(rowIndex, columnIndex);
    if (v==null)return null;
    if (v instanceof String)return (String) v;
    return v.toString();
  }

  /**
   * Gets the value of the designated column in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as a
   * <code>String</code> in the Java programming language.
   * Has the ability to force the value to be converted toString().
   * <p>
   * Rows are numbered from 0. The <code>columnName</code> value is
   * converted to uppercase, making this operation case insensitive.
   * <p>
   *
   * @param rowIndex
   *            The index within the <code>RowList</code>.
   * @param columnName
   *            The name of the column to get.
   * @param isForce
   *            If true, and if the value is not a String object, the toString() of
   *            this value is returned.
   * @throws ArrayIndexOutOfBoundsException
   *             Thrown if the <code>rowIndex</code> is out of range.
   *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
   * @throws IllegalArgumentException
   *             Thrown if the column name does not exist in the
   *             <code>RowList</code>.
   * @return Returns the column value; if the value of the column in the
   *         <code>RowList</code> is <code>null</code>, the value
   *         returned is <code>null</code>
   * @since 2.0
   */
  public String getString(final int rowIndex, final String columnName,
                          final boolean isForce) {
    if (!isForce)return getString(rowIndex, columnName);
    Object v=getObject(rowIndex, columnName);
    if (v==null)return null;
    if (v instanceof String)return (String) v;
    return v.toString();
  }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aIsForce If true, and if the value is not a String object, the toString() of
 *    this value is returned.
 * @param aDefaultValue String The default value to use if the column does not exist.
 * @return String the column value, or aDefaultValue if the column does not exist.
 */
  public String getString(final int aRowIndex,
                          final String aColumnName,
                          final boolean aIsForce,
                          final String aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getString(aRowIndex, aColumnName, aIsForce);
  }

  /**
   * Gets the value of the designated column in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as an
   * <code>int</code> in the Java programming language.
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
   * @throws IllegalArgumentException
   *             Thrown if the column name does not exist in the
   *             <code>RowList</code>.
   * @return Returns the column value.
   * @since 1.0
   */
  public int getInt(int rowIndex, String columnName) {
    Object o=getObject(rowIndex, columnName.toUpperCase());
    if (o==null)
      return 0;
    return Integer.parseInt(o.toString());
  }

  /**
   * Gets the value of the designated column,identified by the
   * <code>columnIndex</code>, in the row identified by
   * <code>rowIndex</code> of this <code>RowList</code> object as an
   * <code>int</code> in the Java programming language.
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
   * @return Returns the column value.
   * @since 1.0
   */
  public int getInt(final int rowIndex, final int columnIndex) {
    Object o=getObject(rowIndex, columnIndex);
    if (o==null) {
      return 0;
    }
    return Integer.parseInt(o.toString());
  }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue int The default value to use if the column does not exist.
 * @return int the column value, or aDefaultValue if the column does not exist.
 */
  public int getInt(final int aRowIndex,
                    final String aColumnName,
                    final int aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getInt(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>boolean</code> in the Java programming language.
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
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public boolean getBoolean(int rowIndex, String columnName) {
        Object o = getObject(rowIndex, columnName);
        if (o == null) {
            return false;
        }
        return Boolean.valueOf(o.toString()).booleanValue();
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>columnIndex</code>, in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>boolean</code> in the Java programming language.
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
     * @return Returns the column value.
     * @since 1.0
     */
    public boolean getBoolean(final int rowIndex, final int columnIndex) {
        Object o = getObject(rowIndex, columnIndex);
        if (o == null) {
            return false;
        }
        return Boolean.valueOf(o.toString()).booleanValue();
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue boolean The default value to use if the column does not exist.
 * @return boolean the column value, or aDefaultValue if the column does not exist.
 */
  public boolean getBoolean(final int aRowIndex,
                            final String aColumnName,
                            final boolean aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getBoolean(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>long</code> in the Java programming language.
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
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public long getLong(int rowIndex, String columnName) {
        Object o = getObject(rowIndex, columnName);
        if (o == null) {
            return 0l;
        }
        return Long.parseLong(o.toString());
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>columnIndex</code>, in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>long</code> in the Java programming language.
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
     * @return Returns the column value.
     * @since 1.0
     */
    public long getLong(final int rowIndex, final int columnIndex) {
        Object o = getObject(rowIndex, columnIndex);
        if (o == null) {
            return 0l;
        }
        return Long.parseLong(o.toString());
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue long The default value to use if the column does not exist.
 * @return long the column value, or aDefaultValue if the column does not exist.
 */
  public long getLong(final int aRowIndex,
                      final String aColumnName,
                      final long aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getLong(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>double</code> in the Java programming language.
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
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public double getDouble(int rowIndex, String columnName) {
        Object o = getObject(rowIndex, columnName);
        if (o == null) {
            return 0.0d;
        }
        return Double.parseDouble(o.toString());
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>columnIndex</code>, in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>double</code> in the Java programming language.
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
     * @return Returns the column value.
     * @since 1.0
     */
    public double getDouble(final int rowIndex, final int columnIndex) {
        Object o = getObject(rowIndex, columnIndex);
        if (o == null) {
            return 0.0d;
        }
        return Double.parseDouble(o.toString());
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue double The default value to use if the column does not exist.
 * @return double the column value, or aDefaultValue if the column does not exist.
 */
  public double getDouble(final int aRowIndex,
                          final String aColumnName,
                          final double aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getDouble(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>float</code> in the Java programming language.
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
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public float getFloat(int rowIndex, String columnName) {
        Object o = getObject(rowIndex, columnName);
        if (o == null) {
            return 0.0f;
        }
        return Float.parseFloat(o.toString());
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>columnIndex</code>, in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>float</code> in the Java programming language.
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
     * @return Returns the column value.
     * @since 1.0
     */
    public float getFloat(final int rowIndex, final int columnIndex) {
        Object o = getObject(rowIndex, columnIndex);
        if (o == null) {
            return 0.0f;
        }
        return Float.parseFloat(o.toString());
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue float The default value to use if the column does not exist.
 * @return float the column value, or aDefaultValue if the column does not exist.
 */
  public float getFloat(final int aRowIndex,
                        final String aColumnName,
                        final float aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getFloat(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>java.util.Date</code> in the Java programming language.
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
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @throws ClassCastException
     *             Thrown if the column cannot be casted to a
     *             <code>java.util.Date</code>.
     * @return Returns the column value; if the value of the column in the
     *         <code>RowList</code> is <code>null</code>, the value
     *         returned is <code>null</code>
     * @since 1.0
     */
    public Date getDate(int rowIndex, String columnName) {
        return (Date) getObject(rowIndex, columnName);
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>columnIndex</code>, in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as a
     * <code>java.util.Date</code> in the Java programming language.
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
     *             <code>java.util.Date</code>.
     * @return Returns the column value; if the value of the column in the
     *         <code>RowList</code> is <code>null</code>, the value
     *         returned is <code>null</code>
     * @since 1.0
     */
    public Date getDate(final int rowIndex, final int columnIndex) {
        return (Date) getObject(rowIndex, columnIndex);
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue Date The default value to use if the column does not exist.
 * @return Date the column value, or aDefaultValue if the column does not exist.
 */
  public Date getDate(final int aRowIndex,
                      final String aColumnName,
                      final Date aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getDate(aRowIndex, aColumnName);
  }

    /**
     * Gets the value of the designated column in the row identified by
     * <code>rowIndex</code> of this <code>RowList</code> object as an
     * <code>Object</code> in the Java programming language.
     * <p>
     * Rows are numbered from 0.
     * <p>
     *
     * @param rowIndex
     *            The index within the <code>RowList</code>.
     * @param columnName
     *            The name of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>rowIndex</code> is out of range.
     *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value; if the value of the column in the
     *         <code>RowList</code> is <code>null</code>, the value
     *         returned is <code>null</code>. If you pass the row counter column name,
     *         the row counter value is returned.
     * @since 1.0
     */
    public Object getObject(int rowIndex, String columnName) {
        if (columnName == null) {
            throw new NullPointerException(
                             "Please provide a value for the column name");
        }
        if (pIsUseRowCounter && columnName.equalsIgnoreCase(pRowCounterName) ) {
          return pRowCounter.getItem(rowIndex);
        }

        columnName = columnName.toUpperCase();
        int colIndex = indexOfColumn(columnName);
        assertColumnIndex(colIndex, columnName);
        return getObject(rowIndex, colIndex);
    }

    /**
     * Get the value for the given column index at the row identified by
     * rowIndex. This will not return the row counter.
     *
     * @param rowIndex The index within the RowList to get the row from.
     * @param columnIndex The index of the column to get.
     * @return The found value as an Object.
     */
    public Object getObject(int rowIndex, int columnIndex) {
        Object[] row = getRowData(rowIndex);
        return row[columnIndex];
    }

/**
 * Get the value of the given column.
 * @param aRowIndex int The index within the <code>RowList</code>.
 * @param aColumnName String The name of the column to get.
 * @param aDefaultValue Object The default value to use if the column does not exist.
 * @return Object the column value, or aDefaultValue if the column does not exist.
 */
  public Object getObject(final int aRowIndex,
                          final String aColumnName,
                          final Object aDefaultValue) {
    if (! isColumnExist(aColumnName)) return aDefaultValue;
    return getObject(aRowIndex, aColumnName);
  }


    /**
     * Returns all rows as a List. Each element of the List contains an array of
     * Objects. Each element in the array of Objects contains a column value for
     * the row.
     */
    public List getAllRows() {
        return pRows;
    }

    /**
     * Replaces the value of the specified column at the specified position in
     * this <code>RowList</code> with the specified element.
     * <p>
     * Rows are numbered from 0.
     * <p>
     *
     * @param rowIndex
     *            The index within the <code>RowList</code>.
     * @param columnName
     *            The name of the column.
     * @param element
     *            element to be stored at the specified position.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>rowIndex</code> is out of range.
     *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @since 1.0
     */
    public void setObject(int rowIndex, String columnName, Object element) {
        if (columnName == null) {
            throw new NullPointerException("Please provide a value "
                    + "for the column name");
        }
        columnName = columnName.toUpperCase();
        int colIndex = indexOfColumn(columnName);
        assertColumnIndex(colIndex, columnName);
        setObject(rowIndex, colIndex, element);
    }

    /**
     * Replaces the value of the specified column at the specified position in
     * this <code>RowList</code> with the specified element.
     * <p>
     * Rows are numbered from 0.
     * @param rowIndex The index within the <code>RowList</code>.
     * @param columnIndex The name of the column.
     * @param element element to be stored at the specified position.
     * @throws ArrayIndexOutOfBoundsException Thrown if the <code>rowIndex</code> is out of range.
     *             <code>(rowIndex < 0 || rowIndex >= size())</code>.
     * @since 2.0
     */
    public void setObject(final int rowIndex, final int columnIndex,
                          final Object element) {
        Object[] row = getRowData(rowIndex);
        row[columnIndex] = element;
    }

    /**
     * Gets the List of column names.
     * @return Returns the List of column names.
     * @since 1.0
     */
    public List getColumnNames() {
        return pColumnNames;
    }

  /**
   * Gets the Map of column headers for display on reports or screens.  The key
   * to the Map is the column name and the value is the column header.
   *
   * @return A Map of column headers. Returns empty Map if no column headers
   *         were set.
   */
    public Map getColumnHeaders() {
        return pColumnHeaders;
    }

  /**
   * Sets the Map of column headers to be used on reports or screens. The key
   * to the Map is the column name and the value is the column header.
   *
   * @param aColumnHeaders
   *            The Map of column headers.
   */
    public void setColumnHeaders(Map aColumnHeaders) {
        if (aColumnHeaders == null) {
            return;
        }
        pColumnHeaders = aColumnHeaders;
    }

/** Get a list of column headers */
    public List getColumnHeadersList() {
      List r= new ArrayList(pColumnCount);
      for (int i=0; i<pColumnCount; i++) {
        r.add( pColumnHeaders.get( pColumnNames.get(i) ) );
      }
      return r;
    }

/** Set a list of column headers */
    public void setColumnHeadersList(final List aHeaders) {
      for (int i=0, n= aHeaders.size(); i< n; i++) {
        String k= (String) pColumnNames.get(i);
        pColumnHeaders.put(k, aHeaders.get(i));
      }
    }

    /**
     * Returns the number of rows within this <code>RowList</code>.
     * <p>
     *
     * @return Returns the number of rows within this <code>RowList</code>.
     * @since 1.0
     */
    public int size() {
        return pRows.size();
    }

    /**
     * Gets the number of columns within the <code>RowList</code>.
     * <p>
     *
     * @return Returns the number of columns.
     * @since 1.0
     */
    public int getColumnCount() {
        return pColumnCount;
    }

    /**
     * Determines if the <code>RowList</code> is empty.
     * <p>
     *
     * @return boolean Returns <code>true</code> if the <code>RowList</code>
     *         is empty; <code>false</code> if not.
     * @since 1.0
     */
    public boolean isEmpty() {
        return pRows.isEmpty();
    }



    /**
     * Returns the String representation of the RowList. The first line of the
     * output will contain the column names separated by a tab character. The
     * remaining lines will be the values of the columns, separated by a tab
     * character.
     * <p>
     *
     * @return String Returns the string representation of the RowList or empty
     *         string ("") if the RowList is empty.
     */
    public String toString() {
        return toString(StringUtility.TAB, StringUtility.NEW_LINE);
    }

    /**
     * Returns the String representation of the RowList. The first line of the
     * output will contain the column names separated by a aColumnSeparator. The
     * remaining lines will be the values of the columns, separated by a
     * aColumnSeparator. Rows will be separated by aRowSeparator.
     * <p>
     *
     * @return the string representation of the RowList or empty string ("") if
     *         the RowList is empty.
     */
    public String toString(final String aColumnSeparator,
                           final String aRowSeparator) {
        if (pRows.isEmpty()) return StringUtility.EMPTY;

        StringBuffer buff = new StringBuffer(128);
        Object[] names= pColumnNames.toArray();
        if (pIsUseRowCounter) {
          names= (Object[])  ArrayUtilities.concat(new String[] {pRowCounterName}, names);
        }
        appendArrayToBuffer(buff, names, aColumnSeparator);

        int n = size() - 1;
        for (int x = 0; x < n; x++) {
            buff.append(aRowSeparator);
            appendRowToBuffer(buff, x, aColumnSeparator);
        }
        buff.append(aRowSeparator);
        appendRowToBuffer(buff, n, aColumnSeparator);
        return buff.toString();
    }

    private void appendRowToBuffer(final StringBuffer aBuf,
                                   final int aRowIndex,
                                   final String aColumnSeparator) {
      if (pIsUseRowCounter) {
        aBuf.append(pRowCounter.getItem(aRowIndex));
        aBuf.append(aColumnSeparator);
      }
      appendArrayToBuffer(aBuf, getRowData(aRowIndex), aColumnSeparator);
    }

    private void appendArrayToBuffer(final StringBuffer aBuf,
                                     final Object[] aRow,
                                     final String aColumnSeparator) {
        for (int i=0, n=aRow.length; i < n; i++) {
            aBuf.append(aRow[i]);
            if (i < n - 1)
                aBuf.append(aColumnSeparator);
        }
    }

/** Convert the given row to a string */
    public String rowToString(final int aRowIndex, final String aColumnSeparator) {
      StringBuffer b= new StringBuffer(64);
      appendRowToBuffer(b, aRowIndex, aColumnSeparator);
      return b.toString();
    }

/** Convert the given row to a string */
    public String rowToString(final int aRowIndex) {
      return rowToString(aRowIndex, StringUtility.NEW_LINE);
    }

    /**
     * Checks if the object is equal to this object.
     *
     * @param o
     *            The object to compare to this object.
     * @return Returns true if the objects are equal; false if not.
     * @since 1.0
     */
    public boolean equals(Object o) {
      if (o == null) return false;
      if (o==this)  return true;
      if (! (o instanceof RowList)) return false;
      return (toString().equals(o.toString()));
    }

    /**
     * Returns the hash code for this RowList.
     *
     * @return the hash code.
     * @since 1.0
     */
    public int hashCode() {
        return toString().hashCode();
    }

    private void createSortedColumnDescriptors() {
      pSortedColumnDescriptors=new ColumnDescriptor[pColumnCount];
      for (int i=0; i<pColumnCount; i++) {
        ColumnDescriptor t=new ColumnDescriptor( (String) pColumnNames.get(i), i);
        pSortedColumnDescriptors[i]=t;
      }
      Arrays.sort(pSortedColumnDescriptors);
    }

    private int indexOfColumn(String columnName) {
      ColumnDescriptor searchKey=new ColumnDescriptor(columnName, 0);
      int i=Arrays.binarySearch(pSortedColumnDescriptors, searchKey);
      if (i<0) {
        throw new IllegalArgumentException("ColumnName ("+columnName+ ") not found");
      }
      //
      return pSortedColumnDescriptors[i].index;
    }

/*
 * Check if the value in the rowlist at the given (rowIndex, ColumnName) is
 * equal to the given aColumnValue.
 * @param aRowIndex The index of the row to inspect.
 * @param aColumnName The name of the column to inspect
 * @param aColumnValue The value to compare against.
 * @return true the value in the list equals aColumnValue
 */
    private boolean checkColumn(final int aRowIndex,
                                final String aColumnName,
                                final Object aColumnValue) {
      Object v = getObject(aRowIndex, aColumnName);
      if (v instanceof String) {
        return StringUtility.equals((String)v, (String)aColumnValue, isCaseSensitive);
      }
      return v.equals(aColumnValue);
    } // checkColumn

/*
 * Check if the row at the given index has column values equal to the values
 * in aFilters
 * @param aRowIndex The index of the row to inspect.
 * @param aFilters A map of (columnName, value) pairs
 * @return true if the row matches the given filter.
 */
    private boolean checkRow(final int aRowIndex, final Map aFilters) {
        Set set = aFilters.entrySet();
        Map.Entry filter;
        String colName;
        Object colVal;
        for (Iterator i = set.iterator(); i.hasNext();) {
            filter = (Map.Entry) i.next();
            colName = (String) filter.getKey();
            colVal = filter.getValue();
            if (!checkColumn(aRowIndex, colName, colVal)) return false;
        }
        return true;
    } // checkRow

/*
 * Check if the row at the given index has column values equal to the values
 * in aFilters
 * @param aRowIndex The index of the row to inspect.
 * @param aFilters An array of column values sorted by column name.
 * @return true if the row matches the given filter.
 */
    private boolean checkRow(final int aRowIndex, final String[] aFilters) {
        int n = Math.min(aFilters.length, getColumnCount());
        Object[] row = getRowData(aRowIndex);
        String v;
        for (int i = 0; i < n; i++) {
            v = row[i].toString();
            if (!StringUtility.equals(aFilters[i], v, isCaseSensitive))
                return false;
        }
        return true;
    } // checkRow

    /**
     * Find the index of the FIRST row that matches the filter values.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilters
     *            A map of (columnName, value) pairs to look for.
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final Map aFilters) {
        for (int i = 0, n = size(); i < n; i++) {
            if (checkRow(i, aFilters)) {
                return i;
            }
        }
        return -1;
    } // indexOf

    /**
     * Locates the index of the FIRST row that matches the filter values.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilters
     *            An array of ordered column values to look for.
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final String[] aFilters) {
        for (int i = 0, n = size(); i < n; i++) {
            if (checkRow(i, aFilters)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Locates the index of the FIRST row that has its first column equals to
     * aFilter1.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilter1
     *            The value of the first column
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final String aFilter1) {
        return indexOf(new String[] { aFilter1 });
    }

    /**
     * Locates the index of the FIRST row that has its 1,2 column equals to
     * aFilter1, aFilter2 respectivly.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilter1
     *            The value of the first column
     * @param aFilter2
     *            The value of the second column
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final String aFilter1, final String aFilter2) {
        return indexOf(new String[] { aFilter1, aFilter2 });
    }

    /**
     * Locates the index of the FIRST row that has its 1,2,3 column equals to
     * aFilter1, aFilter2, aFilter3 respectivly.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilter1
     *            The value of the first column
     * @param aFilter2
     *            The value of the second column
     * @param aFilter3
     *            The value of the thirs column
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final String aFilter1, final String aFilter2,
            final String aFilter3) {
        return indexOf(new String[] { aFilter1, aFilter2, aFilter3 });
    }

    /**
     * Locates the index of the FIRST row that has its 1,2,3, 4 column equals to
     * aFilter1, aFilter2, aFilter3, aFilter4 respectivly.
     * <p>
     * Uses the value of the <code>isCaseSensitive</code> property when
     * searching for the row.
     * <p>
     *
     * @param aFilter1
     *            The value of the first column
     * @param aFilter2
     *            The value of the second column
     * @param aFilter3
     *            The value of the thirs column
     * @return index of the first found row, -1 if not found.
     * @since 1.0
     */
    public int indexOf(final String aFilter1, final String aFilter2,
            final String aFilter3, final String aFilter4) {
        return indexOf(new String[] { aFilter1, aFilter2, aFilter3, aFilter4 });
    }

    /**
     * Takes a <code>String</code> of SQL style order by syntax and sorts the
     * <code>RowList</code>. If passing in column indexes, remember the
     * columns start index is 0.
     *
     * Example: myRowList.sortSql("col1 desc, col2 asc);
     *
     * The delimiter can be either ', ' or ',' between the columns. The delimiter
     * between column name/index and sort order is ' '.
     *
     * @param aSortOrder
     *            This is an SQL like syntax string specifying
     * @since 2.0
     */
    public void orderBy(final String aSortOrder) {
      ListOfString list=new ListOfString();
      list.lineSeparator=StringUtility.COMMA;
      list.setText(aSortOrder, true);

      int size=list.size();
      int[] columns=new int[size];
      int[] flags=new int[size];

      String item, colName, dir;
      for (int i=0; i<size; i++) {
        item=list.getItem(i).toUpperCase();
        colName=StringUtility.substringBefore(item, StringUtility.BLANK);
        dir=StringUtility.substringAfterLast(item, StringUtility.BLANK);
        columns[i]=getSqlColumnIndex(colName);
        flags[i]=DESC.equals(dir)?pSORT_DESC:pSORT_ASC;
      }

      Comparator c = new RowListComparator(columns, flags);
      Collections.sort(pRows, c);
    }  // orderBy

    private int getSqlColumnIndex(final String aCol) {
      if (!NumberUtils.isDigits(aCol)) return indexOfColumn(aCol);

      int index= Integer.parseInt(aCol);
      if (index > (getColumnCount() - 1)) {
          throw new IllegalArgumentException("Column index out of bounds.");
      }
      return index;
    }

    /**
     * Add the rows of the given aRowList to this RowList object. Make sure that
     * the added rows have the same number and names as this object's.
     *
     * @param aRowList The RowList to append to this object.
     */
    public void addAll(final RowList aRowList) {
        if (aRowList == null) return;
        for (int i = 0, n = aRowList.size(); i < n; i++) {
            add(aRowList.get(i));
        }
    }

  /**
   * Converts the given row to a <code>SingleRowList</code>.
   * @return Returns the <code>RowList</code> as a <code>SingleRowList</code>.
   */
    public SingleRowList getRowAsSingleRowList(final int aRowIndex) {
      Map row = get(aRowIndex);
      SingleRowList rowList = new SingleRowList(row);
      rowList.setColumnHeaders(getColumnHeaders());
      return rowList;
    }

/** Get the row counter column name, the default is [ROW_COUNTER] */
    public String getRowCounterName() {
      return pRowCounterName;
    }

/** Set the row counter column name */
    public void setRowCounterName(final String aRowCounterName) {
      pRowCounterName= aRowCounterName;
    }

/** Check if a row counter column is used, the default is false */
    public boolean getIsUseRowCounter() {
      return pIsUseRowCounter;
    }

/** Set the row counter column */
    public void setIsUseRowCounter(final boolean aIsUseRowCounter) {
      setIsUseRowCounter(aIsUseRowCounter, pRowCounterName, pRowCounterStartIndex);
    }

/** Set the row counter column */
    public void setIsUseRowCounter(final boolean aIsUseRowCounter,
                                   final int aStartIndex) {
      setIsUseRowCounter(aIsUseRowCounter, pRowCounterName, aStartIndex);
    }

/**
 * Set the row counter column
 * @param aIsUseRowCounter boolean turn it on or off.
 * @param aRowCounterName String Name of row counter column
 * @param aStartIndex int The start index of the counter, default is 1
 */
    public void setIsUseRowCounter(final boolean aIsUseRowCounter,
                                   final String aRowCounterName,
                                   final int aStartIndex) {
      pIsUseRowCounter= aIsUseRowCounter;
      pRowCounterName= aRowCounterName;
      pRowCounterStartIndex= aStartIndex;
      if (!aIsUseRowCounter) {
        pRowCounter= null;
        return;
      }

      int n= size();
      pRowCounter= new ListOfString(n);
      for (int i=0; i<n; i++) {
        appendToRowCounterList(i+aStartIndex);
      }
    }  // setIsUseRowCounter

/** Append a new value to the row counter list, the value will be last value + 1 */
    private void appendToRowCounterList() {
      if (!pIsUseRowCounter) return;

      int v;
      if (pRowCounter.size()==0) v=pRowCounterStartIndex;
      else {
        String s= pRowCounter.getLastItem();
        v= Integer.parseInt(s) + 1;
      }
      appendToRowCounterList(v);
    }

    private void appendToRowCounterList(final int aValue) {
      if (!pIsUseRowCounter) return;
      pRowCounter.add( String.valueOf(aValue) );
    }

/**
 * Get the value of the row counter at the given index as a string
 * @param aRowIndex int
 * @return String the row counter, if this object is not using a row counter, return
 * an empty string
 */
    public String getRowCounterAsString(final int aRowIndex) {
      if (!pIsUseRowCounter) return StringUtility.EMPTY;
      return pRowCounter.getItem(aRowIndex);
    }

/**
 * Get the value of the row counter at the given index as an int
 * @param aRowIndex int
 * @return int the row counter, if this object is not using a row counter, return -1
 */
    public int getRowCounter(final int aRowIndex) {
      if (!pIsUseRowCounter) return -1;
      return Integer.parseInt( pRowCounter.getItem(aRowIndex) );
    }

/**
 * Determines if the row has only null values in all of its columns.
 * @param aRowIndex The index within the <code>RowList</code>.
 * @return Returns <code>true</code> if the row is empty;
 *         <code>false</code> otherwise.
 */
  public boolean isRowNullValues(final int aRowIndex) {
    if (isEmpty()) return true;

    Object[] row=getRowData(aRowIndex);
    return ArrayUtilities.isNull(row);
  }  // isRowNullValues


/**
   * Set the interface whose properties represent the columns of a single row in this object.
   * The names of the properties on the given interface must match the names of the
   * columns in this RowList. For example, if the interface has setName()/getName, then
   * this RowList must have a column named "name".
 */
  public void setRowInterface(final Class aRowInterface) {
    pRowInterface= aRowInterface;
    pBeanFromRowBuilder= new BeanFromRowBuilder(aRowInterface);
  }

/** Get the interface whose properties represent the columns of a single row in this object */
  public Class getRowInterface() {
    return pRowInterface;
  }

/**
 * Get an object which implements the interface defined in setRowInterface() for the row
 * at the given index.
 */
  public Object getRowAsBean(final int aRowIndex) {
    if (pRowInterface==null) {
      throw new IllegalStateException("You must call setRowInterface() before you call this method");
    }

    pBeanFromRowBuilder.rowIndex= aRowIndex;
    return pBeanFromRowBuilder.getBean();
  }

/**
 * Get an object which implements aRowInterface for the row at aRowIndex.
 * For example, if you have a RowList with columns: name and address, create this
 * interface: <pre>
interface IPerson {
  void setName(String name);
  String getName();
  void setAddress(String address);
  String getAddress();
}
</pre>
To get a bean that represent the first row in the RowList: <pre>
IPerson person= (IPerson) rowList.getRowAsBean(0, IPerson.class);
</pre>

Now the person object can be used to set and get the values of the first row columns.
 *
 * @param aRowIndex int the index of the row to represent as a bean.
 * @param aRowInterface The interface wich will be implemented by the returned bean.
 * @return Object The bean which represent the given row.
 */
  public Object getRowAsBean(final int aRowIndex, final Class aRowInterface) {
    setRowInterface(aRowInterface);
    return getRowAsBean(aRowIndex);
  }

  private class BeanFromRowBuilder extends AbstractBeanBuilder
                                   implements AbstractBeanBuilder.IBeanValueAccessor {
    int rowIndex;

    BeanFromRowBuilder(final Class aRowInterface) {
      pBeanValueAccessor= this;
      super.createBean(aRowInterface);
    }

    public Object getPropertyValue(final Object aKey) {
      return getObject(rowIndex, (String) aKey);
    }

    public void setPropertyValue(final Object aKey, final Object aValue) {
      setObject(rowIndex, (String) aKey, aValue);
    }
  }

  /** for testing */
  public static void main(String[] args) {
    List cn= new ArrayList();
    cn.add("c3");
    cn.add("c2");
    cn.add("c1");
    RowList r= new RowList(cn);

    Map row= new HashMap();
    row.put("c2", "v2");
    row.put("c1", "v1");
    row.put("c3", "v3");

    r.add(row);

    r.setIsUseRowCounter(true, "#", 3);
    System.out.println(r.toString());

    row= r.get(0);

    for (Iterator it=row.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry entry= (Map.Entry) it.next();
      String name= (String) entry.getKey();
      String val= (String) entry.getValue();
      System.out.println(name + "=" + val);
    }

  }

}  // RowList
