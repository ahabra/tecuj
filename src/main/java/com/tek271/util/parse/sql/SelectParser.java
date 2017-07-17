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

package com.tek271.util.parse.sql;


import com.tek271.util.parse.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
Define a parser that parses a <b><code>Select</code></b> Sql statement.
To use it:
<ol>
 <li>Construct a SelectParser object using a select statement, this will also parse the Sql string.</li>
 <li>getColumns: a list of selected columns</li>
 <li>getTables: a list of tables in the <code>From</code> clause.</li>
 <li>getWhere: a list of items in the <code>where</code> clause.</li>
 <li>getGroupBy: an optional list of items in the <code>Group by</code> clause.</li>
 <li>getOrderBy: an optional list of items in the <code>Order by </code>clause.</li>
</ol>
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class SelectParser {
  private static final String[] pKEYWORDS =
      {"select", "from", "where", "order", "group",  "by", "as", "asc", "desc" };

  private static final String[] pSTRING_MARKERS = {  "'", "\"" };

  private static final String[] pSEPARATORS =
      { ",", "(", ")", "=", "<", "<=", ">", ">="};

  private static final String[] pWHITE_SPACE =  { " ", "\u0009" };
  private static final String[] pSINGLE_LINE_COMMENT = { "--" };
  private static final String[] pMULTI_LINE_COMMENT_STARTER = { "/*" };
  private static final String[] pMULTI_LINE_COMMENT_ENDER   = { "*/" };
  private static final String[] pNEW_LINEMARKER = { "\n", "\r\n", "\n\r", "\r" };

  private static final String[] pMAIN_CLAUSES = {"select", "from", "where",
                                                 "order", "group"};

  private ListOfString pColumns= new ListOfString();
  private ListOfString pTables= new ListOfString();
  private ListOfString pWhere= new ListOfString();
  private ListOfString pOrderBy= new ListOfString();
  private ListOfString pGroupBy= new ListOfString();

  private Tokenizer pTokenizer;
  private String pCurrentClause= StringUtility.EMPTY;  // select, from, where, order by, group by

  public SelectParser(final String aSql) {
    initTokenizer();
    parse(aSql);
  }

  private void initTokenizer() {
    pTokenizer = new Tokenizer();
    pTokenizer.setCaseSensitive(false);

    pTokenizer.setKeywords( new ListOfString(pKEYWORDS) );
    pTokenizer.setMultiLineCommentEnders( new ListOfString(pMULTI_LINE_COMMENT_ENDER) );
    pTokenizer.setMultiLineCommentStarters( new ListOfString(pMULTI_LINE_COMMENT_STARTER) );
    pTokenizer.setNewLineMarkers( new ListOfString(pNEW_LINEMARKER) );
    pTokenizer.setSeparators( new ListOfString(pSEPARATORS) );
    pTokenizer.setSingleLineCommentMarkers( new ListOfString(pSINGLE_LINE_COMMENT) );
    pTokenizer.setStringMarkers( new ListOfString(pSTRING_MARKERS) );
    pTokenizer.setWhiteSpaces( new ListOfString(pWHITE_SPACE) );

    pTokenizer.isReturnComments= false;
    pTokenizer.isReturnNewLineMarkers= false;
    pTokenizer.isReturnWhiteSpace= false;
  }  // initTokenizer();

  private void parse(final String aSql) {
    pTokenizer.setText(aSql);
    Token tk;
    String v;
    int tp;
    String lastVal= StringUtility.EMPTY;
    int lastType=-1;
    ListOfString curList;
    while (true) {
      tk= pTokenizer.getNext();
      if (tk==null) break;
      tp= tk.type;
      v= tk.getValue();
      curList= getCurrentClauseList();

      if (tp== Token.TYPE_KEYWORD) {
        setCurrentClause(v);
        if (v.equals("by")) continue;
        if (v.equals("as")) { lastVal= v; continue; }
        if (v.equals("asc") || v.equals("desc")) curList.setLastItem(curList.getLastItem() + " " + v, false );
        lastType= tp;
        continue;
      }
      if (tp== Token.TYPE_SEPARATOR) {
        lastVal= v;
        if ( pCurrentClause.equals("where") )    curList.add(v);
        if ( pCurrentClause.equals("select")) {
          if ("()".indexOf(v)>=0 ) curList.add(v);
        }
        lastType= tp;
        continue;
      }
      if (tp== Token.TYPE_LITERAL) {
        if (lastVal.equals("as") ) {
          pColumns.setLastItem(pColumns.getLastItem() + " as " + v, false);
          lastVal= StringUtility.EMPTY;
          continue;
        }

        if (lastType== Token.TYPE_LITERAL && pCurrentClause.equals("from") ) {
          pTables.setLastItem(pTables.getLastItem() + " " + v, false);
          lastType=-1;
          continue;
        }
        curList.add(v);
      }
      if (tp== Token.TYPE_STRING) {
        curList.add(v);
      }
      lastType= tp;
    } // while
  }  // parse();

///** Append aValue to the last item in the list */
//  private static void appendToLastItem(final ListOfString aList, final String aValue) {
//    aList.setLastItem(aList.getLastItem() + aValue);
//  }

  private void setCurrentClause(final String aKeyword) {
    if (StringUtility.isBlank(aKeyword)) return;
    if (StringUtility.in(aKeyword, pMAIN_CLAUSES)) pCurrentClause= aKeyword;
  }  // setCurrentClause();

/** get the list of the current clause */
  private ListOfString getCurrentClauseList() {
    if (pCurrentClause.equals("from"))   return pTables;
    if (pCurrentClause.equals("where"))  return pWhere;
    if (pCurrentClause.equals("order"))  return pOrderBy;
    if (pCurrentClause.equals("group"))  return pGroupBy;
    return pColumns;
  } // getCurrentClauseList

  public ListOfString getColumns() {
    return pColumns;
  }

  public ListOfString getTables() {
    return pTables;
  }

  public ListOfString getWhere() {
    return pWhere;
  }

  public ListOfString getOrderBy() {
    return pOrderBy;
  }

  public ListOfString getGroupBy() {
    return pGroupBy;
  }

  private String getColumnsAsString() {
    StringBuffer b= new StringBuffer(32);
    for (int i=0,n=pColumns.size(); i<n-1; i++) {
      String item= pColumns.getItem(i);
      String next= pColumns.getItem(i+1);

      if (item.equals("(")) b.append(item);
      else if ("()".indexOf(next)>=0) b.append(item);
      else b.append(item).append(", ");
    }
    b.append(pColumns.getLastItem());
    return b.toString();
  }

  public String toString() {
    ListOfString r= new ListOfString();
    r.add("SELECT", getColumnsAsString());
    pTables.lineSeparator= ", ";
    r.add("FROM", pTables.toString());
    pWhere.lineSeparator= " ";
    r.add("WHERE", pWhere.toString());
    pOrderBy.lineSeparator= ", ";
    r.add("ORDER BY", pOrderBy.toString());
    pGroupBy.lineSeparator= ", ";
    r.add("GROUP BY", pGroupBy.toString());
    return r.toString();
  } // toString()


  public String toSql() {
    ListOfString r= new ListOfString();
    r.valueEquator= " ";
    if (pColumns.size() >0) {
      r.add("SELECT", getColumnsAsString());
    }
    if (pTables.size() >0) {
      pTables.lineSeparator=", ";
      r.add("FROM", pTables.toString());
    }
    if (pWhere.size() >0) {
      pWhere.lineSeparator=" ";
      r.add("WHERE", pWhere.toString());
    }

    if (pOrderBy.size() >0) {
      pOrderBy.lineSeparator=", ";
      r.add("ORDER BY", pOrderBy.toString());
    }

    if (pGroupBy.size() >0) {
      pGroupBy.lineSeparator=", ";
      r.add("GROUP BY", pGroupBy.toString());
    }
    return r.toString();
  } // toSql()

/** for test */
  public static void main(String[] args) {
//    String sql= "select count(*) as k1,c2 from t1,t2 x, t3 where x<10  and b !has ('2', 3) order by c1 asc ,c2 desc, c3 asc  group by c1 asc";
    String sql= "select count(*), max(x) from x";
    SelectParser sp= new SelectParser(sql);
    System.out.println(sql + "\n------");
    System.out.println(sp.toSql());
  }


}  // SelectParser
