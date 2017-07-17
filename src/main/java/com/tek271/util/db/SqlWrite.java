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

import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
Build an INSERT or an UPDATE SQL string. This is  specially helpful to avoid repeating
sql code for insert and update on the same table.
<p><b>Example:</b></p>
<div style="background-color: #E6E6E6">
<pre>SqlWrite q = <b>new</b> SqlWrite(SqlWrite.UPDATE, &quot;<font color="#008000">employee</font>&quot;);
q.setWhere(&quot;<font color="#008000">age&lt;18</font>&quot;);
q.addColumn(&quot;<font color="#008000">destination</font>&quot;, &quot;<font color="#008000">Hawaii</font>&quot;, <b>true</b>);  <font color="#666666"><i>// <b>true</b>: surround Hawaii by single quotes</i></font>
q.addColumn(&quot;<font color="#008000">vacationDays</font>&quot;, 10);
System.out.println(q.toString());</pre>
</div>
<p>The above code will generate the following SQL statement:</p>
<div style="background-color: #E6E6E6">
 <pre>    <b>UPDATE</b> employee <b>SET</b> destination='<font color="#008000">Hawaii</font>',vacationDays=10 <b>WHERE</b> age&lt;18</pre>
</div>
<p>Now, to generate an INSERT statement on the same table and columns, all we need is to call:</p>
<div style="background-color: #E6E6E6">
<pre>q.setQueryType(SqlWrite.INSERT);
System.out.println(q.toString());</pre>
</div>
<p>Which will generate: (notice that we did not have to repeat column definitions)</p>
<div style="background-color: #E6E6E6">
 <pre>    <b>INSERT INTO</b> employee (destination,vacationDays) <b>VALUES</b> ('<font color="#008000">Hawaii</font>',10) <b>WHERE</b> age&lt;18</pre>
