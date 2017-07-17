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

package com.tek271.util.parse;

import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
 This is an <b>abstract</b> class that is implemented by the <code>Tokenizer</code> class.
<ol>
  <li>Has a default package scope, i.e. not visible outside this package. </li>
  <li>Defines the public properties, methods, and fields of the <code>Tokenizer</code> class.</li>
</ol>
<p>The class defines the following public methods:</p>
<ol>
   <li><code>set/get Text</code>: The text or source to be tokenized.</li>
   <li><code>getNext</code>: abstract, implemented in the
       <code>Tokenizer</code> class. Returns the next token.</li>
   <li><code>getList</code>: Tokenize the source and return a list of Token objects.</li>
   <li><code>toString</code>: Converts the class into a string including its properties values.</li>
   <li><code>set/is CaseSensitive</code>: Controls if keywords are case sensitive or not.</li>
   <li><code>set/get Keywords</code>: A list of the keywords.
       E.g. <code>if else class while</code>.</li>
   <li><code>set/get MultiLineCommentEnders</code>: A list of character
       sequences that indicate <i>end</i> of a multi-line comment.
       E.g. <code>&#42;/</code> or <code>*)</code>.</li>
   <li><code>set/get MultiLineCommentStarters</code>:
       A list of character sequences that indicate <i>start</i> of a multi-line comment.
       E.g. <code>/*</code> or <code>(*</code>.</li>
   <li><code>set/get NewLineMarkers</code>: A list of character
       sequences that indicate the end of a line and the start of a new line.
       E.g. <code>\n</code> or <code>\n\r</code>.</li>
   <li><code>set/get Separators</code>: A list of arithmetic or
       punctuation separators.
       E.g. <code>= + - * == &lt;= ; : .</code></li>
   <li><code>set/get SingleLineCommentMarkers</code>: A list of
       character sequences that indicate the start of a single line comment. The comment
       may start at any position in the line.
       E.g. <code>//</code> or <code>REM</code>.</li>
   <li><code>set/get StringMarkers</code>: A list of characters that
       indicate the beginning/end of a string literal.
       E.g. <code>&quot;</code> or <code>'</code>.</li>
   <li><code>set/get WhiteSpaces</code>: A list of characters that
       indicate a space. E.g. blank or tab.</li>
</ol>

<p>The class also defines the following public boolean fields:</p>
<ol>
  <li><code>isReturnComments</code>: Controls if comments should
     be returned as a result of tokenization. Default is true.</li>
  <li><code>isReturnNewLineMarkers</code>: Controls if
     new-line-markers should be returned as a result of tokenization. Default is true.</li>
  <li><code>isReturnWhiteSpace</code>: Controls if white-spaces
     should be returned as a result of tokenization. Default is true.</li>
</ol>
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @version 0.11
 * <ul>
 * <li>2005.02.07: Fixed toString(). Used to fail when syntax elements lists are null.
 * </ol>
 * @author Abdul Habra
 */
abstract class AbstractTokenizer {
  public boolean isReturnWhiteSpace= true;
  public boolean isReturnNewLineMarkers= true;
  public boolean isReturnComments= true;

/** A list of possible keywords to tokenize */
  protected ListOfString pKeywords;
/** String markers, e.g. " or ' */
  protected ListOfString pStringMarkers;
/** Separators, e.g. , ; { } ( ) = := */
  protected ListOfString pSeparators;
/** Single char each. e.g. space, tab, ... */
  protected ListOfString pWhiteSpaces;
/** Markers that start single line comments e.g. // or ' */
  protected ListOfString pSingleLineCommentMarkers;
/** Markers that start multi line comments e.g. /* or (* */
  protected ListOfString pMultiLineCommentStarters;
/** Markers that end multi line comments e.g. *\/ or *) */
  protected ListOfString pMultiLineCommentEnders;
/** New-line markers e.g. CR or CR+LF */
  protected ListOfString pNewLineMarkers;

/** Are keywords case sensetive. */
  protected boolean pIsCaseSensitive = true;

/** The max length for any none-literal term, e.g. max len for any keyword */
  protected int pMaxLengthOfNotLiteral = 1;

/** A buffer that contains the text to be parsed */
  protected char[] pText;
/** Points to the chracter to be parsed next. */
  protected int pPointer;
/** The current line number of the parsed token. */
  protected int pLineNumber;
/** The current column number (in a line) of the parsed token. */
  protected int pColumn;

  protected final static String pERR_EOL_BEFORE_EOS = "End of line found before end of string";
  protected final static String pERR_EOT = "Unexpected end of text";
  protected final static String pERR_NO_EOC = "End of comment is not found";


  public boolean isCaseSensitive() {
    return pIsCaseSensitive;
  }

  public void setCaseSensitive(final boolean aIsCaseSensitive) {
    pIsCaseSensitive= aIsCaseSensitive;
    if (pKeywords!= null) pKeywords.isCaseSensitive= pIsCaseSensitive;
  }

