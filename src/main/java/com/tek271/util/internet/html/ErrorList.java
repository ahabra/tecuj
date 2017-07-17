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

import java.util.*;
import com.tek271.util.string.*;
import com.tek271.util.io.FileIO;
import com.tek271.util.collections.list.*;
import com.tek271.util.exception.*;

/**
 * Collect a list of errors which can be displayed later in one page.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ErrorList {
  public String errorPrefix= "The following error(s) happened:";

  public String style; // eg font-size: 10
  public String colorNamesFG= "red";
  public String colorNamesBG= "";
  public String colorTextFG= "red";
  public String colorTextBG= "";
  public boolean isBoldNames= false;
  public boolean isBoldText= false;
  public String align;

  public String nameValueSeparator= ": ";

  private static final String pNL= StringUtility.NEW_LINE;

  private List pErrName;
  private List pErrText;
  private List pStackTraces;  // of ListOfString objects, each has a stack trace

  public ErrorList() {
    pErrName= new ArrayList();
    pErrText= new ArrayList();
    pStackTraces= new ArrayList();
  }

  public ErrorList(final String aErrorPrefix) {
    this();
    errorPrefix= aErrorPrefix;
  }

  public ErrorList(final String aErrorPrefix,
                   final String aErrorName,
                   final String aErrorText ) {
    this();
    errorPrefix= aErrorPrefix;
    add(aErrorName, aErrorText);
  }

  public void clear() {
    pErrName.clear();
    pErrText.clear();
    pStackTraces.clear();
  }

  public int size() {
    return pErrName.size();
  }

  public boolean isEmpty() {
    return pErrName.isEmpty();
  }

  public boolean isNotEmpty() {
    return !pErrName.isEmpty();
  }

  public int indexOfErrorName(final String aErrorName) {
    return pErrName.indexOf(aErrorName);
  }

  public boolean containsErrorName(final String aErrorName) {
    return pErrName.contains(aErrorName);
  }

  public String getErrorName(final int aIndex) {
    return (String) pErrName.get(aIndex);
  }

  public String getErrorText(final int aIndex) {
    return (String) pErrText.get(aIndex);
  }

  public String getErrorText(final String aErrorName) {
    int i= indexOfErrorName(aErrorName);
    if (i<0) return null;
    return (String) pErrText.get(i);
  }

  public ListOfString getStackTrace(final int aIndex) {
    return (ListOfString) pStackTraces.get(aIndex);
  }

  public ListOfString getStackTrace(final String aErrorName) {
    int i= indexOfErrorName(aErrorName);
    if (i<0) return null;
    return getStackTrace(i);
  }

/** Get the error at the given index */
  public String get(final int aIndex) {
    String en= getErrorName(aIndex);
    String tx= StringUtility.defaultString(getErrorText(aIndex));
    ListOfString list= getStackTrace(aIndex);

    StringBuffer buf= new StringBuffer(64);
    if (en != null) buf.append(en).append(nameValueSeparator);
    buf.append(tx);
    if (list != null) buf.append(pNL).append(list.getText(pNL) );
    return buf.toString();
  }

  private static ListOfString stackTrace2List(final Throwable aThrowable) {
    if (aThrowable==null) return null;

    String st= ExceptionUtil.asString(aThrowable);
    ListOfString r= new ListOfString(st, pNL);
    return r;
  }

  public void add(final String aErrorName,
                  final String aErrorText,
                  final Throwable aThrowable) {
    pErrName.add(aErrorName);
    pErrText.add(aErrorText);
    if (aThrowable==null) {
      pStackTraces.add(null);
      return;
    }
    ListOfString list= stackTrace2List(aThrowable);
    pStackTraces.add(list);
  }


  public void add(final String aErrorName, final String aErrorText) {
    add(aErrorName, aErrorText, null);
  }

  public void add(final String aErrorText) {
    add(null, aErrorText, null);
  }

  public void remove(final int aIndex) {
    pErrName.remove(aIndex);
    pErrText.remove(aIndex);
  }

  public void remove(final String aErrorName) {
    int i= indexOfErrorName(aErrorName);
    if (i<0) return;
    remove(i);
  }

  private static void appendTd(final StringBuffer aBuffer,
                               final String aColorFG,
                               final String aColorBG,
                               final boolean aIsBold,
                               final int aColSpan,
                               String aValue) {
    aBuffer.append("    <td valign='top'");
    TableUtils.appendAttribute(aBuffer, "bgcolor", aColorBG);
    TableUtils.appendAttribute(aBuffer, "colspan", aColSpan);
    aBuffer.append('>');

    boolean isFG= StringUtility.isNotBlank(aColorFG);
    if (isFG) aBuffer.append("<font color='").append(aColorFG).append("'>");
    if (aIsBold) aBuffer.append("<b>");

    aValue= StringUtility.trim(aValue);
    if (aValue.length()==0) aValue= HtmlUtil.SPACE;
    aBuffer.append(aValue);

    if (aIsBold) aBuffer.append("</b>");
    if (isFG) aBuffer.append("</font>");
    aBuffer.append("</td>").append(pNL);
  }

  private void appendTableTop(final StringBuffer aBuffer) {
    aBuffer.append("<table border='0' cellspacing='0' cellpadding='1'");
    TableUtils.appendAttribute(aBuffer, "style", style);
    TableUtils.appendAttribute(aBuffer, "align", align);
    aBuffer.append('>').append(pNL);

    if (StringUtility.isNotBlank(errorPrefix)) {
      aBuffer.append("  <tr>").append(pNL);
      appendTd(aBuffer, colorTextFG, colorTextBG, isBoldText, 4, errorPrefix);
      aBuffer.append("  </tr>").append(pNL);
    }
  }

  private String getTextAndStackTrace(final int aIndex,
                                      final boolean aIsShowStackTraces) {
    String r= getErrorText(aIndex);
    if (! aIsShowStackTraces) return r;

    ListOfString st= getStackTrace(aIndex);
    if (st != null)  r= r + HtmlUtil.BR + st.getText(HtmlUtil.BR);
    return r;
  }

