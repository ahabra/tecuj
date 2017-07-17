/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2005  Abdul Habra.
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

package com.tek271.util.string;

import java.util.*;
import com.tek271.util.collections.list.*;

/**
 * A text template class that allows substituting patterns (or tags) with values.
 * The easiest way to use it is to call one of the process() static methods.<br>
 * However, if you want to reuse the same template in your program but with different
 * values, consider creating a TextTemplate object and assigning values to it using
 * either setTagValue() or setAll().
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * <p><b>History:</b></p><ol>
 * <li>2005.06.22: Added copy() method.</li>
 * <li>2006.01.30: Added ITagTransformer and transformTags()
 * </ol>
 * @author Abdul Habra
 * @version 1.2
 */
public class TextTemplate {

/** Allows transforming tags to new values */
  public interface ITagTransformer {
    /** Check if aTag matches a tag in the template. */
    boolean isMatch(String aTag);

    /** Transform the value of a tag to a new value. */
    String transform(String aTag);
  }

/** Case sensetivity for tag searches. Tag start/end are always case sensetive. Default=true */
  public boolean isCaseSensitive = true;

  private static final String pTAG_START= "${";
  private static final String pTAG_END= "}";

  private String pTagStart;
  private String pTagEnd;
  private int pTagStartLen;
  private int pTagEndLen;

  private String pText;    // original text to be parsed
  private int pTextLen;
  private int pCurrentIndex;

  private List pList;      // of Element items
  protected List pTagList;   // references to Element items

  private TextTemplate() {}

/**
 * Create a TextTemplate object
 * @param aText String The text that contains tags to be replaced
 * @param aTagStart String The marker for the start of a tag, e.g. {
 * @param aTagEnd String The marker for the end of a tag, e.g. }. If this end marker
 * is null or zero-length then the first white-space character will mark the end of
 * the tag.
 */
  public TextTemplate(final String aText,
                      final String aTagStart,
                      final String aTagEnd) {
    pTagStart= aTagStart;
    pTagStartLen= StringUtility.length(pTagStart);
    pTagEnd= StringUtility.defaultString(aTagEnd);
    pTagEndLen= pTagEnd.length();
    setText(aText);
  }

  public void setText(final String aText) {
    pCurrentIndex= 0;
    pText= StringUtility.defaultString(aText);
    pTextLen= StringUtility.length(aText);
    parseToList();
  }

/**
 * Create a TextTemplate object with a start tag marker and white-space character end
 * marker.
 * @param aText String The text that contains tags to be replaced
 * @param aTagStart String The marker for the start of a tag, e.g. #. Note that there is
 * no end-marker which means the tag ends when the first white-space character is found.
 */
  public TextTemplate(final String aText,
                      final String aTagStart) {
    this(aText, aTagStart, null);
  }

/**
 * Create a TextTemplate object with a start tag marker ${ and tag end marker }
 * @param aText String The text that contains tags to be replaced.
 */
  public TextTemplate(final String aText) {
    this(aText, pTAG_START, pTAG_END);
  }

/**
 * Process the given text and replace its tags from the given aValues.
 * @param aText String The text that contains tags to be replaced
 * @param aTagStart String The marker for the start of a tag, e.g. {
 * @param aTagEnd String The marker for the end of a tag, e.g. }. If this end marker
 * is null or zero-length then the first white-space character will mark the end of
 * the tag.
 * @param aValues ListOfString Contains name=value pairs that will be replaced in the
 * given text. The name will match a tag whose value will be substituted.
 * @return String A new string with tags replaced from aValues.
 */
  public static String process(final String aText,
                               final String aTagStart,
                               final String aTagEnd,
                               final ListOfString aValues) {
    TextTemplate tt= new TextTemplate(aText, aTagStart, aTagEnd);
    tt.setAll(aValues);
    return tt.getText();
  }

/**
 * Process the given text and replace its tags from the given aValues.
 * @param aText String The text that contains tags to be replaced
 * @param aTagStart String The marker for the start of a tag, e.g. {
 * @param aTagEnd String The marker for the end of a tag, e.g. }. If this end marker
 * is null or zero-length then the first white-space character will mark the end of
 * the tag.
 * @param aValues String[][] aValues[i][0] is the tag name, while  aValues[i][1] is
 * the tag value.
 * @return String A new string with tags replaced from aValues.
 */
  public static String process(final String aText,
                               final String aTagStart,
                               final String aTagEnd,
                               final String[][] aValues) {
    TextTemplate tt= new TextTemplate(aText, aTagStart, aTagEnd);
    tt.setAll(aValues);
    return tt.getText();
  }

/**
 * Process the given text and replace its tags from the given aValues.
 * @param aText String The text that contains tags to be replaced
 * @param aTagStart String The marker for the start of a tag, e.g. {
 * @param aTagEnd String The marker for the end of a tag, e.g. }. If this end marker
 * is null or zero-length then the first white-space character will mark the end of
 * the tag.
 * @param aValues Map The key in the map is the tag name, while the value in the
 * map is the tag value.
 * @return String A new string with tags replaced from aValues.
 */
  public static String process(final String aText,
                               final String aTagStart,
                               final String aTagEnd,
                               final Map aValues) {
    TextTemplate tt= new TextTemplate(aText, aTagStart, aTagEnd);
    tt.setAll(aValues);
    return tt.getText();
  }

/** Get the string that marks the start of a tag */
  public String getTagStart() {
    return pTagStart;
  }

/** Get the string that marks the end of a tag */
  public String getTagEnd() {
    return pTagEnd;
  }

/**
 * Represents a single element of the processed text, the element can be either
 * a string or a tag.
 */
  public class Element {
    public static final int TEXT = 1;
    public static final int TAG = 2;