</div>
<p>Although this was a simple example that may not justify using the
<code>SqlWrite</code> class, it is meant to demonstrate the basic
usage. This class really shines when you have complex inserts or updates that take
many lines and are hard to follow.
</p>
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class SqlWrite {
  /** Put a line break between different clauses of the select statment. Default=true */
  public boolean isPutLineBreaks= true;

  public static final String INSERT = "INSERT";
  public static final String UPDATE = "UPDATE";
  private static final String pWHERE = "WHERE ";

  private String pTableName= StringUtility.EMPTY;
  private String pWhere=StringUtility.EMPTY;
  private String pQueryType;
  private boolean pIsReplaceHtmlTags = false;
  private ListOfString pNames = new ListOfString();
  private ListOfString pValues = new ListOfString();
  private ListOfString pOriginalValues = new ListOfString();


  public static void main(String[] args) {
    SqlWrite q = new SqlWrite(SqlWrite.UPDATE, "employee");
    q.setQueryType(SqlWrite.INSERT);
    q.setWhere("age<18");
    q.addColumn("destination", "Hawaii", true);
    q.addColumn("vacationDays", "10");
    System.out.println(q.toString());
  }

  public SqlWrite() {
    pIsReplaceHtmlTags= false;
  }

  public SqlWrite(boolean aIsReplaceHtmlTags) {
    pIsReplaceHtmlTags= aIsReplaceHtmlTags;
  }

  public SqlWrite(final String aQueryType,
                  final String aTableName,
                  final boolean aIsReplaceHtmlTags) {
    pIsReplaceHtmlTags= aIsReplaceHtmlTags;
    setTableName(aTableName);
    setQueryType(aQueryType);
  }

  public SqlWrite(final String aQueryType,
                  final String aTableName) {
    setTableName(aTableName);
    setQueryType(aQueryType);
  }

  public boolean getReplaceHtmlTags() {
    return pIsReplaceHtmlTags;
  }

  public String getTableName() {
    return pTableName;
  }

  public void setTableName(String aTableName) {
    pTableName = aTableName;
  }

  public String getWhere() {
    return pWhere;
  }

  public void setWhere(String aWhere) {
    pWhere=StringUtility.EMPTY;
    if (StringUtility.isBlank(aWhere)) return;
    pWhere= StringUtility.prefix(aWhere, pWHERE);
  } // setWhere();

  public String getQueryType() {
    return pQueryType;
  }

  /**
  * Set query type to insert or update
  * @param aQueryType INSERT or UPDATE, case not sensetive.
  */
  public void setQueryType(String aQueryType){
    String qt = StringUtility.trim(aQueryType);

    if (qt.equalsIgnoreCase(INSERT) || qt.equalsIgnoreCase(UPDATE))
      pQueryType = aQueryType;
    else
      pQueryType = StringUtility.EMPTY;
  } // setQueryType();

  /**
   * Add a new column to be written
   * @param aName Name of column
   * @param aValue Value of column to be written
   * @param aIsQuoted Should the value be quoted.
   */
  public void addColumn(String aName, String aValue, boolean aIsQuoted) {
    pOriginalValues.add(aValue);
    String q= aIsQuoted? StringUtility.SINGLE_QUOTE : StringUtility.EMPTY;
    String v= aIsQuoted? StringUtility.replaceQuote4Db(aValue) : aValue;
    if (pIsReplaceHtmlTags) v= StringUtility.replaceHtmlTags(v);
    pNames.add(aName);
    pValues.add(q + v + q);
  } // addColumn();

  /** Add a column, with String value not quoted. */
  public void addColumn(String aName, String aValue) {
    addColumn(aName, aValue, false);
  } // addColumn();

  /** Add a column, with long value not quoted. */
  public void addColumn(String aName, long aValue) {
    addColumn(aName, Long.toString(aValue), false);
  }

  /** Add a column, with float value not quoted. */
  public void addColumn(String aName, float aValue) {
    addColumn(aName, Float.toString(aValue), false);
  }

  /**
   * @return Separator between clauses of the query, either new line or blank.
   */
  private String getSeparator() {
    return isPutLineBreaks? StringUtility.NEW_LINE : StringUtility.BLANK;
  }

  private String makeInsert() {
    pNames.lineSeparator= StringUtility.COMMA;
    pValues.lineSeparator= StringUtility.COMMA;
    String sep= getSeparator();
    StringBuffer q = new StringBuffer();
    q.append("INSERT INTO ").append(pTableName).append(sep);
    q.append(StringUtility.LPARAN);
    q.append(pNames.getText());
    q.append(StringUtility.RPARAN).append(sep);
    q.append("VALUES (");
    q.append(pValues.getText());
    q.append(StringUtility.RPARAN).append(sep).append(pWhere);
    return q.toString();
  } // makeInsert()

  private String makeUpdate() {
    StringBuffer b = new StringBuffer();
    String sep= getSeparator();
    b.append("UPDATE ").append(pTableName).append(sep).append("SET ");
    ListOfString merge= ListOfString.merge(pNames, pValues, StringUtility.EQUAL);
    merge.lineSeparator= StringUtility.COMMA;
    b.append( merge.getText() );
    b.append(sep).append(pWhere);
    return b.toString();
  } // makeUpdate()

  /**
   * Get the sql statement text for the insert or update
   * @return The text of the query. If the query is not valid, returns an
   * error message.
   */
  public String getSql() {
    if (pQueryType.equalsIgnoreCase(INSERT))
      return makeInsert();
    else
    if (pQueryType.equalsIgnoreCase(UPDATE))
      return makeUpdate();
    else
      return "ERROR: Invalid Query Type. Class=" + getClass().getName();
  } // getSql()

  public String toString() {
    return getSql();
  }

/** Get the value of the given column, null if column not found */
  public String getColumnValue(final String aColumnName) {
    int i= pNames.indexOf(aColumnName);
    if (i<0) return null;
    return pOriginalValues.getItem(i);
  }

  public ListOfString getListOfColumnsValues(final ListOfString aColumns) {
    if (aColumns==null) return null;
    int n= aColumns.size();
    if (n==0) return null;

    ListOfString r= new ListOfString(n);
    for (int i=0; i<n; i++) {
      String cn= aColumns.getItem(i);
      String v= StringUtility.defaultString( getColumnValue(cn) );
      r.add(cn, v);
    }
    return r;
  }

}  // SqlWrite
