/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra, Doug Estep.
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang.StringUtils;

import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.collections.list.ListOfString;

/**
 * Contains common string functions. Note that this class <i>extends</i>
 * <code>org.apache.commons.lang.StringUtils</code>.
 * The <code>StringUtils</code> class is available to download from
 * <a href="http://jakarta.apache.org/commons/lang/">
 * http://jakarta.apache.org/commons/lang/</a>.
 * Make sure to include the downloaded jar file in your classpath, currently this is
 * <code>commons-lang-2.1.jar</code>. However, the tecuj download includes
 * this jar file.
 * <p>Some interesting features are:</p> <ol>
 * <li>Define some common String constants like NEW_LINE, ALPHA_CAPS, WHITE_SPACE, ...</li>
 * <li>Delete a set of characters from a given string.</li>
 * <li><code>equals()</code> method with a case sensitivity flag.</li>
 * <li>Convert to/from Hexadecimals and Unicode.</li>
 * <li>Check if a value is a member in a given set of values.</li>
 * <li>Several overloaded indexOf(), indexOfAny(), indexOfAnyBut() methods to find
 *     the index of a value in a target.</li>
 * <li>Get the lastChar() of a given string.</li>
 * <li>Repeat a character a given number of times.</li>
 * <li>And more ...</li>
 * </ol>
 * <p>Copyright (c) 2003-2008 Technology Exponent</p>
 * @author Doug Estep, Abdul Habra
 * @version 1.0
 */
public class StringUtility extends StringUtils {
  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final String TAB = "\t";
  public static final String SINGLE_QUOTE = "'";
  public static final String DOUBLE_QUOTE = "\"";
  public static final String BLANK = " ";
  public static final String EQUAL = "=";
  public static final String COMMA = ",";
  public static final String DOT = ".";
  public static final String SEMI_COLON= ";";
  public static final String COLON = ":";
  public static final String QUESTION = "?";
  public static final String SLASH = "/";
  public static final String BACK_SLASH = "\\";
  public static final String LESS = "<";
  public static final String GREATER = ">";
  public static final String LPARAN = "(";
  public static final String RPARAN = ")";
  public static final String PIPE = "|";
  public static final String MINUS = "-";
  public static final String PLUS = "+";
  public static final String YES = "YES";
  public static final String NO = "NO";

  public static final String WHITE_SPACE = BLANK + TAB + NEW_LINE;

  /** simly faces */
  public static final String SML_HAPPY = ":-)";
  public static final String SML_WINK  = ";-)";
  public static final String SML_SAD   = ":-(";
  public static final String SML_CRY   = ":'-(";
  public static final String SML_DUMB  = "<:-)";
  public static final String SML_DRUNK = ":*)";
  public static final String SML_YUMM  = ":~)";

  public static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String ALPHA_SMALL= "abcdefghijklmnopqrstuvwxyz";
  public static final String DIGITS = "0123456789";
  public static final String ALPHA_NUMERIC = DIGITS + ALPHA_CAPS + ALPHA_SMALL;

  public static final String ZERO = "0";
  public static final String ONE = "1";
  public static final String TWO = "2";
  public static final String THREE = "3";
  public static final String FOUR = "4";
  public static final String FIVE = "5";
  public static final String SIX = "6";
  public static final String SEVEN = "7";
  public static final String EIGHT = "8";
  public static final String NINE = "9";
  public static final String TEN = "10";


  /*
   * No public constructor.
   */
  private StringUtility() {
  }

/**
 * Extract letters and/or numbers from the given string
 * @param aStr String to extract from.
 * @param aIsExtractAlpha extract letters?
 * @param aIsExtractNum boolean extract digits?
 * @return String
 */
  public static String extract(final String aStr,
                               final boolean aIsExtractAlpha,
                               final boolean aIsExtractNum) {
    if (aStr==null) return EMPTY;
    if (!aIsExtractAlpha && !aIsExtractNum) return EMPTY;

    int n= aStr.length();
    StringBuffer r=new StringBuffer(n);
    for (int i=0; i<n; i++) {
      char ch=aStr.charAt(i);
      if (aIsExtractAlpha && Character.isLetter(ch)) r.append(ch);
      else
      if (aIsExtractNum && Character.isDigit(ch)) r.append(ch);
    }
    return r.toString();
  }  // extract


  /**
   * Removes all non-alpha numeric characters from a string.
   * @param aStr The string to process.
   * @return String The formatted string.
   */
  public static String extractAlphaNum(final String aStr) {
    return extract(aStr, true, true);
  }

  /**
   * Returns only those characters within the string that are alpha.
   * @param aStr String to extract from.
   * @return String Returns alpha characters with the string passed.
   */
  public static String extractAlpha(final String aStr) {
    return extract(aStr, true, false);
  }

  /**
   * Returns only those characters within the string that are numeric.
   * @param str
   * @return String Returns numeric characters with the string passed.
   */
  public static String extractNumeric(final String str) {
    int n= str.length();
    StringBuffer num=new StringBuffer(n);
    for (int x=0; x<n; x++) {
      char ch=str.charAt(x);
      if (Character.isDigit(ch))  num.append(ch);
    }
    return num.toString();
  }

  /**
   * Get the total length of strings in the array
   * @param aArray of strings
   * @return length of all strings
   */
  public static int getLengthSum(final String[] aArray) {
    if (aArray==null) return 0;
    int sum=0;
    for (int i=0, n=aArray.length; i<n; i++)
      if (aArray[i] != null)
        sum += aArray[i].length();
    return sum;
  } // getLengthSum

