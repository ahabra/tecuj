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

import com.tek271.util.string.*;
import com.tek271.util.io.*;

/**
 * Display a list of grades (e.g. from 1 to 10) with each grade displayed as a link.
 * Background colors will gradually change from minium grade to max grade
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Grade {

/** Location of text prompt in relation to the grades, used with promptPosition field */
  public static final int PROMPT_LEFT   = 1;
  public static final int PROMPT_RIGHT  = 2;
  public static final int PROMPT_TOP    = 3;
  public static final int PROMPT_BOTTOM = 4;

/** Minimum grade. Default=1 */
  public int minGrade= 1;

/** Maximum grade. Default=10 */
  public int maxGrade= 10;

/** Base url used to form the link for each grade */
  public String baseUrl;

/** The url param name used for the value of the grade */
  public String urlParamName= "grade";

/** Text to describe the loweset grade, default= Bad */
  public String badText= "Bad";

/** Text to describe the heighest grade, default= Good */
  public String goodText= "Good";

/** Background color of the lowest grade, default= red = FF0000 */
  public String colorBgBad= "FF0000";   // red

/** Background color of the heighest grade, default= green = 00FF00 */
  public String colorBgGood= "00FF00";  // green

/** Foreground color of the grade, default= yellow= FFFF00 */
  public String colorFgGrade= "FFFF00";  // yellow

/** Foreground color of the text describing lowest grade, default= white= FFFFFF */
  public String colorFgBadText= "FFFFFF";   // white

/** Foreground color of the text describing heighest grade, default= black= "" */
  public String colorFgGoodText= "";        // black default

/** Background color of the prompt. Default= "" */
  public String colorBgPrompt= "";

/** Foreground color of the prompt. Default= "" */
  public String colorFgPrompt= "";

/** Font size, default= 3 */
  public int fontSize= 3;

/** Number of spaces to pad each grade on the left, default=1. */
  public int leftPadCount=1;    // # of spaces surrouding each grade#

/** Number of spaces to pad each grade on the right, default=1. */
  public int rightPadCount=1;    // # of spaces surrouding each grade#

/** Text or prompt to desplay next to the grades */
  public String prompt;

/**
 * Location of the prompt in relation to the grades. Use on of the PROMPT_x constants.
 * The default is PROMPT_LEFT.
 */
  public int promptPosition= PROMPT_LEFT;

/** Horisontal alignment of this component. May be left, right, center, or empty */
  public String align;

/** Should grades be displayed in bold, default= true. */
  public boolean isBoldGrade= true;

  private static final String pNL= StringUtility.NEW_LINE;
  private StringBuffer pBuffer;
  private String pLeftPad;
  private String pRightPad;


/**
 * Build a row cell element
 * @param aText String The text to show
 * @param aHref String to use with an &lt;a> element
 * @param aFgColor String Foreground color
 * @param aBgColor String Background color
 * @param aFontSize int font size
 * @param aIsBold boolean bold the text
 * @param aColSpan int column span of the cell
 */
  private void buildCell(final String aText,
                         final String aHref,
                         final String aFgColor,
                         final String aBgColor,
                         final int aFontSize,
                         final boolean aIsBold,
                         final int aColSpan) {
    append("    <td");
    TableUtils.appendAttribute(pBuffer, "colSpan", aColSpan);

    appendColorAttribute("bgcolor", aBgColor);
    append(">");

    append(pLeftPad);
    boolean isHref= appendHref(aHref);
    boolean isFont= appendFont(aFgColor, aFontSize);

    if (aIsBold) append("<b>");
    append(aText);

    if (aIsBold) append("</b>");
    if (isFont) append("</font>");
    if (isHref) append("</a>");
    append(pRightPad);
    appendLn("</td>");
  }  // buildCell

  private void appendColorAttribute(final String aName, final String aValue) {
    if (StringUtility.isBlank(aValue)) return;

    pBuffer.append(' ').append(aName).append("='#");
    pBuffer.append(aValue).append('\'');
  }

/** Append an anchore */
  private boolean appendHref(final String aHref) {
    if (StringUtility.isBlank(aHref)) return false;
    append("<a href='");
    append(aHref);
    append("'>");
    return true;
  }

/** Append font */
  private boolean appendFont(final String aFgColor, final int aFontSize) {
    boolean isFg= StringUtility.isNotBlank(aFgColor);
    boolean isSize= aFontSize != 0;
    boolean isAny= isFg || isSize;
    if (!isAny) return false;

    append("<font");
    appendColorAttribute("color", aFgColor);

    TableUtils.appendAttribute(pBuffer, "size", aFontSize);
    append(">");

    return true;
  }  // appendFont

  private void append(final String aString) {
    pBuffer.append(aString);
  }

  private void appendLn(final String aString) {
    pBuffer.append(aString).append(pNL);
  }

/** Add the prompt text */
  private void appendPrompt() {
    int colSpan=0;
    boolean isNewRow= false;
    if (promptPosition==PROMPT_BOTTOM || promptPosition==PROMPT_TOP) {
      colSpan= maxGrade - minGrade + 3;
      isNewRow= true;
      appendLn(" <tr>");
    }
    buildCell(prompt, null, colorFgPrompt, colorBgPrompt, fontSize, false, colSpan);
    if (isNewRow) appendLn(" </tr>");
  }

/** Build a table with links for each grade */
  private void build() {
    pBuffer= new StringBuffer(512);
    boolean hasPrompt= StringUtility.isNotBlank(prompt);
    pLeftPad= StringUtility.repeat(HtmlUtil.SPACE, leftPadCount);
    pRightPad= StringUtility.repeat(HtmlUtil.SPACE, rightPadCount);

    TableUtils.tableStart(pBuffer, 0, null, 0, 0, align);

    if (hasPrompt && promptPosition==PROMPT_TOP)  appendPrompt();
    appendLn(" <tr>");
    if (hasPrompt && promptPosition==PROMPT_LEFT) appendPrompt();

    buildCell(badText, null, colorFgBadText, colorBgBad, fontSize, false, -1);

    ColorGradient cg= new ColorGradient(colorBgBad, colorBgGood, minGrade, maxGrade);
    for (int i= minGrade; i<=maxGrade; i++) {
      String txt= String.valueOf(i);
      String href= baseUrl + "&" + urlParamName + "=" + txt;
      String bg= cg.getColorAt(i);
      buildCell(txt, href, colorFgGrade, bg, fontSize, isBoldGrade, -1);
    }
    buildCell(goodText, null, colorFgGoodText , colorBgGood, fontSize, false, -1);

    if (hasPrompt && promptPosition==PROMPT_RIGHT) appendPrompt();
    appendLn(" </tr>");

    if (hasPrompt && promptPosition==PROMPT_BOTTOM)  appendPrompt();
    appendLn("</table>");
  }  // build()

  public String toString() {
    build();
    return pBuffer.toString();
  }

/** for testing */
  public static void main(String[] args) throws Exception {
    Grade g= new Grade();
    g.prompt= "Rate it";
    g.baseUrl= "";
//    g.leftPadCount= 2;
//    g.rightPadCount= 1;
//    g.align= "center";
     g.colorBgBad= "FF00FF";
     g.colorBgGood= "00FF00";  //  "02FF3A";
    //g.colorFgGrade= "0000FF";
//    g.promptPosition= Grade.PROMPT_TOP;
    //g.fontSize= 2;

    String s= g.toString();
    FileIO.write("c:/temp/grade.html", s);
    System.out.println(s);
  }

}  // Grade
