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

import com.tek271.util.collections.list.*;
import com.tek271.util.string.*;
import com.tek271.util.io.FileIO;

/**
 * Build the html for a combo box.
 * @author Abdul Habra
 * <p>Copyright (c) 2005 Technology Exponent</p>
 */
public class ComboBox {
  public String id;
  public String name;
  public int indentSize;
  public boolean disabled= false;
  public String styleClass;
  public String style;
  public String onChange;

  private static final String pNL= StringUtility.NEW_LINE;
  private static final String pEMPTY= StringUtility.EMPTY;

  private ListOfString pItems;
  private ListOfString pValues;
  private String pSelected;

  private StringBuffer pBuffer;
  private String pIndent;

  public ComboBox(final ListOfString aItems,
                  final ListOfString aValues,
                  final String aSelected) {
    pItems= aItems;
    pValues= aValues;
    pSelected= aSelected;
  }

  public ComboBox(final ListOfString aItems,
                  final String aSelected) {
    this(aItems, null, aSelected);
  }

  public ComboBox(final ListOfString aItems) {
    this(aItems, null, null);
  }

  private void append(final String aString) {
    pBuffer.append(aString);
  }

  private void appendLn(final String aString) {
    pBuffer.append(aString).append(pNL);
  }

  private void appendItem(final int aIndex) {
    String item= pItems.getItem(aIndex);
    String val= pValues==null? pEMPTY : pValues.getItem(aIndex);
    boolean isSelected= StringUtility.equals(item, pSelected);

    append(pIndent);
    append("  <option");
    if (isSelected) append(" selected");
    HtmlUtil.appendAttribute(pBuffer, "value", val);
    append(">");
    append(item);
    appendLn("</option>");
  }

  public String toString() {
    StringBuffer buf= new StringBuffer(128);
    toStringBuffer(buf);
    return buf.toString();
  }

  public StringBuffer toStringBuffer(final StringBuffer aBuffer) {
    pIndent= StringUtility.blanks(indentSize);
    pBuffer= aBuffer;
    append(pIndent);
    append("<select size='1'");
    HtmlUtil.appendAttribute(pBuffer, "id", id);
    HtmlUtil.appendAttribute(pBuffer, "name", name);
    if (disabled) append(" disabled='true'");
    HtmlUtil.appendAttribute(pBuffer, "class", styleClass);
    HtmlUtil.appendAttribute(pBuffer, "style", style);
    HtmlUtil.appendAttribute(pBuffer, "onChange", onChange);

    appendLn(">");
    for (int i=0, n=pItems.size(); i<n; i++) {
      appendItem(i);
    }
    append(pIndent);
    appendLn("</select>");
    pBuffer= null;
    return aBuffer;
  }

  public static void main(String[] a) throws Exception {
    ListOfString items= new ListOfString("syria,usa,canada,france,china,jordan", ",");
    ListOfString values= new ListOfString("sy,us,ca,fr,ch,jo", ",");

    ComboBox cb= new ComboBox(items, values, "usa");
    cb.name= "n1";
    cb.indentSize= 2;
    cb.disabled= false;
    cb.style="color:blue;background-color:yellow;font-weight:bolder;font-size:large;font-style:italic;font-family:verdana";

    String s= cb.toString();
    FileIO.write("c:/temp/combo.html", s);
    System.out.println(s);
  }


} // ComboBox