  /**
  * Create a string of a repeated character.
  * @param aChar Character to repeat.
  * @param aCount How many times.
  * @return Repeated character string.
  */
  public static String repeat(final char aChar, final int aCount) {
    if (aCount == 0) return EMPTY;
    char[] buf = new char[aCount];
    Arrays.fill(buf, aChar);
    return new String(buf);
  } // repeat

  /**
   * Create a string of blank space characters.
   * @param aCount Size of string
   * @return The blanks string.
   */
  public static String blanks(final int aCount) {
    return repeat(' ', aCount);
  }

  /**
   * Append a single space before and after aStr
   * @param aStr The string to pad.
   * @return The padded string.
   */
  public static String pad(final String aStr) {
    return BLANK + aStr + BLANK;
  }

  /** Count how many aSub exist in aSource */
  public static int countSubstring(final String aSource, final String aSub) {
    int index=0, count=0;
    int subLen = aSub.length();

    while (true) {
      index = aSource.indexOf(aSub, index);
      if (index == -1) break;
      count++;
      index += subLen;
    } // while
    return count;
  } // countSubstring

  /** return the hex character for a single digit */
  private static char toHex(byte aDigit) {
    aDigit = (byte) (Math.abs(aDigit) % 16);
    int hex;
    if (aDigit < 10)
      hex = aDigit + '0';
    else
      hex = aDigit + 'A' - 10;
    return (char) hex;
  } // toHex

  /** the hex representation for a character */
  public static String toHex(char aChar) {
    return toHex(aChar, false);
  } // toHex

  /** the hex representation for a character, can trim leading zeros */
  public static String toHex(char aChar, final boolean aIsTrim) {
    StringBuffer buf = new StringBuffer(4);
    int digit;

    for (int i=0; i < 4; i++) {
      digit = aChar % 16;
      buf.append(toHex((byte)digit) );
      aChar /= 16;
    }
    buf.reverse();

    if (!aIsTrim)  return buf.toString();

    for (int i=0; i<4; i++) {
      char ch= buf.charAt(i);
      if (ch != '0') {
        return buf.substring(i);
      }
    }
    return buf.toString();
  } // toHex


/**
 * Convert the hex char into its decimal value, return -1 if not valid.
 * @param aHex char
 * @return int -1 if aHex is not in (0..9, A..F)
 */
  public static int fromHex(char aHex) {
    aHex= Character.toUpperCase(aHex);
    if (aHex >= '0' && aHex <='9') return aHex - '0';
    if (aHex >= 'A' && aHex <='F') return aHex - 'A' + 10;
    return -1;
  }  // fromHex

/**
 * Convert aHex into its numeric value.
 * @param aHex String A hex string to convert to long. The maximum length for aHex is
 * 15, in order to fit within a long.
 * @return -1 if error.
 */
  public static long fromHex(final String aHex) {
    int n=aHex.length();
    if (n>15) return -1;

    long r=0;
    int c;
    for (int i=0; i<n; i++) {
      c= fromHex(aHex.charAt(i));
      if (c==-1) return -1;
      r= r*16 + c;
    }
    return r;
  }  // fromHex

/** The Unicode representation for a char. */
  public static String toUnicode(final char aChar) {
    return "\\u" + toHex(aChar);
  } // toUnicode

/** The Unicode representation for a string. */
  public static String toUnicode(final String aString) {
    StringBuffer buf = new StringBuffer();
    for (int i=0; i<aString.length(); i++) {
      buf.append( toUnicode(aString.charAt(i)) );
    }
    return buf.toString();
  } // toUnicode

/**
 * Convert a unicode string for a single character to its numeric value.
 * @param aUnicode Example: \u0010 will return 16. The string must be 6 chars. long.
 * @return int -1 if error.
 */
  public static int fromUnicode(final String aUnicode) {
    if (aUnicode.length() != 6) return -1;
    String u= aUnicode.toUpperCase();
    if (!u.startsWith("\\U")) return -1;

    u= u.substring(2);
    return (int) fromHex(u);
  } // fromUnicode()

/** Unescape a string that may contain unicode chars */
  public static String unescapeUnicode(final String aString) {
    int n= aString.length();
    String caps= aString.toUpperCase();
    StringBuffer r= new StringBuffer(n);
    int start=0;
    while (true) {
      int i= caps.indexOf("\\U", start);
      if (i<0) {
        r.append( aString.substring(start) );
        break;
      }
      r.append( aString.substring(start, i) );
      String us= substring(aString, i, i+6);  // unicode string
      int ui= fromUnicode(us);  // its value
      if (ui<0) {  // not a valid unicode
        r.append(us.charAt(0));
        start=i+1;
      } else {  // valid unicode, convert it to one char
        r.append( (char) ui);
        start= i+6;
      }
    }  // while
    return r.toString();
  }  // enescapeUnicode

  /**
   * Check if aSubject starts with aTarget case insensetive
   * @param aSubject String to inspect
   * @param aTarget String to look for
   * @return true if aSubject starts with aTarget, false otherwise.
   */
  public static boolean startsWithIgnoreCase(final String aSubject, final String aTarget) {
    if (aSubject.length() < aTarget.length()) return false;
    String pre = aSubject.substring(0, aTarget.length() );
    return pre.equalsIgnoreCase(aTarget);
  } // startsWithIgnoreCase()

