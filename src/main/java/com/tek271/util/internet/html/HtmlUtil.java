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

import org.apache.commons.lang3.StringUtils;

import com.tek271.util.collections.list.*;
import com.tek271.util.string.*;

/**
 * Static methods to build different HTML tags and elements.
 * Supported elements include:
 * &lt;a>, &lt;b>, &lt;br>, &lt;h>, &lt;i>, &lt;img>, &lt;p>, Check box, Font color,
 * Numbered lines, ...
 * <p>The purpose of this class is to simplify the creation of HTML elements in Java code.</p>
 * <p>Copyright (c) 2003 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class HtmlUtil {

public static final String SPACE =  "&nbsp;";
public static final String NEW_LINE = System.getProperty("line.separator");
public static final String SPACE_NEW_LINE = SPACE + NEW_LINE;
public static final String BR = "<br>";
public static final String RIGHT = "right";
public static final String CENTER = "center";
public static final String LEFT = "left";
public static final String HR = "<hr>" + NEW_LINE;

private static final String mOPTION_START = "<option>";
private static final String mOPTION_END = "</option>";
private static final String mOPTION_SELECTED = "<option selected>";

public HtmlUtil() {}

/** Create an anchor element */
public static void a(StringBuffer aBuf, String aHref, String aParams, String aData) {
  aBuf.append("<a href=\"").append(aHref);
  if (! StringUtility.isEmpty(aParams)) {
    aBuf.append("?").append(aParams);
  }
  aBuf.append("\">").append(aData).append("</a>");
} // a

/** Create an anchor element */
public static void a(StringBuffer aBuf, String aHref, String aParams, String aData,
                     String aTarget, String aOnClick) {
  aBuf.append("<a href=\"").append(aHref);
  if (aParams.length() >0) {
    aBuf.append("?").append(aParams);
  }
  aBuf.append(" TARGET=\"").append(aTarget);
  aBuf.append("\" ONCLICK=\"").append(aOnClick).append("\"");

  aBuf.append("\">").append(aData).append("</a>");
} // a

private static void appendFeature(StringBuffer aWorkBuffer,
                                  String aFeature, boolean aValue) {
  aWorkBuffer.append(aFeature).append('=');
  aWorkBuffer.append(aValue? "yes" : "no");
  aWorkBuffer.append(',');
} // appendFeature();

private static void appendFeature(StringBuffer aWorkBuffer,
                                  String aFeature, int aValue) {
  aWorkBuffer.append(aFeature).append('=');
  aWorkBuffer.append(aValue);
} // appendFeature();

/** Create an anchor element that will create a new window when clicked. */
public static void anchorNewWindow(StringBuffer aBuf, String aCaption,
                                   String aUrl, String aWindowName,
                                   int aWidth, int aHeight,
                                   boolean aMenuBar, boolean aToolBar,
                                   boolean aLocation, boolean aStatus,
                                   boolean aScrollBars, boolean aResizable) {
  aBuf.append("<a target='").append(aWindowName);
  aBuf.append("' href='").append(aUrl);
  aBuf.append("' OnClick=\"window.open('");
  aBuf.append(aUrl);
  aBuf.append("', '").append(aWindowName);
  aBuf.append("', '");
  appendFeature(aBuf, "menubar", aMenuBar);
  appendFeature(aBuf, "toolbar", aToolBar);
  appendFeature(aBuf, "location", aLocation);
  appendFeature(aBuf, "status", aStatus);
  appendFeature(aBuf, "scrollbars", aScrollBars);
  appendFeature(aBuf, "resizable", aResizable);
  if (aWidth >0)
    appendFeature(aBuf, "width", aWidth);
  if (aHeight >0) {
    aBuf.append(',');
    appendFeature(aBuf, "height", aHeight);
  }
  aBuf.append("')\">").append(aCaption);
  aBuf.append("</A>").append(NEW_LINE);
} //  anchorNewWindow

