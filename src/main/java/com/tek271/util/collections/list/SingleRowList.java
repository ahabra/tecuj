/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Tom Comer
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

import java.util.*;

/**
 * @author Tom Comer  <p>
 * A <code>SingleRowList</code> represents a <code>RowList</code> with just a
 * single row of data. <p>
 * @version 1.0
 * @since 1.0
 */
public class SingleRowList extends RowList {

    /**
     * Constructs a <code>SingleRowList</code>, setting the number of columns
     * for the row. This constructor will set the column names to the String
     * representation of the column number.
     * <p>
     *
     * @param columnCount
     *            The number of columns to initiate the
     *            <code>SingleRowList</code> with.
     * @since 1.0
     */
    public SingleRowList(int columnCount) {
        super(columnCount);
    }

    /**
     * Constructs a <code>SingleRowList</code> and populates the column names
     * of the <code>SingleRowList</code> with the contents of the List.
     * <p>
     * Column names will be stored in the <code>SingleRowList</code> sorted
     * ascending.
     * <p>
     *
     * @param names
     *            A List of column names.
     * @since 1.0
     */
    public SingleRowList(List names) {
        super(names);
    }

    /**
     * Constructs a <code>SingleRowList</code> and populates the first
     * element, represented by a Map, within the <code>SingleRowList</code>.
     * The Map should contain an entry for each column name / column value to be
     * used to initialize the first row within the <code>SingleRowList</code>.
     * <p>
     * Column names will be stored in the <code>SingleRowList</code> sorted
     * ascending.
     * <p>
     *
     * @param row
     *            A Map containing the column name / column value pairs to
     *            populate the first row of the <code>SingleRowList</code>.
     * @throws IllegalArgumentException
     *             Thrown if the number of columns in the Map do not match the
     *             number of columns in the <code>SingleRowList</code> or if
     *             the column names in the Map do not exist in the
     *             <code>SingleRowList</code>.
     * @since 1.0
     */
    public SingleRowList(Map row) {
        super(row);
    }

    /**
     * Adds a row to the SingleRowList. The array should contain a value for
     * each column name within the SingleRowList. The positioning of each value
     * with the array must correspond one-to-one with the positioning of their
     * respective column name.
     * <p>
     *
     * @param values
     *            An array of objects. Each element within the array corresponds
     *            to one column value.
     * @throws IllegalArgumentException
     *             Thrown if the number of columns in the array do not match the
     *             number of columns in the <code>SingleRowList</code> or if
     *             there is already a row of data in the
     *             <code>SingleRowList</code>.
     * @since 1.0
     */
    public void add(List values) {
        assertDataSize();
        super.add(values);
    }

    /**
     * Adds a row, represented by a Map, into the <code>SingleRowList</code>.
     * The Map should contain an entry for each column name / column value which
     * makes up the row being added to the <code>SingleRowList</code>.
     * <p>
     *
     * @param row
     *            A Map containing the column name / column value pairs to add
     *            to the <code>SingleRowList</code>.
     * @throws IllegalArgumentException
     *             Thrown if the number of columns in the Map do not match the
     *             number of columns in the <code>SingleRowList</code> or if
     *             the column names in the Map do not exist in the
     *             <code>SingleRowList</code> or if there is already a row of
     *             data in the <code>SingleRowList</code>.
     * @since 1.0
     */
    public void add(Map row) {
        assertDataSize();
        super.add(row);
    }

    /**
     * Adds a row to the SingleRowList. The array should contain a value for
     * each column name within the SingleRowList. The positioning of each value
     * with the array must correspond one-to-one with the positioning of their
     * respective column name.
     * <p>
     *
     * @param rowData
     *            An array of objects. Each element within the array corresponds
     *            to one column value.
     * @throws IllegalArgumentException
     *             Thrown if the number of columns in the array do not match the
     *             number of columns in the <code>SingleRowList</code> or if
     *             there is already a row of data in the
     *             <code>SingleRowList</code>.
     * @since 1.0
     */
    public void add(Object[] rowData) {
        assertDataSize();
        super.add(rowData);
    }