    public int type;          // TEXT or TAG
    public String text;       // this is either the text or the tag
    public String tagValue;   // defined only if type=TAG

    public Element copy() {
      Element r= new Element();
      r.type= type;
      r.text= text;
      r.tagValue= tagValue;
      return r;
    }

/** Create an elelemnt extracted from the text at pCurrentIndex */
    Element() {
      parseNext();
    }

/** Get the value of the elelemnt */
    public String getValue() {
      if (type==TAG) return tagValue;
      return text;
    }

    public boolean isTag() {
      return type==TAG;
    }

/** Get the tag without the start/end markers */
    public String getTagName() {
      if (type != TAG) return StringUtility.EMPTY;
      return StringUtility.substring(text, pTagStartLen, text.length() - pTagEndLen);
    }

/** Parse starting at the pCurrentIndex and advance pCurrentIndex */
    void parseNext() {
      int i= pText.indexOf(pTagStart, pCurrentIndex);  // index of next start marker
      if (i<0) {               // last text
        type= TEXT;
        text= pText.substring(pCurrentIndex);
        pCurrentIndex= pTextLen;
      } else if (i==pCurrentIndex) {  //  tag here
        type= TAG;
        text= extractTag();   // Extract tag and advance pCurrentIndex
        tagValue= text;
      } else {                // text here, followed by a tag
        type= TEXT;
        text= pText.substring(pCurrentIndex, i);
        pCurrentIndex= i;
      }
    }  // parseNext

/** Get a debug info of this element */
    public String toString() {
      if (type==TEXT) {
        return "[TEXT=" + text + "]";
      }
      
      return "[TAG=" + text + "] [VALUE=" + StringUtility.defaultString(tagValue) + "]";
    }  // toString

  }  // element class


/** Parse pText into a list of Element objects */
  private void parseToList() {
    pList= new ArrayList();
    pTagList= new ArrayList();

    if (StringUtility.isBlank(pText)) return;

    while (pCurrentIndex < pTextLen) {
      Element e= new Element();  // will parse text and advance pCurrentIndex
      pList.add(e);
      if (e.isTag()) pTagList.add(e);
    }
  }  // parseToList