  /**
   * Check if aSubject starts with aTarget according to the aCaseSensetive flag
   * @param aSubject String to inspect
   * @param aTarget String to look for
   * @param aCaseSensetive Determine if the search is case sensetive or not.
   * @return true if aSubject starts with aTarget, false otherwise.
   */
  public static boolean startsWith(final String aSubject,
                                   final String aTarget,
                                   final boolean aCaseSensetive) {
    if (aCaseSensetive)   return aSubject.startsWith(aTarget);
    return startsWithIgnoreCase(aSubject, aTarget);
  } // startsWith()


/**
 * Check if aSubject ends with aTarget case insensetive
 * @param aSubject String to inspect
 * @param aSuffix String to look for
 * @return true if aSubject ends with aTarget, false otherwise.
 */
  public static boolean endsWithIgnoreCase(final String aSubject, final String aSuffix) {
    int sufLen= aSuffix.length();
    if (aSubject.length() < sufLen) return false;
    String suf= right(aSubject, sufLen);
    return suf.equalsIgnoreCase(aSuffix);
  } // startsWithIgnoreCase()

/**
 * Check if aSubject ends with aSuffix according to the aCaseSensetive flag
 * @param aSubject String to inspect
 * @param aSuffix String to look for
 * @param aCaseSensetive Determine if the search is case sensetive or not.
 * @return true if aSubject ends with aSuffix, false otherwise.
 */
  public static boolean endsWith(final String aSubject,
                                 final String aSuffix,
                                 final boolean aCaseSensetive) {
    if (aCaseSensetive)  return aSubject.endsWith(aSuffix);
    return endsWithIgnoreCase(aSubject, aSuffix);
  } // startsWith()

  /**
  * Check if aIndex>=0 and aIndex < aTarget.length()
  */
  public static boolean isValidIndex(final String aTarget, final int aIndex) {
    return ( (aIndex>=0) && (aIndex<aTarget.length()) );
  } // isValidIndex()

  /**
   * Replace every single quote in aStr with two single quotes. Useful for
   *   SQL queries.
   * @param aStr The string to replace its single quotes
   * @return The string with all single quotes replaced
   */
  public static String replaceQuote4Db(final String aStr) {
    return replace(aStr, SINGLE_QUOTE, "''");
  }

  /**
   * Replace all occurance of html tags with their encoding.
   * @param aStr The string to replace its html tags.
   * @return The string with all & replaced with &amp;amp; and
   *   < replaced with &amp;lt;
   */
  public static String replaceHtmlTags(final String aStr) {
    String search= "&<";
    String[] rep= {"&#38;", "&lt;"};
    return replaceAll(aStr, search, rep);
  }

/**
 * Replaces the following: <pre>
 * &   =>  &amp;#38;
 * <   =>  &amp;lt;
 * '   =>  ''
 * </pre>
 * Useful to save html form field to db
 * @param aStr String The string to replace its html tags and single quotes.
 * @return String The string with all & < ' replaced.
 */
  public static String replaceHtmlTagsAndQuote(final String aStr) {
    String search= "&<'";
    String[] rep= {"&#38;", "&lt;", "''"};
    return replaceAll(aStr, search, rep);
  }


  /**
   * Replace all occurance of double quote with the escape code and double quote.
   * @param aStr The string to replace its double quotes.
   * @return The string with all double quotes replaced with escape code and double quote.
   */
  public static String replaceDoubleQuote4Html(final String aStr) {
    return replace(aStr, DOUBLE_QUOTE, "\\\"");
  }

  /**
   * Make sure aStr is prefixed with aPrefix.
   * @param aStr String to inspect.
   * @param aPrefix Prefix that must start the returned string.
   * @param aCaseSensetive is prefix case sensetive?
   * @return If aStr is blank return empty string, else return aStr but make
   *   sure it starts with aPrefix.
   */
  public static String prefix(String aStr, String aPrefix,
                              boolean aCaseSensetive ) {
    if (StringUtility.isBlank(aStr)) return StringUtility.EMPTY;
    aStr= aStr.trim();
    if (startsWith(aStr, aPrefix, aCaseSensetive)) return aStr;
    return aPrefix + aStr;
  }

/**
 * Make sure aStr is prefixed with aPrefix. Case insensetive.
 * @param aStr String to inspect.
 * @param aPrefix Prefix that must start the returned string.
 * @return If aStr is blank return empty string, else return aStr but make
 *   sure it starts with aPrefix.
 */
  public static String prefix(String aStr, String aPrefix) {
    return prefix(aStr, aPrefix, false);
  }

/** Return the last character in the string */
  public static char lastChar(final String aStr) {
    return aStr.charAt( aStr.length()-1 );
  } // lastChar

  /** Search a String to find the first index of any character in the given
   * set of characters starting at aStartIndex
   */
  public static int indexOfAny(final String aStr,
                               final String aSearchChars,
                               final int aStartIndex) {
    for(int i= aStartIndex, n= aStr.length(); i<n; i++) {
      char c= aStr.charAt(i);
      if (aSearchChars.indexOf(c)>=0) return i;
    }
    return -1;
  } // indexOfAny

/**
 * Search aStr to find the index of the first char that is not in aSearchChars.
 * @param aStr String String to search.
 * @param aSearchChars String chars to search for any but them.
 * @param aStartIndex int starting index in aStr
 * @return int
 */
  public static int indexOfAnyBut(final String aStr,
                                  final String aSearchChars,
                                  final int aStartIndex) {
    for(int i= aStartIndex, n= aStr.length(); i<n; i++) {
      char c= aStr.charAt(i);
      if (aSearchChars.indexOf(c) < 0) return i;
    }
    return -1;
  } // indexOfAnyBut