    private void assertDataSize() {
        if (this.size() > 0) {
            throw new IllegalArgumentException("This is a single row list, and there is already a row "
                    + "in this single row list.");
        }
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>aColumnIndex</code> of this <code>SingleRowList</code> object
     * as a <code>boolean</code> in the Java programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown <code>columnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public boolean getBoolean(final int aColumnIndex) {
        return getBoolean(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as a <code>boolean</code> in
     * the Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public boolean getBoolean(String aColumnName) {
        return getBoolean(0, aColumnName);
    }

    /**
     * Gets the value of the designated column of this
     * <code>SingleRowList</code> object as a <code>java.util.Date</code> in
     * the Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
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
    public Date getDate(String aColumnName) {
        return getDate(0, aColumnName);
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>aColumnIndex</code> of this <code>SingleRowList</code> object
     * as a <code>java.util.Date</code> in the Java programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>columnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @throws ClassCastException
     *             Thrown if the column cannot be casted to a
     *             <code>java.util.Date</code>.
     * @return Returns the column value; if the value of the column in the
     *         <code>SingleRowList</code> is <code>null</code>, the value
     *         returned is <code>null</code>
     * @since 1.0
     */
    public Date getDate(final int aColumnIndex) {
        return getDate(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as a <code>double</code> in the
     * Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>SingleRowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public double getDouble(String aColumnName) {
        return getDouble(0, aColumnName);
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>aColumnIndex</code> as a <code>double</code> in the Java
     * programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>aColumnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public double getDouble(final int aColumnIndex) {
        return getDouble(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column of this
     * <code>SingleRowList</code> object as a <code>float</code> in the Java
     * programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public float getFloat(String aColumnName) {
        return getFloat(0, aColumnName);
    }

    /**
     * Gets the value of the designated column,identified by the
     * <code>aColumnIndex</code> as a <code>float</code> in the Java
     * programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if <code>aColumnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public float getFloat(final int aColumnIndex) {
        return getFloat(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as an <code>int</code> in the
     * Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public int getInt(String aColumnName) {
        return getInt(0, aColumnName);
    }

    /**
     * Gets the value of the designated column, identified by the
     * <code>aColumnIndex</code>, as an <code>int</code> in the Java
     * programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>rowIndex</code> <code>aColumnIndex</code>
     *             is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public int getInt(final int aColumnIndex) {
        return getInt(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as a <code>long</code> in the
     * Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>SingleRowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public long getLong(String aColumnName) {
        return getLong(0, aColumnName);
    }

    /**
     * Gets the value of the designated column as a <code>long</code> in the
     * Java programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>aColumnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public long getLong(final int aColumnIndex) {
        return getLong(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as a <code>String</code> in the
     * Java programming language.
     * <p>
     * The <code>aColumnName</code> value is converted to uppercase, making
     * this operation case insensitive.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>SingleRowList</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public String getString(String aColumnName) {
        return getString(0, aColumnName);
    }

    /**
     * Gets the value of the designated column as a <code>String</code> in the
     * Java programming language.
     * <p>
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @throws ArrayIndexOutOfBoundsException
     *             Thrown if the <code>aColumnIndex</code> is out of range.
     *             <code>(getColumnCount() < 0 || columnIndex >= getColumnCount())</code>.
     * @return Returns the column value.
     * @since 1.0
     */
    public String getString(final int aColumnIndex) {
        return getString(0, aColumnIndex);
    }

    /**
     * Gets the value of the designated column as an <code>Object</code> in
     * the Java programming language.
     * <p>
     *
     * @param aColumnName
     *            The name of the column to get.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>RowList</code>.
     * @return Returns the column value; if the value of the column in the
     *         <code>SingleRowList</code> is <code>null</code>, the value
     *         returned is <code>null</code>
     * @since 1.0
     */
    public Object getObject(String aColumnName) {
        return getObject(0, aColumnName);
    }

    /**
     * Get the value for the given column index as an <code>Object</code> in
     * the Java programming language.
     *
     * @param aColumnIndex
     *            The index of the column to get.
     * @return The found value as an Object.
     */
    public Object getObject(int aColumnIndex) {
        return getObject(0, aColumnIndex);
    }

    /**
     * Replaces the value of the specified column at the specified position in
     * this <code>SingleRowList</code> with the specified element.
     * <p>
     *
     * @param aColumnName
     *            The name of the column.
     * @param aColumnValue
     *            The value to be stored at the specified position.
     * @throws IllegalArgumentException
     *             Thrown if the column name does not exist in the
     *             <code>SingleRowList</code>.
     * @since 1.0
     */
    public void setObject(String aColumnName, Object aColumnValue) {
        setObject(0, aColumnName, aColumnValue);
    }
}
