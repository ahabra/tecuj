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

package com.tek271.util.internet.html;

import com.tek271.util.string.StringUtility;
import com.tek271.util.collections.list.*;

/**
* Utility methods for HTML tables, including the creation of &lt;td> and &lt;th> elements.
* Copyright:    Copyright (c) 2001 Technology Exponent
* @author Abdul Habra
* @version 1.0
*/
public class TableUtils {

private static final String pDQ= StringUtility.DOUBLE_QUOTE;
private static final String pTD= "td";
private static final String pTH= "th";
private static final String pCSS_CLASS= "class";
private static final String pWIDTH= "width";
private static final String pVALIGN= "valign";
private static final String pHALIGN= "align";
private static final String pBGCOLOR= "bgcolor";
private static final String pTAB_LT= "  <";
private static final String pLT_DASH= "</";
// private static final char pLT= '<';
private static final char pGT= '>';
private static final String pNBSP= "&nbsp;";


private TableUtils(){}

private static void tdh(final StringBuffer aBuf,
                        final String aValue,
                        final String aCssClass,
                        final String aWidth,
                        final String aVAlign,
                        final String aHAlign,
                        final String aBgColor,
                        final String aTag) {
  aBuf.append(pTAB_LT).append(aTag);
  appendAttribute(aBuf, pCSS_CLASS, aCssClass);
  appendAttribute(aBuf, pWIDTH, aWidth);
  appendAttribute(aBuf, pVALIGN, aVAlign);
  appendAttribute(aBuf, pHALIGN, aHAlign);
  
  appendAttribute(aBuf, pBGCOLOR, aBgColor);
  String v= StringUtility.defaultString(aValue, pNBSP);
  aBuf.append(pGT).append(v);
  aBuf.append(pLT_DASH).append(aTag).append(pGT);
  aBuf.append( StringUtility.NEW_LINE );
} // tdh

private static void tdh(final StringBuffer aBuf,
                        final String aValue,
                        final String aCssClass,
                        final int aWidth,
                        final String aVAlign,
                        final String aHAlign,
                        final String aBgColor,
                        final String aTag) {
  tdh(aBuf, aValue, aCssClass, 
      String.valueOf(aWidth) + "px", 
      aVAlign, aHAlign, aBgColor, aTag);
} // td

public static void td(StringBuffer aBuf, String aCssClass,
                      String aValue, int aWidth,
                      String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, aCssClass, aWidth, aVAlign, aHAlign, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf, String aCssClass,
    String aValue, String aWidth,
    String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, aCssClass, aWidth, aVAlign, aHAlign, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf,
                      String aValue, int aWidth,
                      String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, aHAlign, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf,
    String aValue, String aWidth,
    String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, aHAlign, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf,
                      String aValue, int aWidth,
                      String aVAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, null, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf,
    String aValue, String aWidth,
    String aVAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, null, aBgColor, pTD);
} // td

public static void td(StringBuffer aBuf, String aValue, int aWidth) {
  td(aBuf, aValue, aWidth, null, null);
} // td()

public static void td(StringBuffer aBuf, String aValue, String aWidth) {
  td(aBuf, aValue, aWidth, null, null);
} // td()

public static void td(StringBuffer aBuf, String aValue) {
  td(aBuf, aValue, -1, null, null);
} // td()

public static void th(StringBuffer aBuf, String aCssClass,
                      String aValue, int aWidth,
                      String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, aCssClass, aWidth, aVAlign, aHAlign, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf, String aCssClass,
    String aValue, String aWidth,
    String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, aCssClass, aWidth, aVAlign, aHAlign, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf,
                      String aValue, int aWidth,
                      String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, aHAlign, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf,
    String aValue, String aWidth,
    String aVAlign, String aHAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, aHAlign, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf,
                      String aValue, int aWidth,
                      String aVAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, null, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf,
    String aValue, String aWidth,
    String aVAlign, String aBgColor) {
  tdh(aBuf, aValue, null, aWidth, aVAlign, null, aBgColor, pTH);
} // th

public static void th(StringBuffer aBuf, String aValue, int aWidth) {
  th(aBuf, aValue, aWidth, null, null);
} // th()

public static void th(StringBuffer aBuf, String aValue, String aWidth) {
  th(aBuf, aValue, aWidth, null, null);
} // th()

public static void th(StringBuffer aBuf, String aValue) {
  th(aBuf, aValue, -1, null, null);
} // th()


public static StringBuffer appendAttribute(final StringBuffer aBuf,
                                           final String aName,
                                           final String aValue) {
  if (StringUtility.isBlank(aValue)) return aBuf;

  aBuf.append(StringUtility.BLANK);
  aBuf.append(aName);
  aBuf.append(StringUtility.EQUAL);
  aBuf.append(pDQ);
  aBuf.append(aValue);
  aBuf.append(pDQ);
  aBuf.append(StringUtility.BLANK);
  return aBuf;
}

public static StringBuffer appendAttribute(final StringBuffer aBuf,
                                           final String aName,
                                           final int aValue) {

  if (aValue<0) return aBuf;
  return appendAttribute(aBuf, aName, String.valueOf(aValue));
}

// <table border="2" width="50%" cellpadding="3" cellspacing="4">

public static StringBuffer tableStart(final StringBuffer aBuf,
                                      final int aBorder,
                                      final String aWidth,
                                      final int aCellPadding,
                                      final int aCellSpacing) {
  return tableStart(aBuf, null, aBorder, aWidth, aCellPadding, aCellSpacing, null);
}  // tableStart()

public static StringBuffer tableStart(final StringBuffer aBuf,
                                      final int aBorder,
                                      final String aWidth,
                                      final int aCellPadding,
                                      final int aCellSpacing,
                                      final String aAlign) {
  return tableStart(aBuf, null, aBorder, aWidth, aCellPadding, aCellSpacing, aAlign);
}  // tableStart()

public static StringBuffer tableStart(final StringBuffer aBuf,
                                      final String cssClass,
                                      final int aBorder,
                                      final String aWidth,
                                      final int aCellPadding,
                                      final int aCellSpacing,
                                      final String aAlign) {
  aBuf.append("<table");
  appendAttribute(aBuf, "class", cssClass);
  appendAttribute(aBuf, "border", aBorder);
  appendAttribute(aBuf, "width", aWidth);
  appendAttribute(aBuf, "cellpadding", aCellPadding);
  appendAttribute(aBuf, "cellspacing", aCellSpacing);
  appendAttribute(aBuf, "align", aAlign);
  aBuf.append(">").append(HtmlUtil.NEW_LINE);
  return aBuf;
}  // tableStart()

/** Convert a RowList to html table */
public static String rowListToHtmlTable(final RowList aRowList) {
  class CellData implements HtmlTable.ICellCallback {
    public String getCell(int aRow, int aColumn) {
      String v= StringUtility.trim(aRowList.getString(aRow, aColumn, true));
      return StringUtility.defaultIfEmpty(v, "&nbsp;");
    }
  }

  CellData cellData= new CellData();
  int columnCount= aRowList.getColumnCount();
  ListOfString headers= new ListOfString(columnCount);
  headers.addAll( aRowList.getColumnHeadersList() );
  HtmlTable htmlTable= new HtmlTable(cellData, aRowList.size(), columnCount, headers, null);
  htmlTable.border=1;
  htmlTable.rowCounterStart=1;

  return htmlTable.toString();
}


} // TableUtils