  /** Check if aCh is either a single quote or double quote */
  public static boolean isQuote(final char aCh) {
    return aCh=='"' || aCh=='\'';
  }

  /** Compares two Strings, returning true if they are equal, taking into consideration
   *  the case sensetivity parameter.
   */
  public static boolean equals(final String aStr1,
                               final String aStr2,
                               final boolean aCaseSensetive) {
    if (aCaseSensetive) return equals(aStr1, aStr2);
    return equalsIgnoreCase(aStr1, aStr2);
  } // equals

  /**
   * Check if a one-character string is a member of the given set of characters.
   * @param aOneChar A string of one char.
   * @param aSetOfChars The set of chars that can be valid.
   * @return true if aSetOfChars contains aOneChar.
   */
  public static boolean equalsAnyChar(final String aOneChar,
                                      final String aSetOfChars) {
    if (aOneChar.length() != 1) return false;
    return aSetOfChars.indexOf(aOneChar.charAt(0)) >=0;
  } // equals

  /**
   * Search a StringBuffer for the first occurance of aTarget starting at aFromIndex.
   * @param aBuffer StringBuffer
   * @param aFromIndex int
   * @param aTarget char
   * @return int index of aTarget, -1 if not found
   * Note that Java 1.4 StringBuffer has an equivelant method
   */
  public static int indexOfSB(final StringBuffer aBuffer,
                              final int aFromIndex,
                              final char aTarget) {
    for (int i=aFromIndex, n=aBuffer.length(); i<n; i++)
      if (aBuffer.charAt(i)==aTarget) return i;
    return -1;
  }  // indexOfSB

  /**
   * Search a StringBuffer for the first occurance of aTarget starting at first character.
   * @param aBuffer StringBuffer
   * @param aTarget char
   * @return int index of aTarget, -1 if not found
   * Note that Java 1.4 StringBuffer has an equivelant method
   */
  public static int indexOfSB(final StringBuffer aBuffer, final char aTarget) {
    return indexOfSB(aBuffer, 0, aTarget);
  }

  public static String toString(final StringBuffer aBuffer) {
    if (aBuffer==null  || aBuffer.length()==0) return EMPTY;
    return aBuffer.toString();
  }

/** Check is a string buffer is null or zero-length */
  public static boolean isEmpty(final StringBuffer aBuffer) {
    if (aBuffer==null) return true;
    if (aBuffer.length()==0) return true;
    return false;
  }

/** Check if aSet contains aItem */
  public static boolean in(final String aItem, final String[] aSet,
                           final boolean aCaseSensetive) {
    for (int i=0, n=aSet.length; i<n; i++) {
      if (equals(aItem, aSet[i], aCaseSensetive)) return true;
    }
    return false;
  }  // in

/** Check if aSet contains aItem */
  public static boolean in(final String aItem, final String[] aSet) {
    return in(aItem, aSet, true);
  }

/** Delete from aSource all chars that are in aSetToRomve */
  public static String deleteSet(final String aSource, final String aSetToRomve) {
    int n= aSource.length();
    StringBuffer b= new StringBuffer(n);
    char c;
    for (int i=0; i<n; i++) {
      c= aSource.charAt(i);
      if (aSetToRomve.indexOf(c) <0) b.append(c);
    }
    return b.toString();
  } // deleteSet


/** Delete from aSource all chars that are not in aSetToKeep */
  public static String deleteButSet(final String aSource, final String aSetToKeep) {
    int n= aSource.length();
    StringBuffer b= new StringBuffer(n);
    char c;
    for (int i=0; i<n; i++) {
      c= aSource.charAt(i);
      if (aSetToKeep.indexOf(c) >= 0) b.append(c);
    }
    return b.toString();
  }  // deleteButSet

/** Delete from aSource all chars that are less than 32 */
  public static String deleteControlChars(final String aSource) {
    int n= aSource.length();
    StringBuffer b= new StringBuffer(n);
    char c;
    for (int i=0; i<n; i++) {
      c= aSource.charAt(i);
      if (c>=32) b.append(c);
    }
    return b.toString();
  } // deleteControlChars

/** Delete from aSource all chars that are less than 32 or greater than 127. */
  public static String deleteNonAnsi(final String aSource) {
    int n= aSource.length();
    StringBuffer b= new StringBuffer(n);
    char c;
    for (int i=0; i<n; i++) {
      c= aSource.charAt(i);
      if (c<32 || c>127) continue;
      b.append(c);
    }
    return b.toString();
  } // deleteControlChars
  
/**
* Similar to String(char[] value, int offset, int count) but with checked
* offset and count.
*/
  public static String createInstance(char[] aValue, int aOffset, int aCount) {
    if (aCount <= 0) return EMPTY;
    int valLength = aValue.length;
    if (aOffset >= valLength) return EMPTY;
    aOffset = Math.max(aOffset, 0);

    if (aOffset + aCount > valLength)
      aCount = valLength - aOffset;
    return new String(aValue, aOffset, aCount);
  } // createInstance

/**
 * Counts how many times the substring list items appears in the source String.
 * @param aSource String The string to inspect.
 * @param aSubList ListOfString items of this list are counted in the source.
 * @return int
 */
  public static int countMatches(final String aSource, final ListOfString aSubList) {
    int r=0;
    for (int i=0, n=aSubList.size(); i<n; i++) {
      i += countMatches(aSource, aSubList.getItem(i));
    }
    return r;
  }

/**
* The last index in aSource for any item in aTarget.
*/
  public static int lastIndexOf(String aSource, List aTarget, int[] aListIndex) {
    ListIterator it = aTarget.listIterator();
    String item;
    int maxIndex=-1;
    int thisIndex;
    aListIndex[0] = -1;

    while (it.hasNext()) {
      item = (String) it.next();
      thisIndex = aSource.lastIndexOf(item);
      if (thisIndex > maxIndex) {
        maxIndex = thisIndex;
        aListIndex[0] = it.previousIndex();
      }
    } // while
    return maxIndex;
  } // lastIndexOf

/** Length of aString, 0 if aString is null */
  public static int length(final String aString) {
    if (aString==null) return 0;
    return aString.length();
  }

/**
 * Search aSource starting at aStartPos for the first occurance of aSearch
 * @param aSource String The string to search
 * @param aSearch String The string to search for
 * @param aStartPos int Starting index in the string to search
 * @param aCaseSensetive boolean is the search case sensetive
 * @return int index of first occurance
 */
  public static int indexOf(String aSource,
                            String aSearch,
                            final int aStartPos,
                            final boolean aCaseSensetive) {
    if (aCaseSensetive) return indexOf(aSource, aSearch, aStartPos);
    aSource= StringUtility.defaultString(aSource).toLowerCase();
    aSearch= StringUtility.defaultString(aSearch).toLowerCase();
    return indexOf(aSource, aSearch, aStartPos);
  }  // indexOf

