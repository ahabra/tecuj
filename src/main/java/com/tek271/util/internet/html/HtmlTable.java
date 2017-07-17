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

package com.tek271.util.internet.html;

import org.apache.commons.lang3.StringUtils;

import com.tek271.util.string.*;
import com.tek271.util.collections.list.*;

/**
 * <p>Generates an HTML table. To use this class, <ol>
 * <li>Implement the interface HtmlTable.ICellCallback
 * <li>create a new object of HtmlTable
 * <li>initialize its properties as you like,
 * <li>then call the toString() method.
 * </ol>
 * </p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class HtmlTable {

/**
 * Client applications should implement this interface to provide the text to be
 * displayed at each cell in the table.
 */
  public interface ICellCallback {
    String getCell(int aRow, int aColumn);
  }

/** The cell callback which provides text to be displayed at each cell */
  public ICellCallback cellCallback;

/** List of columns headers text */
  public ListOfString columnHeaders;

/** Alignment for each column */
  public ListOfString columnAlignment;

  public ListOfString columnWidth;
  
/** Number of rows in the table */
  public int rowCount;

/** Number of columns in the table */
  public int colCount;

  public boolean isShowColumnHeaders= true;
  public boolean isShowRowCounter= true;

/** The row counter starting value */
  public int rowCounterStart;

/** Border size, default is 0 */
  public int border=0;

  public int cellSpacing= 0;
  public int cellPadding= 1;

/** Width of the table, -1 means no width specified */
  public int width=-1;

/** Is width in pixeles or percent of window */
  public boolean widthIsPercent;

/** Background color of the header, default is #F5F5DC. Will be ignored if
   * <code>classHeader</code> property is set. */
  public String colorHeaderBg= "#F5F5DC";

/** Background color1 for alternating row colors, default is empty. Will be ignored if
   * <code>classRow1</code> property is set. */
  public String colorRowBg1= pEMPTY;

/** Background color2 for alternating row colors, default is #EAEAFF. Will be ignored if
   * <code>classRow2</code> property is set. */
  public String colorRowBg2= "#EAEAFF";

/** CSS style for header row styles, default is empty */
  public String classHeader="";

/** CSS style for alternating row styles, default is empty */
  public String classRow1="";

/** CSS style for alternating row styles, default is empty */
  public String classRow2="";
  
  public String cssClass= "";

/** Should an empty <code>td</code> cell be replaced by a blank (<code>&amp;nbsp;</code>)  */  
  public boolean isReplaceEmptyCellWithSpace= true;
  
  private static final String pNL= StringUtility.NEW_LINE;
  private static final String pEMPTY= StringUtility.EMPTY;
//  private static final String pNBSP= "&nbsp;";
  private static final String pTOP= "top";
  private static final String pLEFT= "left";

  private StringBuffer pBuffer;


/**
 * Create a new HTML table generator.
 * @param aCellCallback ICellCallback The cell callback which provides text to be
 *        displayed at each cell
 * @param aRowCount int Number of rows in the table
 * @param aColCount int Number of columns in the table
 * @param aColumnHeaders ListOfString List of columns headers text, can be null
 * @param aColumnAlignment ListOfString Alignment for each column, can be null
 */
  public HtmlTable(final ICellCallback aCellCallback,
                   final int aRowCount,
                   final int aColCount,
                   final ListOfString aColumnHeaders,
                   final ListOfString aColumnAlignment) {
    cellCallback= aCellCallback;
    rowCount= aRowCount;
    colCount= aColCount;
    columnHeaders= aColumnHeaders;
    columnAlignment= aColumnAlignment;
  }

/**
 * Create a new HTML table generator.
 * @param aCellCallback ICellCallback The cell callback which provides text to be
 *        displayed at each cell
 * @param aRowCount int Number of rows in the table
 * @param aColCount int Number of columns in the table
 */
  public HtmlTable(final ICellCallback aCellCallback,
                   final int aRowCount,
                   final int aColCount) {
    this(aCellCallback, aRowCount, aColCount, null, null);
  }


  private void appendLn(final String aString) {
    pBuffer.append(aString).append(pNL);
  }

  private String getAlignValue(final int aColumnIndex) {
    if (columnAlignment==null || aColumnIndex>=columnAlignment.size()) return pEMPTY;
    String a= columnAlignment.getItem(aColumnIndex);
    if (StringUtility.isBlank(a)) return pEMPTY;
    return a;
  }
  
  private String getColumnWidth(final int columnIndex) {
    if (columnIndex<0) return pEMPTY;
    if (columnWidth==null || columnIndex>=columnWidth.size()) return pEMPTY;
    String w= columnWidth.getItem(columnIndex);
    if (StringUtils.isBlank(w)) return pEMPTY;
    w= StringUtils.trimToEmpty(w);
    return w;
  }

  private void buildHeaderRow() {
    if (!isShowColumnHeaders) return;
    if (columnHeaders==null) return;

    String clr= StringUtility.isBlank(classHeader)? colorHeaderBg : null;
    appendLn("<tr class=\"" + classHeader + "\">");
    if (isShowRowCounter) {
      TableUtils.td(pBuffer, "#", -1, null, pLEFT, clr);
    }
    for (int i=0, n= columnHeaders.size(); i<n; i++) {
      String d= columnHeaders.getItem(i);
      String ha= getAlignValue(i);
      String width= getColumnWidth(i);
      TableUtils.th(pBuffer, d, width, null, ha, clr);
    }
    appendLn("</tr>");
  } // buildHeaderRow

  private String getCssClass(final int rowIndex) {
    String cssClass= rowIndex%2 == 0? classRow1 : classRow2;
    cssClass= StringUtils.trimToEmpty(cssClass);
    if (StringUtils.isBlank(cssClass)) return StringUtils.EMPTY;
    
    return " class=\"" + cssClass + "\"";
  }
  
  private void buildRow(final int aRowIndex) {
    String color;
    String cssClass= getCssClass(aRowIndex);
    if (aRowIndex%2 == 0) {
      color= StringUtility.isBlank(cssClass)? colorRowBg1 : null;
    } else {
      color= StringUtility.isBlank(cssClass)? colorRowBg2 : null;
    }
    appendLn("<tr" + cssClass + ">");
    
    String hdrColor= StringUtility.isBlank(classHeader)? colorHeaderBg : null;

    if (isShowRowCounter) {
      String v= String.valueOf(aRowIndex + rowCounterStart);
      TableUtils.td(pBuffer, classHeader, v, -1, pTOP, null, hdrColor);
    }

    for (int i=0; i<colCount ; i++) {
      String v= cellCallback.getCell(aRowIndex, i);
      if (isReplaceEmptyCellWithSpace && StringUtility.isEmpty(v)) {
        v= HtmlUtil.SPACE;
      }
      String a= getAlignValue(i);
      String width= getColumnWidth(i);
      TableUtils.td(pBuffer, v, width, pTOP, a, color);
    }

    appendLn("</tr>");
  }  // buildRow


  private void buildTable() {
    appendLn(pEMPTY);

    String w= null;
    if (width>=0) {
      w= String.valueOf(width);
      if (widthIsPercent) w= w + "%";
    }

    TableUtils.tableStart(pBuffer, cssClass, border, w, cellPadding, cellSpacing, null);

    buildHeaderRow();
    for (int i=0; i<rowCount; i++) {
      buildRow(i);
    }
    appendLn("</table>");
  }  // buildTable

/** Generate the HTML string for this table */
  public String toString() {
    pBuffer= new StringBuffer(128);
    buildTable();
    return pBuffer.toString();
  }


}   // HtmlTable
