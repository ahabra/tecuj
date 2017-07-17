/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2009  Abdul Habra
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

import com.tek271.util.Printf;
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.internet.html.HtmlTable.ICellCallback;

/**
 * Create a scrolling list of checkbox items. To use this class:<ol>
 * <li>Implement the interface <code>ICheckboxListCallback</code></li>
 * <li>Create a new object <code>CheckboxList</code> passing an <code>ICheckboxListCallback</code>
 *     and number of items in the list to the constructor.</li>
 * <li>optionally set the properties: <code>cssClassForRows</code>, <code>height</code>, 
 *     and <code>width</code>.</li>
 * <li>call the toString() method to get the html of a scrolling list box</li>
 * </ol>
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2009.
 */
public class CheckboxList {
  
/**
 * Objects of this class are returned by <code>ICheckboxListCallback.getCheckboxInfo(int)</code>
 * method. It describes a checkbox item in the list.
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2009.
 */  
  public static class CheckboxInfo {
    /** The value returned by the checkbox when selected */
    public String value;
    /** Should it be checked */
    public boolean isChecked;
    /** Is it disabled */
    public boolean isDisabled;
    /** The onClick even handler */
    public String onClick;
    /** ID attribute of the checkbox  */
    public String id;
    /** Name attribute of the checkbox  */
    public String name;
  }
  
/** User of the class CheckboxList must provide an implementation of this interface */
  public interface ICheckboxListCallback {
    
    /**
     * Return a CheckboxInfo object which describes the checkbox at the given row
     * @param row index
     * @return a CheckboxInfo object
     */
    CheckboxInfo getCheckboxInfo(int row);
    
    /**
     * Get the title at the given row/column index. 
     * @param row of the the title
     * @param column of the title starting at index=0 for the first title column
     * @return the title at the given index
     */
    String getTitle(int row, int column);
  }
  
  private static class HtmlTableCellCallback implements ICellCallback {
    private final ICheckboxListCallback checkBoxCallback;
    
    public HtmlTableCellCallback(ICheckboxListCallback checkBoxCallback) {
      this.checkBoxCallback= checkBoxCallback;
    }
    
    public String getCell(int row, int column) {
      if (column==0) {
        CheckboxInfo checkboxInfo= checkBoxCallback.getCheckboxInfo(row);
        return createCheckBox(checkboxInfo);
      }
      return checkBoxCallback.getTitle(row, column-1);
    }
    
  }
  
  private final HtmlTableCellCallback htmlTableCellCallback;
  private final int rowCount;
  private final int columnCount;
  
  /** The CSS class to be used with each row */
  public String cssClassForRows;
  
  public String cssClassOuterTable;
  public String cssClassInnerTable;
  
  /** The height if the list, e.g. 200px */
  public String height;
  
  /** The width of the list, e.g. 300px */
  public String width;

/**
 * Create an instance of this class
 * @param callback caller must provide an implementation of <code>ICheckboxListCallback</code>.
 * @param rowCount Number of rows in the list
 * @param columnCount Number of columns in the list.
 * @throws IllegalArgumentException if columnCount is less than one.
 */  
  public CheckboxList(ICheckboxListCallback callback, int rowCount, int columnCount) {
    this.htmlTableCellCallback= new HtmlTableCellCallback(callback);
    this.rowCount= rowCount;
    this.columnCount= columnCount;
    if (columnCount<1) {
      throw new IllegalArgumentException("columnCount must be 1 or more.");
    }
  }
  
  private static String createCheckBox(CheckboxInfo checkboxInfo) {
    StringBuffer sb= new StringBuffer();
    HtmlUtil.checkBox(sb, checkboxInfo.isChecked, checkboxInfo.isDisabled, 
          checkboxInfo.id, checkboxInfo.name, 
          checkboxInfo.onClick, checkboxInfo.value, null);
    return sb.toString();
  }

  private static final ListOfString COLUMN_ALIGNMENTS= new ListOfString(
        new String[] {
            null, "left",
        }
      );
  private static final ListOfString COLUMN_WIDTH= new ListOfString(
      new String[] {
          "1px", null,
      }
    );
  
  private HtmlTable createHtmlTable() {
    HtmlTable htmlTable= new HtmlTable(htmlTableCellCallback, rowCount, columnCount+1, null, COLUMN_ALIGNMENTS);
    htmlTable.columnWidth= COLUMN_WIDTH;
    htmlTable.border= 0;
    htmlTable.cellPadding= 0;
    htmlTable.cellSpacing= 0;
    htmlTable.isReplaceEmptyCellWithSpace= true;
    htmlTable.isShowColumnHeaders= false;
    htmlTable.isShowRowCounter= false;
    htmlTable.width= 100;
    htmlTable.widthIsPercent= true;
    htmlTable.colorRowBg1= null;
    htmlTable.colorRowBg2= null;
    htmlTable.classRow1= cssClassForRows;
    htmlTable.classRow2= cssClassForRows;
    htmlTable.cssClass= cssClassInnerTable;
    return htmlTable;
  }
  
  private static final String HTML_SIZE_CONTROL_TABLE=
    "<table ?cellspacing='0' cellpadding='0' border='0'> <tr>\n" +
    " <td height='?' width='?' valign='top' align='left'>\n" +
    "  <div style='overflow:auto; width:100%; height:100%;'>" +
    "  ?" +
    "  </div>\n" +
    " </td>\n" +
    "</tr></table>\n"
    ;
  
/**
 * Call this method to get the html of the scrolling list box.
 */  
  public String toString() {
    HtmlTable htmlTable= createHtmlTable();
    String html= htmlTable.toString();
    if (StringUtils.isBlank(height) && StringUtils.isBlank(width)) {
      return html;
    }
    String outer= StringUtils.trimToEmpty(cssClassOuterTable);
    if (StringUtils.isNotEmpty(outer)) {
      outer= "class='" + outer + "' ";
    }
    
    html= Printf.p(HTML_SIZE_CONTROL_TABLE, outer, height, width, html);
    return html;
  }
  
}