  private static final String pTAG_ENDERS= StringUtility.WHITE_SPACE + "'\"?<()=,;";

/** Find the index of the next closing tag */
  private int indexOfEndTag(final int aIndex) {
    if (StringUtility.isEmpty(pTagEnd)) {
      return StringUtility.indexOfAny(pText, pTAG_ENDERS, aIndex);
    } 
    return pText.indexOf(pTagEnd, aIndex);
  }  // indexOfEndTag

/** Extract tag and advance the pCurrentIndex */
  private String extractTag() {
    int end= indexOfEndTag(pCurrentIndex+1);
    String r;
    if (end<0) {  // no end tag, finish
      r=pText.substring(pCurrentIndex);
      pCurrentIndex= pTextLen;
    } else {
      r=pText.substring(pCurrentIndex, end+pTagEndLen);
      pCurrentIndex= end + pTagEndLen;
    }
    return r;
  }  // extractTag

/** Number of tags in the given text */
  public int getTagCount() {
    return pTagList.size();
  }

/** get the tag at the given index */
  private Element getTag(final int aIndex) {
    return (Element) pTagList.get(aIndex);
  }

/**
 * Get the index of the given tag starting at aStartIndex. This index is the sequential
 * index of the tag and not its character position in the text. For example, if this
 * method returns 0, it means that this is the first tag in the text, but not necessarily
 * the first chaqracter position in the text.
 * @param aTag String The tag to be found without the start/end markers.
 * @param aStartIndex int starting index, again this is the order of the tag and not its
 * character position in the text.
 * @return int The index of the tag if found starting at 0. -1 if not found.
 */
  public int indexOfTag(final String aTag, final int aStartIndex) {
    String tag= pTagStart + aTag + pTagEnd;
    for (int i=aStartIndex, n=pTagList.size(); i<n; i++) {
      Element e= getTag(i);
      if ( StringUtility.equals(e.text, tag, isCaseSensitive) ) return i;
    }
    return -1;
  }  // indexOfTag

/**
 * Get the index of the given tag starting at aStartIndex. This index is the sequential
 * index of the tag and not its character position in the text. For example, if this
 * method returns 0, it means that this is the first tag in the text, but not necessarily
 * the first chaqracter position in the text.
 * @param aTag String The tag to be found without the start/end markers.
 * @return int The index of the tag if found starting at 0. -1 if not found.
 */
  public int indexOfTag(final String aTag) {
    return indexOfTag(aTag, 0);
  }

/** set the value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final String aValue) {
    String tag= pTagStart + aTag + pTagEnd;
    for (int i=0, n=pTagList.size(); i<n; i++) {
      Element e= getTag(i);
      if ( StringUtility.equals(e.text, tag, isCaseSensitive) ) {
        e.tagValue= aValue;
      }
    }
  }  // setTagValue

/** Set the value of the tag at the given index */
  public void setTagValue(final int aTagIndex,
                          final String aValue) {
    Element e= getTag(aTagIndex);
    e.tagValue= aValue;
  }

/** set the boolean value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final boolean aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }

/** set the char value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final char aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }

/** set the int value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final int aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }

/** set the long value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final long aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }

/** set the float value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final float aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }

/** set the double value of the given tag, if the tag appears multiple times, all are set */
  public void setTagValue(final String aTag,
                          final double aValue) {
    setTagValue(aTag, String.valueOf(aValue));
  }



/** Get the value of the tag at the given index */
  public String getTagValue(final int aTagIndex) {
    Element e= getTag(aTagIndex);
    return e.tagValue;
  }

/**
 * Get the value of the given tag, starting search at the given index.
 * @param aTag String The tag which we want its value.
 * @param aStartIndex int Look for the tag starting at this index.
 * @return String The value of the tag, null if not found.
 */
  public String getTagValue(final String aTag,
                            final int aStartIndex) {
    int i= indexOfTag(aTag, aStartIndex);
    if (i<0) return null;
    return getTagValue(i);
  }

/**
 * Get the value of the given tag.
 * @param aTag String The tag which we want its value.
 * @return String The value of the tag, null if not found.
 */
  public String getTagValue(final String aTag) {
    return getTagValue(aTag, 0);
  }

/** Set the contents of the given name=value list to the tags of this object */
  public void setAll(final ListOfString aValues) {
    for (int i=0, n=aValues.size(); i<n; i++) {
      if (! aValues.isNameValuePair(i)) continue;
      String name= aValues.getNameAtIndex(i).trim();
      String val= aValues.getValueAtIndex(i);
      setTagValue(name, val);
    }
  }  // setAll

/**
 * Set the contents of a two-dimentional array to the tags of this object
 * @param aValues String[][] The second dimention must have two elements the first is
 * the tag name, and the second is the tag value. e.g. aValue[i][0] is a tag, while
 * aValue[i][1] is a value.
 */
  public void setAll(final String[][] aValues) {
    for (int i=0, n=aValues.length; i<n; i++) {
      String name= aValues[i][0];
      String val= aValues[i][1];
      setTagValue(name, val);
    }
  }  // setAll

/**
 * set contents of the map into this object, map's key will be used for the tag, and
 * the map's value will be used for the tag's value.
 */
  public void setAll(final Map aValues) {
    Set es= aValues.entrySet();
    for (Iterator i=es.iterator(); i.hasNext(); ) {
      Map.Entry entry= (Map.Entry) i.next();
      String name= (String) entry.getKey();
      String val= (String) entry.getValue();
      setTagValue(name, val);
    }
  }  // setAll

/** Clear the values of all tags */
  public void clearValues() {
    for (int i=0, n= getTagCount(); i<n; i++) {
      Element e= getTag(i);
      e.tagValue= e.text;
    }
  }

/** Get a list of all tag names in the template */
  public ListOfString getAllTagNames() {
    int n= getTagCount();
    ListOfString r= new ListOfString(n);
    for (int i=0; i<n; i++) {
      r.add( getTag(i).getTagName() );
    }
    return r;
  }  // getAllTagNames

/** Get a list of all tag names that start with the given string */
  public ListOfString getAllTagNames(final String aStartsWith) {
    int n= getTagCount();
    ListOfString r= new ListOfString();
    for (int i=0; i<n; i++) {
      String tag= getTag(i).getTagName();
      if (StringUtility.startsWith(tag, aStartsWith, isCaseSensitive)) {
        r.add(tag);
      }
    }
    return r;
  }  // getAllTagNames


/** get the text after substituting all the tag values */
  public String getText() {
    StringBuffer b= new StringBuffer(pTextLen);
    for (int i=0, n=pList.size(); i<n; i++) {
      Element e= (Element) pList.get(i);
      b.append( e.getValue() );
    }
    return b.toString();
  }  // getText

/** Get a list of all elements in the text, useful for debugging */
  public ListOfString toListOfString() {
    int n= pList.size();
    ListOfString r= new ListOfString( n );
    for (int i=0; i<n; i++) {
      Element e= (Element) pList.get(i);
      r.add( e.toString() );
    }
    return r;
  }

/**
 * Get a string that represents the text template.
 * @param aIsDebugData boolean If true, show the details of the elements, if false show
 * the text with all tags replaced.
 * @return String
 */
  public String toString(final boolean aIsDebugData) {
    if (aIsDebugData)     return toListOfString().toString();
    return getText();
  }

/** Get the text with tags replaced */
  public String toString() {
    return toString(false);
  }

/** Get a new deep-copy of this object */
  public TextTemplate copy() {
    TextTemplate r= new TextTemplate();
    copy(this, r);
    return r;
  }  // copy