/** Get an html representation of the errors */
  public String toHtml(final boolean aIsShowBullets,
                       final boolean aIsShowCounter,
                       final boolean aIsShowNames,
                       final boolean aIsShowStackTraces) {
    int n= size();
    if (n==0) return StringUtility.EMPTY;

    StringBuffer buf= new StringBuffer(128);
    appendTableTop(buf);
    String sep= StringUtility.defaultString(nameValueSeparator);

    for (int i=0; i<n; i++) {
      buf.append("  <tr>").append(pNL);
      if (aIsShowBullets)  appendTd(buf, colorNamesFG, colorNamesBG, isBoldNames, -1, "*");
      if (aIsShowCounter)  appendTd(buf, colorNamesFG, colorNamesBG, isBoldNames, -1, String.valueOf(i+1) + ".&nbsp;");
      String e= getErrorName(i);
      e= e==null? HtmlUtil.SPACE : e + sep;
      if (aIsShowNames)    appendTd(buf, colorNamesFG, colorNamesBG, isBoldNames, -1, e );

      e= getTextAndStackTrace(i, aIsShowStackTraces);
      appendTd(buf, colorTextFG, colorTextBG, isBoldText, -1, e);
      buf.append("  </tr>").append(pNL);
    }

    buf.append("</table>").append(pNL);
    return buf.toString();
  }  // toHtml()

  public String toHtml() {
    return toHtml(true, false, true, true);
  }


/** Get a errors as a list of string, each error is an item */
  public ListOfString toListOfString() {
    int n= size();
    ListOfString r= new ListOfString(n);
    r.valueEquator= nameValueSeparator;
    for (int i=0; i<n; i++) {
      r.add(get(i));
    }
    return r;
  }  // toListOfString

  public static void main(String[] args) throws Exception {
    ErrorList e= new ErrorList();
    e.add("e1", "msg1");
    e.add("e2", "msg2");
    e.add("e3", "msg3");
    e.add("msg4");
    e.add("e5", "msg5", new Exception("exception"));
    e.add("e6", "msg6", new Exception("exception"));

    e.colorNamesBG= "cyan";
    //e.style= "font-size:10pt";
    //e.align= "center";
    e.errorPrefix= null;

    String s= e.toHtml(false, true, true, true);
    FileIO.write("c:/temp/errList.html", s);
    System.out.println(s);
    for (int i=0; i<e.size(); i++) {
      System.out.println(e.get(i));
    }
  }

}  // ErrorList