/**
 * Create a mailto anchore.
 * @param aBuf StringBuffer to append to
 * @param aToEmail String the email address for the mailto
 * @param aText String Caption of the anchore
 * @param aIsEncode boolean should the email be encoded to protect against web bots.
 */
public static void anchoreEmail(final StringBuffer aBuf,
                                String aToEmail,
                                String aText,
                                final boolean aIsEncode) {
  if (aIsEncode) {
    aToEmail= StringEscapeUtility.escapeUrl(aToEmail, true);
    aText= StringEscapeUtility.escapeHtml(aText, aIsEncode);
  }
  a(aBuf, "mailto:" + aToEmail, null, aText);
}  // anchoreEmail

/**
 * Create a mailto anchore, using the given email address in the anchor's text.
 * @param aBuf StringBuffer to append to
 * @param aToEmail String the email address for the mailto
 * @param aIsEncode boolean should the email be encoded to protect against web bots.
 */
public static void anchoreEmail(final StringBuffer aBuf,
                                String aToEmail,
                                final boolean aIsEncode) {
  anchoreEmail(aBuf, aToEmail, aToEmail, aIsEncode);
} // anchoreEmail

/**
 * Create a mailto anchore, using the given email address in the anchor's text, will
 * encode the email address to protect against web bots.
 * @param aBuf StringBuffer to append to
 * @param aToEmail String the email address for the mailto
 */
public static void anchoreEmail(final StringBuffer aBuf,
                                String aToEmail) {
  anchoreEmail(aBuf, aToEmail, aToEmail, true);
}

public static void h(StringBuffer aBuf, int aSize, String aAlign, String aData) {
  aBuf.append("<h").append(aSize).append(" align=\"");
  aBuf.append(aAlign).append("\">");
  aBuf.append(aData).append("</h").append(aSize).append('>').append(NEW_LINE);
} // h

public static void br(StringBuffer aBuf, int aCount) {
  for (int i=0; i<aCount; i++) {
    aBuf.append(BR);
  }
  aBuf.append(NEW_LINE);
} // br

public static void space(StringBuffer aBuf, int aCount) {
  for (int i=0; i<aCount; i++) {
    aBuf.append(SPACE);
  }
} // space

public static void b(StringBuffer aBuf, String aData) {
  aBuf.append("<b>").append(aData).append("</b>");
} // b

public static void i(StringBuffer aBuf, String aData) {
  aBuf.append("<i>").append(aData).append("</i>");
} // b

public static void imgWithTable(StringBuffer aBuf, String aUrl, int aWidth,
                                int aHeight, int aBorder,
                                String aAlign) {
  aBuf.append("<table border=").append(aBorder);
  if (aAlign != null) {
    if (aAlign.length() > 0)  aBuf.append(" align=\"").append(aAlign).append("\"");
  }
  aBuf.append(" cellpadding=0 cellspacing=0> <tr> <td>");

  aBuf.append("<img");
  if (aWidth >= 0)    aBuf.append(" width=").append(aWidth);
  if (aHeight >= 0)   aBuf.append(" height=").append(aHeight);
  aBuf.append(" src=\"").append(aUrl);
  aBuf.append("\"> </td> </tr> </table>");
  aBuf.append(NEW_LINE);
} // imgWithTable

public static void img(StringBuffer aBuf, String aUrl, int aWidth,
                       int aHeight, int aBorder,
                       String aAlign) {
  aBuf.append("<img border=").append(aBorder);
  if ( (aAlign != null) && (aAlign.length() > 0) ) {
    aBuf.append(" align=\"").append(aAlign).append("\"");
  }
  if (aWidth >= 0)    aBuf.append(" width=").append(aWidth);
  if (aHeight >= 0)   aBuf.append(" height=").append(aHeight);
  aBuf.append(" src=\"").append(aUrl);
  aBuf.append("\">");
} // img

public static void p(StringBuffer aBuf, String aData) {
  aBuf.append("<p>").append(aData).append("</p>").append(NEW_LINE);
}

