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

package com.tek271.util.xml;

import java.io.*;
import javax.xml.parsers.*;
//import org.xml.sax.AttributeList;
//import org.xml.sax.HandlerBase;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.apache.commons.lang3.StringEscapeUtils;
import com.tek271.util.log.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
 * General XML utility methods, including the building of XML tags from their components.
 * <p>Copyright (c) 2003-2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class XmlUtil {
  private static final String pCLASS_NAME= "com.tek271.util.xml.XmlUtil.";

  public static final String AMP_STR = "&amp;";
  public static final String LT_STR = "&lt;";
  public static final String GT_STR = "&gt;";
  public static final String DOUBLEQUOTE_STR = "&quot;";
  public static final String APOS_STR = "&apos;";
  public static final String AMP_NUM = "&#038;";
  public static final String LT_NUM = "&#060;";
  public static final String GT_NUM = "&#062;";
  public static final String DOUBLEQUOTE_NUM = "&#034;";
  public static final String APOS_NUM = "&#039;";

  private XmlUtil() {}

///**
// * Create a SAX parser and parse the given xml string.
// * @param aXml An XML formatted string.
// * @param aHandler Handler of parsing events. Note that HandlerBase is depricated in
// *   JDK 1.4 and J2EE 1.4.
// * @param aValidate boolean Specifies that the parser will validate documents as
// *   they are parsed.
// * @param aLogger A logger used when errors occure. The log will contain the text
// * of the XML String.
// * @return true if success, false if not.
// * @deprecated the HandlerBase class is deprecated
// */
//  public static boolean parseSax(final String aXml,
//                                 final HandlerBase aHandler,
//                                 final boolean aValidate,
//                                 final ILogger aLogger) {
//    String method= pCLASS_NAME + "parseSax(): ";
//
//    StringReader strRdr= new StringReader(aXml);     // convert string to Reader
//    InputSource inpSrc = new InputSource(strRdr);    // convert Reader to InputSource
//
//    SAXParserFactory factory= SAXParserFactory.newInstance();
//    factory.setValidating(aValidate);
//
//    try {
//      SAXParser parser = factory.newSAXParser();
//      parser.parse(inpSrc, aHandler);
//    }
//    catch (Exception ex) {
//      aLogger.log(ILogger.ERROR,
//                  method + "SAX Parsing the following XML Data:\n" +
//                  aXml +
//                  "\n--------- END OF XML DATA ---------",
//                  ex);
//      return false;
//    }
//    return true;
//  }  // parseSax

/**
 * Create a SAX parser and parse the given xml string.
 * @param aXml An XML formatted string.
 * @param aHandler Handler of parsing events.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger A logger used when errors occure. The log will contain the text
 * of the XML String.
 * @return true if success, false if not.
 */
  public static boolean parseSax(final String aXml,
                                 final DefaultHandler aHandler,
                                 final boolean aValidate,
                                 final ILogger aLogger) {
    String method= pCLASS_NAME + "parseSax(): ";

    StringReader strRdr= new StringReader(aXml);     // convert string to Reader
    InputSource inpSrc = new InputSource(strRdr);    // convert Reader to InputSource

    SAXParserFactory factory= SAXParserFactory.newInstance();
    factory.setValidating(aValidate);

    try {
      SAXParser parser = factory.newSAXParser();
      parser.parse(inpSrc, aHandler);
    }
    catch (Exception ex) {
      aLogger.log(ILogger.ERROR,
                  method + "SAX Parsing the following XML Data:\n" +
                  aXml +
                  "\n--------- END OF XML DATA ---------",
                  ex);
      return false;
    }
    return true;
  }  // parseSax


///**
// * Create a non-validating SAX parser and parse the given xml string.
// * @param aXml An XML formatted string.
// * @param aHandler Handler of parsing events. Note that HandlerBase is depricated in
// *   JDK 1.4 and J2EE 1.4.
// * @param aLogger A logger used when errors occure. The log will contain the text
// * of the XML String.
// * @return true if success, false if not.
// * @deprecated the HandlerBase class is deprecated
// */
//  public static boolean parseSax(final String aXml,
//                                 final HandlerBase aHandler,
//                                 final ILogger aLogger) {
//    return parseSax(aXml, aHandler, false, aLogger);
//  }  // parseSax

/**
 * Create a non-validating SAX parser and parse the given xml string.
 * @param aXml An XML formatted string.
 * @param aHandler Handler of parsing events.
 * @param aLogger A logger used when errors occure. The log will contain the text
 * of the XML String.
 * @return true if success, false if not.
 */
  public static boolean parseSax(final String aXml,
                                 final DefaultHandler aHandler,
                                 final ILogger aLogger) {
    return parseSax(aXml, aHandler, false, aLogger);
  }  // parseSax

///**
//* Convert an AttributeList to a ListOfString object
//* @deprecated The AttributeList interface is deprecated
//*/
//  public static ListOfString attributeList2List(final AttributeList aAttributes) {
//    if (aAttributes==null) return null;
//    ListOfString r= new ListOfString();
//    for (int i=0, n=aAttributes.getLength(); i<n; i++) {
//      r.add(aAttributes.getName(i), aAttributes.getValue(i) );
//    }
//    return r;
//  }  // attributeList2List

/** Convert an Attributes to a ListOfString object */
  public static ListOfString attributeList2List(final Attributes aAttributes) {
    if (aAttributes==null) return null;
    ListOfString r= new ListOfString();
    for (int i=0, n=aAttributes.getLength(); i<n; i++) {
      r.add(aAttributes.getQName(i), aAttributes.getValue(i) );
    }
    return r;
  }

  /**
   * Extract the text of a tag from an xml string.
   * @param aXml The xml string to search.
   * @param aTagName The name of the tag.
   * @return The text of the FIRST tag found. return null if not found.
   * <p>Example:
   * String xml="<t1>hello</t1><t2 a1='1'></t2>"
   * extractTag(xml, "t2") will return <t2 a1='1'>
   */
  public static String extractTag(final String aXml, String aTagName) {
    if (StringUtility.isBlank(aXml)) return null;
    aTagName= '<' + aTagName;
    int start = indexOfTag(aXml, aTagName);
    if (start<0) return null;

    int last = aXml.indexOf('>', start+1);
    if (last<0) return null;
    return aXml.substring(start, last+1);
  } // extractTag

  /** Find the index of the first tag in the xml string */
  private static int indexOfTag(final String aXml, final String aTagName) {
    int n= aTagName.length();
    int i=-1;
    while (true) {
      i= aXml.indexOf(aTagName, i+1);
      if (i<0) return -1;
      char ch= aXml.charAt(i + n);
      if (ch==' ' || ch=='/' || ch=='>') return i;
    }
  }  // indexOfTag

  /**
   * Extract attributes names and values from a tag.
   * @param aTag An xml tag that has attributes.
   * @return A list of name=value pairs for the tag's attributes.
   */
  public static ListOfString extractAttributes(String aTag) {
    String atts= extractAttributesAsString(aTag);
    if (StringUtility.isBlank(atts)) return null;
    ListOfString r= new ListOfString();
    String name, value;
    int attsLength= atts.length();
    int[] index= {0};
    while (true) {
      name = getNextName(atts, index);
      if (StringUtility.isBlank(name)) return r;
      if (index[0] > attsLength) {
        r.add(name); // no value for name
        return r;
      }
      index[0]= StringUtility.indexOfAnyBut(atts, StringUtility.WHITE_SPACE, index[0]);
      if (index[0]<0) {
        r.add(name); // no value for name
        return r;
      }
      if (atts.charAt(index[0]) != '=') {  // name has no value, just add name
        r.add(name);
        continue;
      }
      index[0]++; // skip =
      value= getNextValue(atts, index);
      r.add(name, value);
    }
  } // extractAttributes

  /** Extract the attributes off a tag */
  private static String extractAttributesAsString(String aTag) {
    if (StringUtility.isBlank(aTag)) return StringUtility.EMPTY;
    aTag= aTag.trim();
    if ( (aTag.charAt(0)!='<') || StringUtility.lastChar(aTag)!='>') return null;

    int startAtt = aTag.indexOf(' ');
    if (startAtt<0) return StringUtility.EMPTY;
    String r= aTag.substring(startAtt, aTag.length()-1).trim();
    if (StringUtility.lastChar(r)=='/') r= r.substring(0, r.length()-1).trim();
    return r;
  }  // extractAttributesAsString

  /** Extract the next name from a string of attributes starting at aIndex[0] */
  private static String getNextName(String aAttributes, int[] aIndex) {
    if (aIndex[0] >= aAttributes.length()) return null;

    int start= StringUtility.indexOfAnyBut(aAttributes, StringUtility.WHITE_SPACE, aIndex[0]);
    if (start<0) return null;
    int last= StringUtility.indexOfAny(aAttributes, StringUtility.WHITE_SPACE + "=", start+1);
    if (last<0) {
      aIndex[0]= aAttributes.length();
      return aAttributes.substring(start);
    }
    aIndex[0]= last;
    return aAttributes.substring(start, last);
  } // getNextName

  /** Extract the next value from a string of attributes starting at aIndex[0] */
  private static String getNextValue(String aAttributes, int[] aIndex) {
    if (aIndex[0] >= aAttributes.length()) return null;

    int start= StringUtility.indexOfAnyBut(aAttributes, StringUtility.WHITE_SPACE, aIndex[0]);
    if (start<0) return null;
    char q= aAttributes.charAt(start);
    int last;
    if (StringUtility.isQuote(q)) {
      last= aAttributes.indexOf(q, ++start);
    } else {
      last= StringUtility.indexOfAny(aAttributes, StringUtility.WHITE_SPACE, start+1);
    }
    if (last<0) {
      aIndex[0]= aAttributes.length();
      return aAttributes.substring(start);
    }
    aIndex[0]= last+1;
    return aAttributes.substring(start, last);
  } // getNextValue

  private static void appendAttributes(StringBuffer aBuffer,
                                       ListOfString aAttributes) {
    if (aAttributes==null) return;

    for (int i = 0, n = aAttributes.size(); i < n; i++) {
      aBuffer.append(' ').append(aAttributes.getNameAtIndex(i));
      String v= aAttributes.getValueAtIndex(i);
      char q= StringUtility.contains(v, '"') ? '\'' : '"';
      aBuffer.append('=').append(q);
      aBuffer.append(v);
      aBuffer.append(q);
    }
  }

  private static StringBuffer buildTag(final StringBuffer aBuffer,
                                       final String aName,
                                       final ListOfString aAttributes,
                                       final int aIndentSize,
                                       final boolean aAppendNewLine,
                                       final String aCloseChar) {
    if (aIndentSize>0) aBuffer.append(StringUtility.blanks(aIndentSize));
    aBuffer.append('<').append(aName);
    appendAttributes(aBuffer, aAttributes);
    aBuffer.append(aCloseChar);
    if (aAppendNewLine) aBuffer.append(StringUtility.NEW_LINE);
    return aBuffer;
  }

  /**
   * Build an xml start tag into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @param aAttributes List of attributes.
   * @param aIndentSize # of blanks to prepend
   * @param aAppendNewLine Add a line break at the end of the tag
   * @return The Same string buffer with the tag appended.
   */
  public static StringBuffer tagStart(final StringBuffer aBuffer,
                                      final String aName,
                                      final ListOfString aAttributes,
                                      final int aIndentSize,
                                      final boolean aAppendNewLine) {
    return buildTag(aBuffer, aName, aAttributes, aIndentSize, aAppendNewLine, ">");
  } // tagStart

  /**
   * Build an xml start tag into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @param aAttributes List of attributes.
   * @return The Same string buffer with the tag appended, with NO prepended blanks and
   * no line break at the end.
   */
  public static StringBuffer tagStart(final StringBuffer aBuffer,
                                      final String aName,
                                      final ListOfString aAttributes) {
    return tagStart(aBuffer, aName, aAttributes, 0, false);
  }

  /**
   * Build an xml start tag into the given buffer without attributes.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @return The Same string buffer with the tag appended, with NO prepended blanks and
   * no line break at the end.
   */
  public static StringBuffer tagStart(final StringBuffer aBuffer,
                                      final String aName) {
    return tagStart(aBuffer, aName, null, 0, false);
  }

/** Create a <tag /> */
  public static StringBuffer tagStartEnd(final StringBuffer aBuffer,
                                         final String aName,
                                         final ListOfString aAttributes,
                                         final int aIndentSize,
                                         final boolean aAppendNewLine) {
    return buildTag(aBuffer, aName, aAttributes, aIndentSize, aAppendNewLine, "/>");
  } // tagStartEnd

  /**
   * Build an xml end tag into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @param aIndentSize # of blanks to prepend
   * @param aAppendNewLine Add a line break at the end of the tag
   * @return The Same string buffer with the tag appended.
   */
  public static StringBuffer tagEnd(final StringBuffer aBuffer,
                                    final String aName,
                                    final int aIndentSize,
                                    final boolean aAppendNewLine) {
    if (aIndentSize>0) aBuffer.append(StringUtility.blanks(aIndentSize));
    aBuffer.append('<').append('/').append(aName).append('>');
    if (aAppendNewLine) aBuffer.append(StringUtility.NEW_LINE);
    return aBuffer;
  } // tagEnd

  /**
   * Build an xml end tag into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @return The Same string buffer with the tag appended, with 0 prepended blanks and
   * no line break at the end.
   */
  public static StringBuffer tagEnd(final StringBuffer aBuffer,
                                    final String aName) {
    return tagEnd(aBuffer, aName, 0, false);
  } // tagEnd

  /**
   * Build a full xml element into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @param aAttributes List of attributes.
   * @param aData the data of the element
   * @param aIndentSize # of blanks to prepend
   * @param aAppendNewLine Add a line break at the end of the tag
   * @return The Same string buffer with the tag appended.
   * <br>history 2006.06.14 - Abdul Habra: Replaced escapeHtml() with escapeXml() based
   * on Doug Estep suggestion.
   */
  public static StringBuffer tagFull(final StringBuffer aBuffer,
                                     final String aName,
                                     final ListOfString aAttributes,
                                     final String aData,
                                     final int aIndentSize,
                                     final boolean aAppendNewLine) {
    if (StringUtility.isBlank(aData)) {
      return tagStartEnd(aBuffer, aName, aAttributes, aIndentSize, aAppendNewLine);
    }
    tagStart(aBuffer, aName, aAttributes, aIndentSize, false);
    aBuffer.append(StringEscapeUtils.escapeXml(StringUtility.defaultString(aData)) );
    tagEnd(aBuffer, aName, 0, aAppendNewLine);
    return aBuffer;
  }  // tagFull

  /**
   * Build a full xml element into the given buffer.
   * @param aBuffer The buffer to append to.
   * @param aName Name of tag
   * @param aAttributes List of attributes.
   * @param aData the data of the element
   * @return The Same string buffer with the tag appended, with 0 prepended blanks and
   * no line break at the end.
   */
  public static StringBuffer tagFull(final StringBuffer aBuffer,
                                     final String aName,
                                     final ListOfString aAttributes,
                                     final String aData) {
    return tagFull(aBuffer, aName, aAttributes, aData, 0, false);
  }

  /**
   * Add a CData section to the buffer.
   * @param aBuffer The buffer to append to.
   * @param aCData The contents of the cdata section
   * @param aIndentSize # of blanks to prepend
   * @return The Same string buffer with the cdata appended.
   */
  public static StringBuffer tagCData(final StringBuffer aBuffer,
                                      final String aCData,
                                      final int aIndentSize) {
    if (aIndentSize>0) aBuffer.append(StringUtility.blanks(aIndentSize));
    aBuffer.append("<![CDATA[");
    aBuffer.append(StringUtility.defaultString(aCData));
    aBuffer.append("]]>").append(StringUtility.NEW_LINE);
    return aBuffer;
  } // tagCData

  /**
   * Add a CData section to the buffer.
   * @param aBuffer The buffer to append to.
   * @param aCData The contents of the cdata section
   * @return The Same string buffer with the cdata appended, with 0 prepended blanks.
   */
  public static StringBuffer tagCData(final StringBuffer aBuffer,
                                      final String aCData) {
    return tagCData(aBuffer, aCData, 0);
  }

  public static StringBuffer tagFullCData(final StringBuffer aBuffer,
                                          final String aName,
                                          final ListOfString aAttributes,
                                          final String aData,
                                          final String aCData,
                                          final int aIndentSize,
                                          final boolean aAppendNewLine) {
    tagStart(aBuffer, aName, aAttributes, aIndentSize, false);
    aBuffer.append(StringUtility.defaultString(aData));
    aBuffer.append(StringUtility.NEW_LINE);
    tagCData(aBuffer, aCData, aIndentSize==0? 0: aIndentSize+2);
    tagEnd(aBuffer, aName, aIndentSize, aAppendNewLine);
    return aBuffer;
  }  // tagFull

  public static StringBuffer tagFullCData(final StringBuffer aBuffer,
                                          final String aName,
                                          final String aCData,
                                          final int aIndentSize) {
    return tagFullCData(aBuffer, aName, null, null, aCData, aIndentSize, true);
  }


/**
 * Replace the five <i>problem</i> characters for XML with the appropriate
 * entity reference. Based on the value of the second parameter, the characters
 * (eg. '&') will be replaced with the string entity reference (&amp;) or the numeric
 * entity reference (&#038;). This method is null-safe. If passed null in the
 * first parameter, it will return null.
 * <br>author Doug Estep, refactored by Abdul Habra.
 * @param aXml The string to be modified.
 * @param aIsUseStr boolean indicating whether string literals or numeric
 *        literals should be substitued. TRUE if strings are to be used
 *        (i.e. "&" is replaced with "&amp;") or FALSE if the string equivalent
 *        is to be used (i.e. "&" is replaced with "&#038;")
 * @return if the string is null, empty, or blank, return the same string, otherwise
 *    return the encoded string.
 */
  public static String replaceXMLEntities(final String aXml, boolean aIsUseStr) {
    if (StringUtility.isBlank(aXml)) return aXml;

    String amp, dq, lt, gt, ap;
    if (aIsUseStr) {
      amp= AMP_STR;
      dq= DOUBLEQUOTE_STR;
      lt= LT_STR;
      gt= GT_STR;
      ap= APOS_STR;
    } else {
      amp= AMP_NUM;
      dq= DOUBLEQUOTE_NUM;
      lt= LT_NUM;
      gt= GT_NUM;
      ap= APOS_NUM;
    }
    int n= aXml.length();
    StringBuffer b= new StringBuffer( Math.min(16, n) );
    for (int i=0; i<n; i++) {
      char c= aXml.charAt(i);
      if (c=='&') b.append(amp);
      else if (c=='"') b.append(dq);
      else if (c=='<') b.append(lt);
      else if (c=='>') b.append(gt);
      else if (c=='\'') b.append(ap);
      else b.append(c);
    }
    return b.toString();
  }  // replaceXMLEntities

}  // XmlUtil