  public static void copy(final TextTemplate from, final TextTemplate to) {
    to.isCaseSensitive= from.isCaseSensitive;
    to.pTagStart= from.pTagStart;
    to.pTagEnd= from.pTagEnd;
    to.pTagStartLen= from.pTagStartLen;
    to.pTagEndLen= from.pTagEndLen;

    to.pText= from.pText;
    to.pTextLen= from.pTextLen;
    to.pCurrentIndex= from.pCurrentIndex;

    int n= from.pList.size();
    to.pList= new ArrayList(n);
    to.pTagList= new ArrayList(from.pTagList.size() + 1);
    for (int i=0; i<n; i++) {
      Element item= (Element) from.pList.get(i);
      to.pList.add(item);
      if (item.isTag()) to.pTagList.add(item);
    }
  }
  
/**
 * Transform the tag values in this template.
 * @param aTransformer ITagTransformer tags that match are transformed. i.e.
 * if aTransformer.isMatch(tag) then tagValue= aTransformer.transform(tag)
 */
  public void transformTags(final ITagTransformer aTransformer) {
    if (aTransformer==null) return;

    for (int i=0, n=pTagList.size(); i<n; i++) {
      Element e= getTag(i);
      String tag= e.getTagName();
      if (aTransformer.isMatch(tag)) {
        e.tagValue= aTransformer.transform(tag);
      }
    }
  }  // transformTags


  public static void main(String[] args) {
    class UrlTransformer implements ITagTransformer {
      public boolean isMatch(String aTag) {
        return StringUtility.startsWith(aTag, "homeUrl", true);
      }

      public String transform(String aTag) {
        return "www.tek271.com" + aTag.substring("homeUrl".length());
      }
    }

    String text= "home page is ${homeUrl/index.html?a=12&b=abdul}. Try it out.";
    TextTemplate tt= new TextTemplate(text);
    UrlTransformer ut= new UrlTransformer();
    tt.transformTags(ut);
    System.out.println(tt.getText());
  }


}  // TextTemplate