  public static int indexOf(String aSource,
                            String aSearch,
                            final boolean aCaseSensetive) {
    return indexOf(aSource, aSearch, 0, aCaseSensetive);
  }  // indexOf

/**
 * Compare two strings with case sensetivity flag. Will check for null values, a null
 * value is considered < any string.
 * @param s1 String
 * @param s2 String
 * @param aCaseSensetive boolean
 * @return 0 if s1 equals s2, -1 if s1 < s2, +1 if s1> s2.
 */
  public static int compare(final String s1, final String s2,
                            final boolean aCaseSensetive) {
    if (s1==null && s2==null) return 0;
    if (s1==null) return -1;
    if (s2==null) return 1;
    if (aCaseSensetive) return s1.compareTo(s2);
    return s1.compareToIgnoreCase(s2);
  } // compare

/**
 * Compare two string (case sensetive). Will check for null values, a null
 * value is considered < any string.
 * @param s1 String
 * @param s2 String
 * @return 0 if s1 equals s2, -1 if s1 < s2, +1 if s1> s2.
 */
  public static int compare(final String s1, final String s2) {
    return compare(s1, s2, true);
  }

  public static String trimLeft(final String aString) {
    if (isEmpty(aString)) return EMPTY;

    char[] ch= aString.toCharArray();
    for (int i=0, n=ch.length; i<n; i++) {
      if (ch[i] > 32) return aString.substring(i);
    }
    return EMPTY;
  }  // trimLeft

/** Return the soundex of the given string */
  public static String soundex(final String aString) {
    if (isBlank(aString)) return EMPTY;

    // return RefinedSoundex.US_ENGLISH.soundex(aString);
    // return Soundex.US_ENGLISH.soundex(aString);  // has bug !!
    String r= new Soundex().soundex(aString);
    return isBlank(r) ? aString : r; // if item is a number its soundex is itself.
  }

/**
 * Get a substring by dividing the source string into blocks of equal size and returning
 * the block at the given index
 * @param aString String Source string to extract from. If this is null, null is returned.
 * @param aBlockIndex int Index of the block starting at 0. If it is greater than
 *        number of blocks or less than zero, null is returned.
 *        If it specifies the index of the last block and remaining chars are less
 *        than the block size, the remaining chars are returned.
 * @param aBlockSize int Number of characters in the block. If less than 1, null is
 *        returned.
 * @return String Substring block at the given index.
 */
  public static String substringBlock(final String aString,
                                      final int aBlockIndex,
                                      final int aBlockSize) {
    if (aString==null) return null;
    if (aBlockSize<1) return null;
    if (aBlockIndex<0) return null;

    int len= aString.length();
    int blockCount= len / aBlockSize;
    if (blockCount * aBlockSize < len) blockCount++;
    if (aBlockIndex>= blockCount) return null;

    int index= aBlockIndex * aBlockSize;
    return mid(aString, index, aBlockSize);
  }  // substringBlock

/**
 * Replaces multiple characters in a String in one go. This method can also be used to
 * delete characters.
 * For example: <code>
 * replaceAll("hello", "ho", {"HH", "OO"} ) = HHellOO.
 * </code>
 *
 * A null string input returns null. An empty ("") string input returns an empty string.
 * A null or empty set of search characters returns the input string.<br>
 *
 * The length of the search characters should normally equal the length of the
 * replace string array. If the search characters is longer, then the extra search
 * characters are deleted. If the search characters is shorter, then the extra
 * replace strings are ignored.
 *
 * @param aString String to replace characters in, may be null
 * @param aSearchChars a set of characters to search for, may be null
 * @param aReplaceStrings String[] a set of strings to replace, may be null
 * @return String modified String, null if null string input
 */
  public static String replaceAll(final String aString,
                                  final String aSearchChars,
                                  final String[] aReplaceStrings) {
    if (isEmpty(aString) || isEmpty(aSearchChars) || aReplaceStrings==null) {
        return aString;
    }

    boolean isModified = false;
    int n= aString.length();
    int replaceSize= aReplaceStrings.length;
    StringBuffer buf = new StringBuffer(n);
    for (int i=0; i<n; i++) {
      char ch=aString.charAt(i);
      int index=aSearchChars.indexOf(ch);
      if (index < 0) {
        buf.append(ch);
        continue;
      }
      isModified=true;
      if (index >= replaceSize) continue;  // skip the char
      String rep= aReplaceStrings[index];
      if (rep==null) continue;  // skip the char

      buf.append(rep);
    }  // for

    if (isModified) return buf.toString();
    return aString;
  }  // replaceAll


/**
 * Replace the substring with the given <code>with</code>.
 * If the value between <code>startIndex</code> and <code>endIndex</code>
 *     is equals to <code>with</code>, then <code>source</code> object is returned,
 *     w.o. creating a new object.
 * @param source The string to search.
 * @param with The new string to place.
 * @param startIndex if less than 0, it will be adjusted to zero.
 * @param endIndex if greater than the length of <code>source</code>, it will be adjusted
 *        to the length of <code>source</code>-1.
 * @return the string with the substring replaced. 
 */  
  public static String replaceBetweenOnce(final String source, String with,
                                          int startIndex, int endIndex) {
    if (startIndex > endIndex) return source;
    if (StringUtility.isEmpty(source)) return StringUtility.EMPTY;
    if (startIndex < 0) startIndex = 0;
    int len = StringUtility.length(source);
    if (endIndex >= len) endIndex = len - 1;
    if (with==null) with= StringUtility.EMPTY;

    if (endIndex-startIndex+1 == with.length()) {
      String sub= StringUtility.substring(source, startIndex, endIndex+1);
      if (StringUtility.equals(sub, with)) return source;
    }
    
    String s1 = StringUtility.left(source, startIndex);
    String s2 = StringUtility.substring(source, endIndex + 1);
    return s1 + with + s2;
  }
  
/**
 * Replace substrings enclosed between <code>start</code> and <code>end</code>
 * with the given replacement <code>with</code>. This can replace all found matches
 * or a specific number of replacements as given by <code>maxReplacements</code>.
 * @param source The string to search.
 * @param with The new string to place. 
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param startingAt index in source to start at.
 * @param maxReplacements maximum number of values to replace, or -1 if no maximum.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> are also
 *        replaced, when false only the enclosed value will be replaced.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return The string with replacements.
 */  
  public static String replaceBetween(final String source, String with,
                                      final String start, final String end, 
                                      int startingAt, final int maxReplacements,
                                      final boolean isInclusive,
                                      final boolean isCaseSensetive) {
    if (maxReplacements==0) return source;
    int startLen= StringUtility.length(start);
    int endLen= StringUtility.length(end);
    if (startLen==0 || endLen==0) return source;
    int sourceLen= StringUtility.length(source);
    if (sourceLen==0 || startingAt >= sourceLen) return source;

    if (startingAt < 0) startingAt = 0;
    if (with == null) with = StringUtility.EMPTY;
    
    StringBuffer sb= new StringBuffer(sourceLen);
    sb.append( StringUtility.left(source, startingAt) );
    int replacementsCount= 0;
    while (true) {
      if (maxReplacements>0 && replacementsCount>=maxReplacements) {
        sb.append( StringUtility.substring(source, startingAt) );
        break;
      }
      int p1= StringUtility.indexOf(source, start, startingAt, isCaseSensetive);
      if (p1<0) {
        sb.append( StringUtility.substring(source, startingAt) );
        break;
      }
      int p2= StringUtility.indexOf(source, end, p1+startLen, isCaseSensetive);
      if (p2<0) {
        sb.append( StringUtility.substring(source, startingAt) );
        break;
      }
      sb.append( StringUtility.substring(source, startingAt, p1) );
      if (!isInclusive) sb.append(start);
      sb.append(with);
      if (!isInclusive) sb.append(end);
      startingAt= p2 + endLen;
      replacementsCount++;
    }
    return sb.toString();
  }
  
/**
 * Replace all substrings enclosed between <code>start</code> and <code>end</code>
 * with the given replacement <code>with</code>. The replacement is inclusive, i.e.
 * the <code>start</code> and <code>end</code> markers will be replaced. The matching
 * for <code>start</code> and <code>end</code> is NOT case sensitive.
 * @param source The string to search.
 * @param with The new string to place. 
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @return The string with replacements.
 */  
  public static String replaceBetweenAll(final String source, final String with, 
                                         final String start, final String end) {
    return replaceBetween(source, with, start, end, 0, -1, true, false);
  }
  
/**
 * Replace the first substring enclosed between <code>start</code> and <code>end</code>
 * with the given replacement <code>with</code>.
 * @param source The string to search.
 * @param with The new string to place. 
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> are also
 *        replaced, when false only the enclosed value will be replaced.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return The string with replacement.
 */  
  public static String replaceBetweenOnce(final String source, final String with, 
                                          final String start, final String end, 
                                          final boolean isInclusive, 
                                          final boolean isCaseSensetive) {
    return replaceBetween(source, with, start, end, 0, 1,
                          isInclusive, isCaseSensetive);
  }
  
/**
 * Replace the first substring enclosed between <code>start</code> and <code>end</code>
 * with the given replacement <code>with</code>. The replacement is inclusive, i.e.
 * the <code>start</code> and <code>end</code> markers will be replaced. The matching
 * for <code>start</code> and <code>end</code> is NOT case sensitive.
 * @param source The string to search.
 * @param with The new string to place. 
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @return The string with replacement.
 */  
  public static String replaceBetweenOnce(final String source, final String with, 
                                          final String start, final String end) {
    return replaceBetween(source, with, start, end, 0, 1, true, false);
  }

/**
 * Remove the substring between the start and end index.
 * @param source The string to search.
 * @param startIndex
 * @param endIndex
 * @return source with substring removed.
 */  
  public static String removeBetweenOnce(final String source, final int startIndex,
                                         final int endIndex) {
    return replaceBetweenOnce(source, StringUtility.EMPTY, startIndex, endIndex);
  }
  
/**
 * Remove the first substring found between <code>start</code> and <code>end</code> 
 * @param source The string to search.
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param startingAt The index to start searching at.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> markers 
 *         will be removed.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return source with substring removed.
 */  
  public static String removeBetweenOnce(final String source, 
                                         final String start, final String end,
                                         final int startingAt,
                                         final boolean isInclusive, 
                                         final boolean isCaseSensetive) {
    return replaceBetween(source, StringUtility.EMPTY, start, end, 
                          startingAt, 1, isInclusive, isCaseSensetive);
  }
  
/**
 * Remove the first substring found between <code>start</code> and <code>end</code> 
 * starting at the beginning of the string.
 * @param source The string to search.
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> markers 
 *         will be removed.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return source with substring removed.
 */  
  public static String removeBetweenOnce(final String source, 
                                         final String start, final String end,
                                         final boolean isInclusive, 
                                         final boolean isCaseSensetive) {
    return replaceBetween(source, StringUtility.EMPTY, start, end, 
                          0, 1, isInclusive, isCaseSensetive);
  }
  
/**
 * Remove all substrings found between <code>start</code> and <code>end</code> 
 * @param source The string to search.
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param startingAt The index to start searching at.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> markers 
 *         will be removed.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return source with all substrings removed.
 */  
  public static String removeBetweenAll(final String source, 
                                        final String start, final String end,
                                        final int startingAt,
                                        final boolean isInclusive, 
                                        final boolean isCaseSensetive) {
    return replaceBetween(source, StringUtility.EMPTY, start, end, 
                          startingAt, -1, isInclusive, isCaseSensetive);
  }
  
/**
 * Remove all substrings found between <code>start</code> and <code>end</code> 
 * starting at the beginning of the string.
 * @param source The string to search.
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @param isInclusive When true, the <code>start</code> and <code>end</code> markers 
 *         will be removed.
 * @param isCaseSensetive Should the string search be case sensitive.
 * @return source with all substrings removed.
 */  
  public static String removeBetweenAll(final String source, 
                                        final String start, final String end,
                                        final boolean isInclusive, 
                                        final boolean isCaseSensetive) {
    return replaceBetween(source, StringUtility.EMPTY, start, end, 
                          0, -1, isInclusive, isCaseSensetive);
  }
 
/**
 * Remove all substrings found between <code>start</code> and <code>end</code> 
 * starting at the beginning of the string. The removal is inclusive, i.e.
 * the <code>start</code> and <code>end</code> markers will be removed. The matching
 * for <code>start</code> and <code>end</code> is NOT case sensitive.
 * @param source The string to search.
 * @param start Starting substring marker.
 * @param end Ending substring marker.
 * @return source with all substrings removed.
 */  
  public static String removeBetweenAll(final String source, 
                                        final String start, final String end) {
    return replaceBetween(source, StringUtility.EMPTY, start, end, 0, -1,
                          true, false);
  }

/**
 * Returns either the passed in String, or <code>null</code> if the string is 
 * an empty String ("") or <code>null</code>.
 * @author Estep
 * @param aStr
 * @return The passed in String or <code>null</code>.
 */
  public static String defaultToNull(final String aStr) {
   return defaultIfEmpty(aStr, null);
  }

/**
 * Gets the substring after the first occurrence of open and close separators, starting
 * at the given <code>fromIndex</code>.
 * The open and close separators are not returned. For example
 * <code>substringAfterOpenClose("foo[bar]lan", "[", "]", 0)</code> will return
 * <code>lan</code>.
 * @param str - the String to get a substring from, may be null
 * @param open - the open marker before the substring, may be null 
 * @param close - the close marker before the substring, may be null
 * @param fromIndex - the starting index to search from.
 * @return String the substring, <code>null</code> if no match
 */
  public static String substringAfterOpenClose(final String str, final String open, 
                                               final String close, final int fromIndex) { 
    if (StringUtility.isEmpty(str)) return null;
    if (open == null || close == null) return null;
    int strLen= str.length();
    if (fromIndex<0 || fromIndex>=strLen) return null;
    
    int openIndex = str.indexOf(open, fromIndex);
    if (openIndex < 0) return null;
    
    int openLen= open.length();
    int closeIndex= str.indexOf(close, openIndex+openLen);
    if (closeIndex<0) return null;
    
    int closeLen= close.length();
    int i= closeIndex+closeLen;
    if (i>=strLen) return null;
    
    return str.substring(i);
  }

/**
 * Gets the substring after the first occurrence of open and close separators. 
 * The open and close separators are not returned. For example
 * <code>substringAfterOpenClose("foo[bar]lan", "[", "]", 0)</code> will return
 * <code>lan</code>.
 * @param str - the String to get a substring from, may be null
 * @param open - the open marker before the substring, may be null 
 * @param close - the close marker before the substring, may be null
 * @return String the substring, <code>null</code> if no match
 */
  public static String substringAfterOpenClose(final String str, final String open, 
      final String close) {
    return substringAfterOpenClose(str, open, close, 0);
  }
  
  
  private static final String ERR_FIELD_NOT_FOUND=
    "The current java.lang.String class does not have a field named value. ";
  private static final String ERR_FIELD_NOT_ACCESSIABLE=
    "Could not reassign a value to the field 'char[] String.value' . ";
  
/**
 * Mutate the value of a string using reflection hack. Note that this may not work on non-Sun
 * JVMs. Note that if stringToMutate is <i>interned</i> by the JVM with other strings,
 * then all the other strings will have their value mutated.
 * @param stringToMutate The string whose value is to be mutated (changed)
 * @param newValue the new value. If the newValue length is greater than the length
 * of stringToMutate, then the extra chars will be trimmed. If  the newValue length is 
 * less than the length of stringToMutate, then stringToMutate will be right-padded with
 * filler chars. 
 * @param filler The character to fill with when length of newValue is less than length
 * of stringToMutate.
 */
  public static void mutate(final String stringToMutate, String newValue, final char filler) {
    int strLen= length(stringToMutate);
    if (strLen==0) return;
    
    Field valueField;
    try {
      valueField= String.class.getDeclaredField("value");
    } catch (NoSuchFieldException e) {
      throw new IllegalAccessError( ERR_FIELD_NOT_FOUND + e.getMessage() );
    }
    // we assume here that the String.value field is a char array !!!
    boolean oldIsAccessible= valueField.isAccessible();
    valueField.setAccessible(true);
    
    newValue= defaultString(newValue);
    newValue= left(newValue, strLen);
    newValue= rightPad(newValue, strLen, filler);
    
    try {
      valueField.set(stringToMutate, newValue.toCharArray());
    } catch (IllegalAccessException e) {
      throw new IllegalAccessError( ERR_FIELD_NOT_ACCESSIABLE + e.getMessage());
    } finally {
      valueField.setAccessible(oldIsAccessible);
    }
  }
  
/**
 * Mutate the value of a string using reflection hack. Note that this may not work on non-Sun
 * JVMs. Note that if stringToMutate is <i>interned</i> by the JVM with other strings,
 * then all the other strings will have their value mutated.
 * @param stringToMutate The string whose value is to be mutated (changed)
 * @param newValue the new value. If the newValue length is greater than the length
 * of stringToMutate, then the extra chars will be trimmed. If  the newValue length is 
 * less than the length of stringToMutate, then stringToMutate will be right-padded with
 * space chars. 
 */
  public static void mutate(final String stringToMutate, final String newValue) {
    mutate(stringToMutate, newValue, ' ');
  }

/**
 * Modify the given text by options based on the given index. You may consider using
 * java.text.MessageFormat for more options, but this method is simpler to use.
 * For example if text is:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 *   the {man|men|woman|women} in the car {has|have|has|have} arrived<br>
 * we can use this method to get the result:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * the men in the car have arrived<br>
 * if we use an index=1.
 * 
 * @param text containing options where one will be selected. 
 * @param index of the selected option. If the index specifies a non existing option,
 * then the option will not be selected.
 * @param open the start tag around options, e.g. {
 * @param close the end tag around options, e.g. }
 * @param separator between options, e.g. |
 * @return The text with an option selected based on given index.
 */
  public static String selectOption(final String text, final int index, 
                           final String open, final String close, 
                           final char separator) {
    if (index<0) return text;
    
    TextTemplate tt= new TextTemplate(text, open, close);
    ListOfString tags= tt.getAllTagNames();
    for (int i=0, n=tags.size(); i<n; i++) {
      String tag= tags.getItem(i);
      String[] options= StringUtils.splitPreserveAllTokens(tag, separator);
      if (index >= ArrayUtilities.length(options)) continue;
      
      tt.setTagValue(tag, options[index]);
    }
    return tt.getText();
  }
  
/**
 * Modify the given text by options based on the given index. You may consider using
 * java.text.MessageFormat for more options, but this method is simpler to use.
 * For example if text is:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 *   the [man|men|woman|women] in the car [has|have|has|have] arrived<br>
 * we can use this method to get the result:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * the men in the car have arrived<br>
 * if we use an index=1.
 * <p>The open mark is { , the close mark is } , while the separator is |</p>
 * @param text containing options where one will be selected. 
 * @param index of the selected option. If the index specifies a non existing option,
 * then the option will not be selected.
 * @return The text with an option selected based on given index.
 */
  public static String selectOption(final String text, final int index) {
    return selectOption(text, index, "{", "}", '|');
  }
  
/**
 * Select an alternate of the given text options based on the value of isPlural.
 * This method uses the same text format as in selectOption() method.
 * For example if text is:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 *   the [man|men] in the car [has|have] arrived<br>
 * We can use this method to get the result:<br>&nbsp;&nbsp;&nbsp;&nbsp;
 * the men in the car have arrived<br>
 * if we pass isPlural = true.
 * <p>The open mark is { , the close mark is } , while the separator is |</p>
 * @param isPlural if false then the first text option is selected, if true then
 * the second option is selected.
 * @param text containing options where one will be selected.
 * @return The text with either singular or plural format.
 */  
  public static String pluralize(final boolean isPlural, final String text) {
    int index= isPlural? 1 : 0;
    return selectOption(text, index);
  }

  public static String leftAndSoOn(String str, int maxLength, String suffix) {
    str= trimToEmpty(str);
    if (str.length() <= maxLength) return str;
    
    suffix= defaultString(suffix);
    int suffixLen= suffix.length();
    
    str= left(str, Math.max(0, maxLength-suffixLen)) + suffix;
    
    if (str.length() >= maxLength) str= left(str, maxLength);
    return str;
  }
  
}  // StringUtility