  private void setMaxLengthOfNotLiteral(final ListOfString aList, final boolean aTrim) {
    int len= aList.getLongestItem(aTrim).length();
    if (len>pMaxLengthOfNotLiteral) pMaxLengthOfNotLiteral=len;
  }  // setMaxLengthOfNotLiteral();

  public ListOfString getKeywords() {
    return pKeywords;
  }

  public void setKeywords(final ListOfString aKeywords) {
    pKeywords = aKeywords;
    pKeywords.trim();
    pKeywords.isCaseSensitive= pIsCaseSensitive;
    setMaxLengthOfNotLiteral(pKeywords, false);
  }  // setKeywords();

  public ListOfString getStringMarkers() {
    return pStringMarkers;
  }

  public void setStringMarkers(final ListOfString aStringMarkers) {
    pStringMarkers = aStringMarkers;
    pStringMarkers.trim();
    setMaxLengthOfNotLiteral(pStringMarkers, false);
  }

  public ListOfString getSeparators() {
    return pSeparators;
  }

  public void setSeparators(final ListOfString aSeparators) {
    pSeparators = aSeparators;
    pSeparators.trim();
    setMaxLengthOfNotLiteral(pSeparators, false);
  }

  public ListOfString getWhiteSpaces() {
    return pWhiteSpaces;
  }

  public void setWhiteSpaces(final ListOfString aWhiteSpaces) {
    pWhiteSpaces = aWhiteSpaces;
  }

  public ListOfString getSingleLineCommentMarkers() {
    return pSingleLineCommentMarkers;
  }

  public void setSingleLineCommentMarkers(final ListOfString aSingleLineCommentMarkers) {
    pSingleLineCommentMarkers = aSingleLineCommentMarkers;
    pSingleLineCommentMarkers.trim();
    setMaxLengthOfNotLiteral(pSingleLineCommentMarkers, false);
  }

  public ListOfString getMultiLineCommentStarters() {
    return pMultiLineCommentStarters;
  }

  public void setMultiLineCommentStarters(final ListOfString aMultiLineCommentStarters) {
    pMultiLineCommentStarters = aMultiLineCommentStarters;
    pMultiLineCommentStarters.trim();
    setMaxLengthOfNotLiteral(pMultiLineCommentStarters, false);
  }

  public ListOfString getMultiLineCommentEnders() {
    return pMultiLineCommentEnders;
  }

  public void setMultiLineCommentEnders(final ListOfString aMultiLineCommentEnders) {
    pMultiLineCommentEnders = aMultiLineCommentEnders;
    pMultiLineCommentEnders.trim();
    setMaxLengthOfNotLiteral(pMultiLineCommentEnders, false);
  }

  public ListOfString getNewLineMarkers() {
    return pNewLineMarkers;
  }

  public void setNewLineMarkers(final ListOfString aNewLineMarkers) {
    pNewLineMarkers = aNewLineMarkers;
  }

  public String getText() {
    return String.valueOf(pText);
  }

/** Set the text to be tokenized. */
  public void setText(final String aText) {
    pText = aText.toCharArray();
    pPointer = 0;
    pLineNumber = 1;
    pColumn = 1;
  } // setText

  public Token.List getList() {
    Token.List r= new Token.List();
    while (true) {
      Token t= getNext();
      if (t==null) break;
      r.add(t);
    }
    return r;
  } // getList()

/** Get next token, returns null if no more. */
  abstract public Token getNext();

  private String listToString(final ListOfString aList, final String aSeparator) {
    String s;
    if (aList==null) s= StringUtility.NEW_LINE;
    else s= aList.getText(aSeparator) + StringUtility.NEW_LINE;
    return s;
  }  // listToString()

  public String toString(final String aSeparator) {
    StringBuffer r= new StringBuffer(128);
    r.append("isCaseSensitive=").append(pIsCaseSensitive).append(StringUtility.NEW_LINE);
    r.append("Keywords=").append(listToString(pKeywords, aSeparator));
    r.append("StringMarkers=").append(listToString(pStringMarkers, aSeparator));
    r.append("Separators=").append(listToString(pSeparators, aSeparator));
    r.append("WhiteSpaces=").append(listToString(pWhiteSpaces, aSeparator));
    r.append("SingleLineCommentMarkers=").append(listToString(pSingleLineCommentMarkers, aSeparator));
    r.append("MultiLineCommentStarters=").append(listToString(pMultiLineCommentStarters, aSeparator));
    r.append("MultiLineCommentEnders=").append(listToString(pMultiLineCommentEnders, aSeparator));
    r.append("NewLineMarkers=").append(listToString(pNewLineMarkers, aSeparator));
    return r.toString();
  }  // toString()

  public String toString() {
    return toString(", ");
  }

} // AbstractTokenizer
