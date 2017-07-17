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

import com.tek271.util.io.FileIO;
import com.tek271.util.string.*;

/**
 * Generate pagination links that could be used to navigate to different search results.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Paginator {

/** total number of items in the search results */
  public int totalCount;

/** How many items will be displayed in each page of search results */
  public int pageSize;

/** How many pagination links will be generated */
  public int maxPagesDisplayed= 10;

/** A prefix string. Default is Result Pages */
  public String prefix= "Result Pages";

/**
 * The url to be used for each link in the pagination. The page index will be appended
 * to this url for each pagination link.
 */
  public String url;

/** The name of the parameter that will be appended to the url. Default is index */
  public String urlParamName= "index";

  public int fontSize= 3;

/** style that will be used with html text. Provide the value of the style attribute. */
  public String style;

  public String align;

  private static final String pNL= StringUtility.NEW_LINE;
  private StringBuffer pBuffer;
  private int pCurrentStart;

/** The index of the start of the current page */
  public void setCurrentStart(final int aCurrentStart) {
    pCurrentStart= Math.max(0, aCurrentStart);
  }

/** The index of the start of the current page */
  public int getCurrentStart() {
    return pCurrentStart;
  }

/** Generate the html string of page links */
  public String toString() {
    build();
    return pBuffer.toString();
  }

  /* example:
  <p> <font size="4">
    <b>Result Pages</b>&nbsp;&nbsp;
    <a href="results.html">First</a> &nbsp;&nbsp;
    <b><a href="results.html">Previous</a> </b> &nbsp;&nbsp;
    <a href="results.html">1</a>
    <a href="results.html">2</a>
    <a href="results.html">3</a>
    <a href="results.html">4</a> &nbsp;&nbsp;&nbsp;
    <b><a href="results.html">Next</a> </b>&nbsp;&nbsp;
    <a href="results.html">Last</a>
  </font> </p>
  */

  private void build() {
    pBuffer= new StringBuffer(128);
    if (totalCount <= pageSize) return;

    append("<p");
    if (StringUtility.isNotBlank(style)) {
      append(" style='", style);
      append("'");
    }
    if (StringUtility.isNotBlank(align)) {
      append(" align='", align);
      append("'");
    }

    append("> <font size='");
    append(fontSize);
    appendLn("'>");

    append("  <b>", prefix);
    appendLn("</b>&nbsp;&nbsp;");

    anchor("First", 0);
    appendLn("&nbsp;&nbsp;");
    append("  <b>");
    anchor("Previous", getPrevious());
    appendLn("</b> &nbsp;");

    buildCounters();

    appendLn("  &nbsp;&nbsp;");
    append("  <b>");
    anchor("Next", getNext());
    appendLn(" </b> &nbsp;");
    int last= pageSize * (totalCount/pageSize);
    if (last==totalCount) last-=pageSize;
    anchorLn("Last", last);
    appendLn("</font> </p>");
  }

  private int getPrevious() {
    int cur= pageSize * (pCurrentStart/pageSize);
    return cur-pageSize;
  }

  private int getNext() {
    int cur= pageSize * (pCurrentStart/pageSize);
    return cur+pageSize;
  }


  private void buildCounters() {
    int halfWindow= pageSize * maxPagesDisplayed/2;
    int firstShown= Math.max(0, pCurrentStart - halfWindow);
    int lastShown= Math.min(totalCount, firstShown + maxPagesDisplayed*pageSize);
    int fs= Math.max(0, lastShown - maxPagesDisplayed*pageSize);
    fs= maxPagesDisplayed * (fs/maxPagesDisplayed);
    firstShown= Math.min(firstShown, fs);
    int cur= pageSize * (pCurrentStart/pageSize);

    for (int i=firstShown; i<lastShown; i=i+pageSize) {
      int pi= i/pageSize +1;
      if (i==cur) {
        append("  ");
        appendLn(pi);
      } else {
        anchorLn(pi, i);
      }
    }
  }  // buildCounters

  private void anchor(final String aText, final int aIndex) {
    if (aIndex<0 || aIndex>=totalCount) {
      append("  ", aText);
      return;
    }

    append("  <a href='", url);
    append("&", urlParamName);
    append("=");
    append(aIndex);
    append("'>", aText);
    append("</a>");
  }

  private void anchorLn(final String aText, final int aIndex) {
    anchor(aText, aIndex);
    pBuffer.append(pNL);
  }

  private void anchorLn(final int aText, final int aIndex) {
    anchorLn(String.valueOf(aText), aIndex);
  }


  private void append(final String aString1, final String aString2) {
    pBuffer.append(aString1).append(aString2);
  }

  private void append(final String aString) {
    pBuffer.append(aString);
  }

  private void append(final int aValue) {
    pBuffer.append(aValue);
  }

  private void appendLn(final String aString) {
    pBuffer.append(aString).append(pNL);
  }

  private void appendLn(final int aValue) {
    pBuffer.append(aValue).append(pNL);
  }


  public static void main(String[] args) throws Exception {
    Paginator p= new Paginator();
    p.fontSize=3;
    p.style= "background-color: #FFEE80";
    p.align="center";
    p.setCurrentStart(40);
    p.maxPagesDisplayed= 9;
    p.pageSize= 10;
    p.totalCount= 300;
    p.url= "find?";

    String s= p.toString();
    FileIO.write("t1.html", s);
    System.out.println(s);
  }

}  // Paginator
