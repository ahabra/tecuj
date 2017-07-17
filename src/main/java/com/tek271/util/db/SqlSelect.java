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
 * Simplifies building Select SQL statement strings. When you have
 * long SQL select statements that take many lines, this class will be very helpful.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class SqlSelect {
  /** Put a line break between different clauses of the select statment. Default=true */
  public boolean isPutLineBreaks= true;

  private static final String pSELECT="SELECT ";
  private static final String pFROM="FROM ";
  private static final String pAS=" AS ";
  private static final String pWHERE="WHERE ";
  private static final String pORDER_BY="ORDER BY ";
  private static final String pGROUP_BY="GROUP BY ";

  private String pWhere = StringUtility.EMPTY;
  private String pOrderBy = StringUtility.EMPTY;
  private String pGroupBy = StringUtility.EMPTY;

  private ListOfString pColumns = new ListOfString();
  private ListOfString pColumnAlias = new ListOfString();
  private ListOfString pTables = new ListOfString();
  private ListOfString pTableAlias = new ListOfString();

/** for testing */
  public static void main(String[] args) {
    SqlSelect q= new SqlSelect();
    q.addColumn("Name");
    q.addColumn("age");
    q.addTable("person");
    q.addTable("address");
    q.setWhere("age<60");
    //q.setOrderBy("name");
    //q.setGroupBy("age");
    q.isPutLineBreaks= false;
    System.out.println(q.getSql());
  }

  public SqlSelect() {
    pColumns.isCaseSensitive= false;
    pTables.isCaseSensitive= false;
  }

  /**
   * @return The where clause of the statement.
   */
  public String getWhere() {
    return pWhere;
  }

  /**
   * Set the Where clause of this statement.
   * @param aWhere A valid sql where clause.
   */
  public void setWhere(final String aWhere) {
    pWhere= StringUtility.prefix(aWhere, pWHERE);
  }

  /**
   * @return The OrderBy clause of the statement.
   */
  public String getOrderBy() {
    return pOrderBy;
  }

  /**
   * Set the OrderBy clause of this statement.
   * @param aOrderBy A valid sql where clause.
   */
  public void setOrderBy(String aOrderBy) {
    pOrderBy= StringUtility.prefix(aOrderBy, pORDER_BY);
  }

  /**
   * @return The GroupBy clause of the statement.
   */
  public String getGroupBy() {
    return pGroupBy;
  }

  /**
   * Set the GroupBy clause of this statement.
   * @param aGroupBy A valid sql where clause.
   */
  public void setGroupBy(String aGroupBy) {
    pGroupBy= StringUtility.prefix(aGroupBy, pGROUP_BY);
  }

  /** Add aName to aNameList and aAlias to aAliasList. */
  private void addNameAlias(String aName, String aAlias,
                            ListOfString aNameList,
                            ListOfString aAliasList) {
    if (StringUtility.isBlank(aName)) return;
    aNameList.add(aName.trim());
    aAliasList.add(StringUtility.defaultString(aAlias).trim());
  }

  /**
   * Add a column to the select list of columns
   * @param aName Name of column as exists in db table
   * @param aAlias An alias for the column.
   */
  public void addColumn(String aName, String aAlias) {
    addNameAlias(aName, aAlias, pColumns, pColumnAlias);
  }

  /**
   * Add a column to the select list of columns
   * @param aName Name of column as exists in db table
   */
  public void addColumn(String aName) {
    addColumn(aName, StringUtility.EMPTY);
  }

  /**
   * Delete the given name from aNameList and its corresponding alias.
   * @param aName Name of column
   */
  public void deleteNameAlias(String aName,
                              ListOfString aNameList,
                              ListOfString aAliasList) {
    int i= aNameList.indexOf(aName);
    if (i<0) return;
    aNameList.remove(i);
    aAliasList.remove(i);
  }  // deleteNameAlias


  /**
   * Delete the given column from the select query.
   * @param aName Name of column
   */
  public void deleteColumn(String aName) {
    deleteNameAlias(aName, pColumns, pColumnAlias);
  }

  /**
   * Add aName to the list of tables
   * @param aName Name of table as in the db.
   * @param aAlias Alias for the table name.
   */
  public void addTable(String aName, String aAlias) {
    addNameAlias(aName, aAlias, pTables, pTableAlias);
  }

  /**
   * Add aName to the list of tables
   * @param aName Name of table as in the db.
   */
  public void addTable(String aName) {
    addNameAlias(aName, StringUtility.EMPTY , pTables, pTableAlias);
  }

  /**
   * Delete the given table from the select query.
   * @param aName Name of table
   */
  public void deleteTable(String aName) {
    deleteNameAlias(aName, pTables, pTableAlias);
  }

  /**
   * @return Separator between clauses of the query, either new line or blank.
   */
  private String getSeparator() {
    return isPutLineBreaks? StringUtility.NEW_LINE : StringUtility.BLANK;
  }

  /** Append aValue to aBuf but put a new line before it if aValue is not empty. */
  private void appendWithNewLine(StringBuffer aBuf,
                                 String aValue) {
    if (aValue.length() > 0) {
      aBuf.append(getSeparator());
      aBuf.append(aValue);
    }
  }

  /**
   * Append to a buffer pairs of items from aNames and aAlias. The name and
   * alias is separated by aSeparator, each pair is separated by comma.
   * @param aBuf buffer to append to.
   * @param aNames List of names
   * @param aAlias List of aliases
   * @param aSeparator Separator between name and alias.
   */
  private void appendListWithAlias(StringBuffer aBuf,
                                   ListOfString aNames,
                                   ListOfString aAlias,
                                   String aSeparator) {
    String als;
    for (int i=0, n=aNames.size(); i<n; i++) {
      aBuf.append( aNames.getItem(i) );
      als= aAlias.getItem(i);
      if (!StringUtility.isBlank(als)) aBuf.append(aSeparator).append(als);
      if (i<n-1) aBuf.append(',');
    }
  }  // appendListWithAlias

  /**
   * @return sql text of the query.
   */
  public String getSql() {
    StringBuffer r= new StringBuffer(64);
    r.append(pSELECT);
    appendListWithAlias(r, pColumns, pColumnAlias, pAS);
    r.append(getSeparator());
    r.append(pFROM);
    appendListWithAlias(r, pTables, pTableAlias, StringUtility.BLANK);

    appendWithNewLine(r, pWhere);
    appendWithNewLine(r, pGroupBy);
    appendWithNewLine(r, pOrderBy);
    return r.toString();
  }

  public String toString() {
    return getSql();
  }

}  // SqlSelect
