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
 * Build the <b><code>where</code></b> clause for
 * an SQL statement. Helps avoiding string concatenations and worrying about quoting
 * string values.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class SqlWhere {
  private static final String pAND = "AND";

  private ListOfString pTerms = new ListOfString();
  private boolean pIsReplaceHtmlTags= false;

  /** For testing  */
  public static void main(String[] args) {
    SqlWhere w= new SqlWhere();
//    w.add("name", "abdul", true, "=", "");
//    w.add("age", "20");
//    w.add("age", 30);

    w.addNullable("city", null, null);
    w.addNullable("state", null);
    w.addNullable("zip", "90210");

    System.out.println(w.getSql());
  }

  public SqlWhere() {}

  public SqlWhere(boolean aIsReplaceHtmlTags) {
    pIsReplaceHtmlTags= aIsReplaceHtmlTags;
  }

  public boolean getReplaceHtmlTags() {
    return pIsReplaceHtmlTags;
  }

  /**
   * Add a term to the where clause.
   * @param aTerm A syntactically valid SQL Where clause term.
   * <p>
   * Examples of invoking this method:<pre>
   *   obj.add("fname='abdul'");
   *   obj.add(" and age&lt;90");</pre>
   * </p>
   */
  public void add(String aTerm) {
    pTerms.add(aTerm);
  }

  /**
   * Add a term to the where clause, the term consists of a column name and value
   * with assiciated operators.
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a string. If the value is quoted
   *   then all single quotes are replaced by two single quotes.
   * @param aIsQuoted Should the value be quoted.
   * @param aComparator The operator between column name and value, usually =
   * @param aPreOperator The operator used before the term, usually AND. For the
   *   first term this should be an empty string.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.add("fname", "abdul", true, "=", "");
   *   obj.add("lname", "Habra", true, "=", "AND");
   *   obj.add("age", "90", false, "&lt;", "OR");
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *   fname='abdul' AND lname='Habra' OR age&lt;90
   * </pre></p>
   */
  public void add(String aColumnName, String aValue,
                  boolean aIsQuoted, String aComparator,
                  String aPreOperator) {
    String pre= StringUtility.isBlank(aPreOperator)? StringUtility.EMPTY :
                                        aPreOperator+StringUtility.BLANK;
    String q= aIsQuoted? StringUtility.SINGLE_QUOTE : StringUtility.EMPTY;
    String v= aIsQuoted? StringUtility.replaceQuote4Db(aValue) : aValue;
    if (pIsReplaceHtmlTags) v= StringUtility.replaceHtmlTags(v);
    String com= aComparator.trim();
    if (!StringUtility.equalsAnyChar(com, "=<>")) StringUtility.pad(com);
    add(pre + aColumnName + com + q + v + q);
  }  // add

  /**
   * Add a term to the where clause, the term consists of column name=value
   * prefixed with AND.
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a string
   * @param aIsQuoted Should the value be quoted.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.add("lname", "Habra", true);
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *   AND lname='Habra'
   * </pre></p>
   */
  public void add(final String aColumnName, final String aValue,
                  final boolean aIsQuoted) {
    add(aColumnName, aValue, aIsQuoted, StringUtility.EQUAL, pAND);
  }

  /**
   * Add a term to the where clause, the term consists of column name=value
   * prefixed with AND. The value is not quoted
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a string, this value should not
   *   require quotes around it.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.add("age", "90");
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *   AND age=90
   * </pre></p>
   */
  public void add(final String aColumnName, final String aValue) {
    add(aColumnName, aValue, false, StringUtility.EQUAL , pAND);
  }

  /**
   * Add a term to the where clause, the term consists of column name=value
   * prefixed with AND. The value is not quoted
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a long, this value should not
   *   require quotes around it.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.add("age", 90);
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *   AND age=90
   * </pre></p>
   */
  public void add(final String aColumnName, final long aValue) {
    add(aColumnName, Long.toString(aValue) , false, StringUtility.EQUAL , pAND);
  }

  /**
   * Add a term to the where clause, the term is a set membership test.
   * @param aColumnName The name of the column.
   * @param aSetMembers Members of a set to check against. If any member contains
   *   a single quote, the caller should have taken care of replacing it with
   *   two single quotes. Note that this method WILL handle html tags correctly
   *   according to the constructor of this object.
   * @param aPreOperator The operator used before the term, usually AND. For the
   *   first term this should be an empty string.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.addSetMember("age", "90,91,92", "");
   *   obj.addSetMember("fname", "'abdul','edward'", "OR");
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *   age IN (90,91,92) OR fname IN ('abdul','edward')
   * </pre></p>
   */
  public void addSetMember(String aColumnName,
                           String aSetMembers,
                           String aPreOperator) {
    String pre= StringUtility.isBlank(aPreOperator)? StringUtility.EMPTY :
                   StringUtility.pad(aPreOperator);
    if (pIsReplaceHtmlTags) aSetMembers= StringUtility.replaceHtmlTags(aSetMembers);
    add(pre + aColumnName + " IN (" + aSetMembers + ")");
  }

  /**
   * Add a term to the where clause, the term consists of a column name and value
   * with assiciated operators, the value can be null
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a string. If the value is quoted
   *   then all single quotes are replaced by two single quotes.
   * @param aPreOperator The operator used before the term, usually AND. For the
   *   first term this should be an empty string.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.addNullable("city", null, null);
   *   obj.addNullable("state", null);
   *   obj.addNullable("zip", "90210", "or");
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *  WHERE (city IS NULL OR city='')  AND (state IS NULL OR state='')  or zip='90210'
   * </pre></p>
   */
  public void addNullable(String aColumnName, String aValue, String aPreOperator) {
    if (StringUtility.isEmpty(aValue)) {
      String pre= StringUtility.isBlank(aPreOperator)?
                    StringUtility.EMPTY : StringUtility.pad(aPreOperator);
      add(pre + "(" + aColumnName + " IS NULL OR " + aColumnName + "='')");
    } else {
      add(aColumnName, aValue, true, StringUtility.EQUAL, aPreOperator);
    }
  }  // addNullable

  /**
   * Add a term to the where clause, the term consists of a column name and value
   * with assiciated operators, the value can be null, prefix with an AND.
   * @param aColumnName The name of the column.
   * @param aValue The value of the column as a string. If the value is quoted
   *   then all single quotes are replaced by two single quotes.
   *<p>
   * Examples of invoking this method:<pre>
   *   obj.addNullable("city", null);
   *   obj.addNullable("state", null);
   *   obj.addNullable("zip", "90210");
   * </pre>
   * Will produce the following term in a where clause:<pre>
   *  WHERE (city IS NULL OR city='')  AND (state IS NULL OR state='')  AND zip='90210'
   * </pre></p>
   */
  public void addNullable(String aColumnName, String aValue) {
    addNullable(aColumnName, aValue, pAND);
  }  // addNullable

/** Add the first term in the where clause */
  public void addFirst(final String aColumnName, final long aValue) {
    add(aColumnName, Long.toString(aValue) , false, StringUtility.EQUAL , StringUtility.EMPTY);
  }

/** Add the first term in the where clause */
  public void addFirst(final String aColumnName, final String aValue) {
    add(aColumnName, aValue , false, StringUtility.EQUAL , StringUtility.EMPTY);
  }

/** Add the first term in the where clause */
  public void addFirst(final String aColumnName, final String aValue,
                       final boolean aIsQuoted) {
    add(aColumnName, aValue, aIsQuoted, StringUtility.EQUAL, StringUtility.EMPTY);
  }




  /**
   * Get the sql text of the where clause.
   * @return The text of a where clause including the WHERE word.
   */
  public String getSql() {
    if (pTerms.size()==0) return StringUtility.EMPTY;

    pTerms.lineSeparator= StringUtility.BLANK;
    return " WHERE " + pTerms.getText();
  }

  public String toString() {
    return getSql();
  }

}  // SqlWhere