/**
* Format aName to normal font and aValue to BOLD.
* This is used to display the details data: e.g. age, sex, ...
*/
public static void makeCell(StringBuffer aBuf, String aName, String aValue) {
  aBuf.append(aName).append(": <b>").append(aValue).append("</b>");
  space(aBuf, 4);
} // makeCell

public static void appendLI(StringBuffer aBuf, String aItem) {
    aBuf.append("  <li>").append(aItem).append("</li>").append(NEW_LINE);
} // appendLI

/**
* Display aItems as a list using aStartTag/aEndTag to start/end list.
*/
private static void showList(StringBuffer aBuf,
                               String[] aItems, String aStartTag, String aEndTag) {
  aBuf.append(aStartTag).append(NEW_LINE);
  for (int i=0; i<aItems.length; i++) {
    appendLI(aBuf, aItems[i]);
  }
  aBuf.append(aEndTag).append(NEW_LINE);
} // showList

/**
* Display aItems as a bulleted list.
*/
public static void ul(StringBuffer aBuf, String[] aItems) {
  showList(aBuf, aItems, "<ul>", "</ul>");
} // ul

/**
* Display aItems as a bulleted list.
*/
public static void ol(StringBuffer aBuf, String[] aItems) {
  showList(aBuf, aItems, "<ol>", "</ol>");
} // ol

public static void color(StringBuffer aBuf, String aColor, String aData) {
  if ( (aData==null) || (aData.length()==0) ) return;

  aBuf.append("<font color=\"").append(aColor).append("\">");
  aBuf.append(aData).append("</font>");
} // color

public static void error(StringBuffer aBuf, String aMsg) {
  if ( StringUtility.isEmpty(aMsg) ) return;

  aBuf.append("<p>");
  color(aBuf, "red", aMsg);
  aBuf.append("</p>").append(NEW_LINE);
} // error()

public static void errorBold(StringBuffer aBuf, String aMsg) {
  if ( StringUtility.isEmpty(aMsg) ) return;

  aBuf.append("<p>");
  aBuf.append("<b>");
  color(aBuf, "red", aMsg);
  aBuf.append("</b>");
  aBuf.append("</p>").append(NEW_LINE);
} // errorBold()

/**
* Make a combo box.
* @param aOptions Option items separated by comma.
*/
public static void combo(StringBuffer aBuf,
                         String aName, int aTabIndex,
                         String aSelected,
                         String aOptions) {
  combo(aBuf, aName, aTabIndex, null, null, aSelected, aOptions);
} // combo

/**
* Make a combo box.
* @param aOptions Option items separated by comma.
*/
public static void combo(StringBuffer aBuf,
                         String aName, int aTabIndex,
                         String aClass, String aStyle,
                         String aSelected,
                         String aOptions) {
  aBuf.append("<select size=1 name=\"");
  aBuf.append(aName).append("\" tabindex=");
  aBuf.append(aTabIndex);
  if (StringUtility.isNotBlank(aClass)) {
    aBuf.append(" class=\"").append(aClass).append("\"");
  }
  if (StringUtility.isNotBlank(aStyle)) {
    aBuf.append(" style=\"").append(aStyle).append("\"");
  }
  aBuf.append(">").append(NEW_LINE);

  ListOfString list = new ListOfString();
  list.lineSeparator = ",";
  list.setText(aOptions);
  String item;
  for (int i=0; i<list.size(); i++) {
    aBuf.append("  ");
    item = (String) list.get(i);
    aBuf.append(item.equals(aSelected) ?  mOPTION_SELECTED : mOPTION_START);
    aBuf.append(item).append(mOPTION_END).append(NEW_LINE);
  }
  aBuf.append("</select>");
} // combo


public static void textBox(StringBuffer aBuf, String aName,
                           String aValue,
                           int aSize,
                           int aMaxLength,
                           int aTabIndex) {
  aBuf.append("<input type=text name=\"");
  aBuf.append(aName).append("\" size=").append(aSize);
  aBuf.append(" maxlength=").append(aMaxLength);
  aBuf.append(" tabindex=").append(aTabIndex);
  aBuf.append(" value=\"").append(aValue);
  aBuf.append("\">");
} // textBox

