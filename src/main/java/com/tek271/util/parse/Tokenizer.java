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
import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.string.StringUtility;

/**
 A generic tokenizer. To use it:<ol>
<li>Initialize the syntax definition by calling setKeyworks, setCaseSensitive, setSeparators, ...</li>
<li>Call setText() and pass it the text you want to tokenize.</li>
<li>Call getNext() repeatedly. Each call will return an object of type
   <code>Token</code>. When no more tokens, a <b>null</b> is returned.</li>
<li>You can call getList() instead of calling getNext() many times.</li>
</ol>
* @version 0.11
* <ul>
* <li>2005.02.07: Fixed getNewLineMarker(). Used to fail when source ends with 2 or more
* empty lines.
* </ol>
* @author Abdul Habra
*/
public class Tokenizer extends AbstractTokenizer {


/** Get next token, returns null if no more. */
  public Token getNext() {
    if (pText == null) return null;
    if (pPointer >= pText.length) return null;
    Token tk = new Token();

    // loop until a token is found
    while (pPointer < pText.length) {
      tk.set(tk.value + pText[pPointer++], tk.type, pLineNumber, pColumn++);

      if (pNewLineMarkers.contains(tk.value)) {
        if (getNewLineMarker(tk))   return tk;
        continue;
      }

      if (pWhiteSpaces.contains(tk.value)) {
        if (getWhiteSpace(tk))   return tk;
        continue;
      }

      if (pSingleLineCommentMarkers.contains(tk.value)) {
        if (getSingleLineComment(tk)) return tk;
        continue;
      }

      if (pMultiLineCommentStarters.contains(tk.value)) {
        if (getMultiLineComment(tk)) return tk;
        continue;
      }

      if (pStringMarkers.contains(tk.value)) {
        getString(tk);
        return tk;
      }

      if (pSeparators.contains(tk.value)) {
        if (isComment(tk)) continue;  // remove ambiguity:  / separator, // comment !!!
        // find the longest separator that matchs the chars at the current position
        getLongestMatchingSeparator(tk);
        return tk;
      }

      // if the following chars are one of the none-literal items
      // then tk.value is a literal or keyword.
      // NOTE: keep this the last check in the loop !!!
      if (! isNextLiteral()) {
        if (pKeywords.isExist(tk.value))    tk.type= Token.TYPE_KEYWORD;
        else tk.type = Token.TYPE_LITERAL;
        return tk;
      }
    } // while
    return null;
  } // getNext

/** Find the longest separator that matchs the chars at the current position */
  private void getLongestMatchingSeparator(final Token aToken) {
    aToken.type= Token.TYPE_SEPARATOR;
    int vlen= aToken.lengthOfValue();
    String v= getNextString(pMaxLengthOfNotLiteral - vlen);
    v= aToken.value + v;
    ListOfString list= new ListOfString();
    for (int i=0, n=pSeparators.size(); i<n; i++) {
      String sp= pSeparators.getItem(i);
      if (sp.length()<=vlen) continue;
      if (v.startsWith(sp)) list.add(sp);
    }
    v= list.getLongestItem(false);
    if (v==null) return;
    if (v.length()<= vlen) return;
    int d= v.length() - vlen;
    pPointer+= d;
    pColumn+=d;
    aToken.set(v, Token.TYPE_SEPARATOR, pLineNumber, pColumn);
  }  // getLongestMatchingSeparator

/** get the string from pText starting at pPointer */
  private String getNextString(int aSize) {
    if (pPointer >= pText.length) return StringUtility.EMPTY;
    aSize= Math.min(aSize, pText.length-pPointer );
    return new String(pText, pPointer, aSize);
  }

