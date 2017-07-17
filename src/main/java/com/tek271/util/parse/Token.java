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

import java.util.*;
import com.tek271.util.string.StringUtility;

/** The <code>Tokenizer</code> class produces Token objects.
 * This is a simple data structure that contains the value, type, line, and column
 * of the token. The token type can be one of:
 * Keyword, String, Separator, Literal, White Space, Comment, New Line, and Error.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Token {
  /** Token types */
  public static final int TYPE_MIN_VALUE  = 1;
  public static final int TYPE_KEYWORD    = 1;
  public static final int TYPE_STRING     = 2;
  public static final int TYPE_SEPARATOR  = 3;
  public static final int TYPE_LITERAL    = 4;
  public static final int TYPE_SPACE      = 5;
  public static final int TYPE_COMMENT    = 6;
  public static final int TYPE_NEWLINE    = 7;
  public static final int TYPE_ERROR      = 8;
  public static final int TYPE_MAX_VALUE  = 8;

/** This is used for error message and testing purposes. */
  private static final String[] pNAMES = new String[] {
                 "Keyword",    "String",  "Separator", "Literal",
                 "WhiteSpace", "Comment", "NewLine",   "Error"   };

/** The value of a parsed token. */
  public String value= StringUtility.EMPTY;
/** The type of the token. It can be on of the predefined constatns TYPE_* */
  public int type;
/** The line where the last character of the token is located */
  public int line;
/** The column (in a line) where the last character of the token is located */
  public int column;

/** Token objects are created by classes in this package only (and subclasses) */
  Token() {}

  void clear() {
    value= StringUtility.EMPTY;
    type=0;
    line=0;
    column=0;
  }

/** The token type as a string */
  public String getTypeAsString() {
    if (type < TYPE_MIN_VALUE || type > TYPE_MAX_VALUE) return StringUtility.EMPTY;
    return pNAMES[type-1];
  }

/** Set the value and type of the token. */
  void set(final String aValue, final int aType) {
    value = aValue;
    type = aType;
  } // setAll

/** Set the value, type, line, and column. */
  void set(final String aValue, final int aType, final int aLine, final int aColumn) {
    value = aValue;
    type = aType;
    line = aLine;
    column = aColumn;
  } // setAll

/** get the value, if aIsUnicode, unescape the value first */
  public String getValue(final boolean aIsEncode) {
    if (! aIsEncode) return value;
    if (type==TYPE_NEWLINE) return StringUtility.toUnicode(value);
    return value;
  }

/** get the token's value */
  public String getValue() {
    return getValue(true);
  }

/** get length of the token's value */
  int lengthOfValue() {
    if (value==null) return 0;
    return value.length();
  }

/** Convert the token to a string */
  public String toString() {
    return
    "[" + StringUtility.rightPad( String.valueOf(line), 2) +
    ", " + StringUtility.rightPad(String.valueOf(column), 3) +
    "] " + StringUtility.leftPad( getTypeAsString(), 10) +
    ", Length=" + StringUtility.rightPad( String.valueOf(lengthOfValue()), 2) +
    ", Value=" + getValue();
  }  // toString()

/** Define a list of tokens. */
  public static class List {
    private java.util.List pList= new ArrayList();

    List () {}

    public int size() {
      return pList.size();
    }

    void add(final Token aToken) {
      pList.add(aToken);
    }

    public Token get(final int aIndex) {
      return (Token) pList.get(aIndex);
    }

    public java.util.List getList() {
      return pList;
    }

    public String toString() {
      StringBuffer b= new StringBuffer(128);
      for (int i=0, n=size(); i<n; i++) {
        b.append(i+1).append(": ");
        b.append( get(i).toString() ).append(StringUtility.NEW_LINE);
      }
      return b.toString();
    }
  }  // List

} // Token