public static void textArea(StringBuffer aBuf, String aName, String aValue,
                            int aRows, int aColumns, int aTabIndex) {
  aBuf.append("<textarea name=\"").append(aName);
  aBuf.append("\" rows=").append(aRows);
  aBuf.append(" cols=").append(aColumns);
  aBuf.append(" tabindex=").append(aTabIndex);
  aBuf.append(">").append(aValue);
  aBuf.append("</textarea>");
} // textArea

public static void passwordBox(StringBuffer aBuf, String aName,
                               String aValue,
                               int aSize,
                               int aMaxLength,
                               int aTabIndex) {
  aBuf.append("<input type=password name=\"");
  aBuf.append(aName).append("\" size=").append(aSize);
  aBuf.append(" maxlength=").append(aMaxLength);
  aBuf.append(" tabindex=").append(aTabIndex);
  aBuf.append(" value=\"").append(aValue);
  aBuf.append("\">");
} // passwordBox

public static void hidden(StringBuffer aBuf, String aName, String aValue) {
  aBuf.append("<input type=hidden name=\"");
  aBuf.append(aName);
  aBuf.append("\" value=\"");
  aBuf.append(aValue);
  aBuf.append("\">");
}  // hidden

/**
* Create a set of checkbox items.
* @param aBuf A buffer which the check boxs will be added to.
* @param aID The ID of the checkboxes. A sequential number (starting at 1)
*   will be added to aID to create the id of each checkbox.
* @param aOnClickFunction The name of a java script function that will
*   be called when a checkbox is clicked. This function will take a parameter
*   the id of the clicked checkbox.
* @param aItemPrefix A string to be added at the start of each item. This will
*   allow having each checkbox in different line.
* @param aItems An array of text to be displayed for each checkbox.
* @param aChecked An array of the items that are checked.
*/
public static void makeCheckBox(StringBuffer aBuf,
                                String aID,
                                String aOnClickFunction,
                                String aItemPrefix,
                                String[] aItems,
                                boolean[] aChecked) {
  if (aItemPrefix==null) aItemPrefix= StringUtility.EMPTY;
  for (int i=0; i<aItems.length; i++) {
    aBuf.append(aItemPrefix);
    aBuf.append("<INPUT TYPE=checkbox ");
    if (aChecked!= null)
      if (aChecked[i])  aBuf.append("CHECKED ");
    aBuf.append("ID=").append(aID).append(i+1).append(' ');
    if (!StringUtility.isEmpty(aOnClickFunction)) {
      aBuf.append("ONCLICK=\"").append(aOnClickFunction);
      aBuf.append("('").append(aID).append(i+1).append("')\"");
    }
    aBuf.append(">").append(aItems[i]);
    aBuf.append("</INPUT>").append( NEW_LINE );
  } // for
} // makeCheckBox();

/**
* Create a set of radio buttons.
* @param aBuf A buffer which the radio buttons will be added to.
* @param aName The name of the radio buttons.
* @param aItemPrefix A string to be added at the start of each item. This will
*   allow having each radio button in different line.
* @param aItems An array of text to be displayed for each radio button.
* @param aChecked The index of the selected item starting at zero. -ve if
*   no selected item.
*/
public static void makeRadio(StringBuffer aBuf,
                             String aName,
                             String aItemPrefix,
                             String[] aItems,
                             int aChecked) {
  if (aItemPrefix==null) aItemPrefix= StringUtility.EMPTY;
  for (int i=0; i<aItems.length; i++) {
    aBuf.append(aItemPrefix);
    aBuf.append("<INPUT TYPE=radio NAME=").append(aName);
    if (aChecked == i)
      aBuf.append(" CHECKED");
    aBuf.append(">").append(aItems[i]);
    aBuf.append("</INPUT>").append( NEW_LINE );
  } // for
} // makeRadio();