  private boolean getNewLineMarker(final Token aToken) {
    if (pPointer >= pText.length) {
      aToken.set(aToken.value, Token.TYPE_NEWLINE, pLineNumber++, aToken.column );
    } else {
      // check for a line marker that is TWO chars long
      String v= aToken.value + pText[pPointer];
      if (pNewLineMarkers.contains(v)) {
        pPointer++;
        aToken.set(v, Token.TYPE_NEWLINE, pLineNumber++, pColumn++);
      } else {
          aToken.set(aToken.value, Token.TYPE_NEWLINE, pLineNumber++, aToken.column );
      }
    }

    pColumn = 1;
    if (!isReturnNewLineMarkers) aToken.clear();
    return isReturnNewLineMarkers;
  }  // getNewLineMarker


/**
* Get characters until end of string is found.
* When this method is started, mPointer is at the first char in the string.
*/
  private void getString(final Token aToken) {
    char ch;
    String endMarker = aToken.value;

    while (true) {
      if (pPointer >= pText.length) {
        aToken.set(pERR_EOT, Token.TYPE_ERROR);
        return;
      }

      ch = pText[pPointer++];
      aToken.value += ch;
      aToken.column = pColumn++;
      if (aToken.value.endsWith(endMarker)) break;
      // is there an end-of-line before the end of the string?
      int isf= pNewLineMarkers.indexOfSuffixItem(aToken.value);
      if (isf >= 0) {
        aToken.set(pERR_EOL_BEFORE_EOS , Token.TYPE_ERROR);
        pLineNumber++;
        pColumn=0;
        pPointer++;
        return;
      }
    } // while
    aToken.type = Token.TYPE_STRING;
  } // getString

/**
* Read chars until the first none space is found.
* mPointer is at the character following the first White Space.
*/
  private boolean getWhiteSpace(final Token aToken) {
    char ch;
    aToken.type = Token.TYPE_SPACE;
    // loop until a no-space is found
    while (true) {
      if (pPointer >= pText.length) break;
      ch = pText[pPointer];
      if (! pWhiteSpaces.contains(String.valueOf(ch)) ) break;
      pPointer++;
      aToken.value += ch;
      aToken.column = pColumn++;
    }
    if (!isReturnWhiteSpace) aToken.clear();
    return isReturnWhiteSpace;
  } // getWhiteSpace

/**
* For the string starting at mText[mPointer], check if this string starts
* with a string marker, separator, white-space, comment, or new-line.
* @return true if the following is literal (i.e. not one of the above options)
*/
  private boolean isNextLiteral() {
    // convert the start of the array to a string
    int length = Math.min(pMaxLengthOfNotLiteral, pText.length-pPointer);
    if (length <= 0) return false;
    String st= getNextString(length);

    if (pStringMarkers.indexOfPrefixItem(st) >= 0) return false;
    if (pSeparators.indexOfPrefixItem(st) >= 0) return false;
    if (pWhiteSpaces.indexOfPrefixItem(st) >= 0) return false;
    if (pSingleLineCommentMarkers.indexOfPrefixItem(st) >= 0) return false;
    if (pMultiLineCommentStarters.indexOfPrefixItem(st) >= 0) return false;
    if (pNewLineMarkers.indexOfPrefixItem(st) >= 0) return false;
    return true;
  } // isNextLiteral


/**
* read comment chars until end of line is found.
* mPointer is at the 1'st comment character.
*/
  private boolean getSingleLineComment(final Token aToken) {
    int[] listIndex = {0};
    // find the index of the eol starting at mPointer
    int eolIndex = ArrayUtilities.indexOf(pText,
                                          pNewLineMarkers,
                                          pPointer,
                                          listIndex);
    String commentText;
    if (eolIndex == -1) // end of text found before eol
      commentText = getNextString(pText.length - pPointer);
    else
      commentText = getNextString(eolIndex - pPointer);

    pPointer += commentText.length();
    pColumn += commentText.length();
    aToken.set(aToken.value + commentText, Token.TYPE_COMMENT, aToken.line, pColumn-1);
    if (!isReturnComments) aToken.clear();
    return isReturnComments;
  }  // getSingleLineComment


/**
* Read chars until end-of-comment marker is found.
* mPointer is at the 1'st comment character.
*/
  private boolean getMultiLineComment(final Token aToken) {
    aToken.type = Token.TYPE_COMMENT;
    int[] listIndex = {0};
    int commentEndIndex = ArrayUtilities.indexOf(pText,
                                                 pMultiLineCommentEnders,
                                                 pPointer,
                                                 listIndex);

    if (commentEndIndex == -1) {
      aToken.set(pERR_NO_EOC, Token.TYPE_ERROR, aToken.line, pColumn++);
      pPointer = pText.length;
      return true;
    }
    // extract the comment including the comment-end marker.
    int endMarkerLen = ((String)pMultiLineCommentEnders.get(listIndex[0])).length();
    aToken.value += getNextString(commentEndIndex - pPointer + endMarkerLen);
    pPointer = commentEndIndex + endMarkerLen;

    // find the line #
    pLineNumber += StringUtility.countMatches(aToken.value, pNewLineMarkers);
    aToken.line = pLineNumber;

    // find the column #
    int lastNewLine = StringUtility.lastIndexOf(aToken.value, pNewLineMarkers, listIndex);
    if (lastNewLine == -1)
      pColumn = aToken.lengthOfValue();
    else {
      int eolMarkerLen = ( (String)pNewLineMarkers.get(listIndex[0])).length();
      pColumn = aToken.lengthOfValue() - lastNewLine - eolMarkerLen;
    }
    aToken.column = pColumn++;
    if (!isReturnComments) aToken.clear();
    return isReturnComments;
  } // getMultiLineComment

/**
* Check if aToken.value is the beginning of any comment marker.
*/
  private boolean isComment(final Token aToken) {
    // make the string of the token and several following chars
    String v= aToken.value;
    String st= v + getNextString(pMaxLengthOfNotLiteral-v.length());
    int tl= aToken.lengthOfValue();
    String sv= getValueInListAndLongerThanToken(st, pSingleLineCommentMarkers, tl);
    String mv= getValueInListAndLongerThanToken(st, pMultiLineCommentStarters, tl);
    if (sv==null && mv==null) return false;

    return true;
  } // isComment

/**
* Check if aValue is prefixed with any item in aList. If it is, check if
* the length of the item is more than the length aToken.value.
* @return If found and longer than token, the index in the list, else -1.
*/
  private int indexOfValueInListAndLongerThanToken(final String aValue ,
                                                   final ListOfString aList,
                                                   final int aTokenLength) {
    int index= aList.indexOfPrefixItem(aValue);
    if (index == -1) return -1;
    if (aList.getItem(index).length() > aTokenLength) return index;
    return -1;
  } // valueInListAndLongerThanToken

  private String getValueInListAndLongerThanToken(final String aValue ,
                                                  final ListOfString aList,
                                                  final int aTokenLength) {
    int i= indexOfValueInListAndLongerThanToken(aValue, aList, aTokenLength);
    if (i==-1) return null;
    return aList.getItem(i);
  }  // getValueInListAndLongerThanToken()

} // Tokenizer