public static void checkBox(StringBuffer aBuf,
                            String aName, boolean aIsChecked,
                            int aTabIndex, String aText) {
  aBuf.append("<input type='checkbox' name='");
  aBuf.append(aName);
  aBuf.append("' ");
  if (aIsChecked)  aBuf.append("checked ");
  if (aTabIndex>0) {
    aBuf.append("tabindex='");
    aBuf.append(aTabIndex);
    aBuf.append("'");
  }
  aBuf.append(">");
  aBuf.append(aText);
}  // checkBox

public static void checkBox(StringBuffer sb, boolean isChecked, boolean isDisabled,
                            String id, String name, String onClick,
                            String value, String text) {
  sb.append("<input type='checkbox' ");
  if (isChecked) sb.append("checked ");
  if (isDisabled) sb.append("disabled ");

  appendAttribute(sb, "id", id);
  appendAttribute(sb, "name", name);
  appendAttribute(sb, "onClick", onClick);
  appendAttribute(sb, "value", value);
  sb.append("/>");
  if (StringUtils.isNotBlank(text)) sb.append(text);
}

public static StringBuffer appendAttribute(final StringBuffer aBuf,
                                           final String aName,
                                           final String aValue) {
  if (StringUtility.isBlank(aValue)) return aBuf;

  if (! endsWith(aBuf, ' ')) aBuf.append(StringUtility.BLANK);
  aBuf.append(aName);
  aBuf.append(StringUtility.EQUAL);
  aBuf.append('\'');
  aBuf.append(aValue);
  aBuf.append('\'');
  aBuf.append(StringUtility.BLANK);
  return aBuf;
}

private static boolean endsWith(final StringBuffer aBuf, char aChar) {
  if (aBuf==null) return false;
  int size= aBuf.length();
  if (size==0) return false;
  
  char last= aBuf.charAt(size-1);
  return last==aChar;
}

public static StringBuffer appendAttribute(final StringBuffer aBuf,
                                           final String aName,
                                           final int aValue) {

  if (aValue<0) return aBuf;
  return appendAttribute(aBuf, aName, String.valueOf(aValue));
}

//<span style="position:relative; color:gray">
//A headline with a drop shadow
//<span style="position:absolute; color:black; left:-3px; top:-3px;">A headline with a drop shadow</span>
//</span>

public static StringBuffer shadowText(final StringBuffer aBuf,
                                      final String aText,
                                      final String aColor,
                                      final String aShadowColor,
                                      final String aBackgroundColor,
                                      final int aShadowShift) {
  aBuf.append("<span style=\"position:relative; color:");
  aBuf.append(aShadowColor).append("\">");
  aBuf.append(aText);
  aBuf.append("<font ");
  if (StringUtility.isNotBlank(aBackgroundColor)) {  // invisible when no css
    aBuf.append("color=\"").append(aBackgroundColor).append("\" ");
  }
  aBuf.append("style=\"position:absolute; color:");
  aBuf.append(aColor);
  aBuf.append("; left:").append(aShadowShift);
  aBuf.append("px; top:").append(aShadowShift);
  aBuf.append("px;\">");
  aBuf.append(aText);
  aBuf.append("</font></span>");
  return aBuf;
}

/**
 * Create a text with shadow
 * @param aText The text to display with shadow
 * @param aColor color of the text
 * @param aShadowColor color of the shadow
 * @param aBackgroundColor background color of the page
 * @param aShadowShift amount of shadow's shift in pixels.
 * @return The text with a shadow
 */
public static String shadowText(final String aText,
                                final String aColor,
                                final String aShadowColor,
                                final String aBackgroundColor,
                                final int aShadowShift) {
  StringBuffer sb= new StringBuffer(64);
  shadowText(sb, aText, aColor, aShadowColor, aBackgroundColor, aShadowShift);
  return sb.toString();
}

public static void main(String[] a) {
  StringBuffer buf= new StringBuffer();
  anchoreEmail(buf, "ahabra@yahoo.com");
  System.out.println(buf);
}

} // Element
