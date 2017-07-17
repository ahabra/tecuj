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
package com.tek271.util.string;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Different string escapeing methods.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class StringEscapeUtility extends StringEscapeUtils {

/**
 * Url Escape the given char, appending it to the given string buffer, for example
 * the char - becomes %2D
 * @param aBuf StringBuffer to append to
 * @param aChar char char to url-encode
 * @param aIsEscapeAll boolean should all chars be encoded, if false, alph-numerics will
 *        not be encoded.
 * @return StringBuffer the string buffer which was appended to.
 */
  private static StringBuffer escapeUrlToBuffer(final StringBuffer aBuf,
                                                final char aChar,
                                                final boolean aIsEscapeAll) {
    if (aIsEscapeAll)  {
      aBuf.append('%').append(StringUtility.toHex(aChar, true));
      return aBuf;
    }

    if (StringUtility.ALPHA_NUMERIC.indexOf(aChar) >= 0) {
      aBuf.append(aChar);
      return aBuf;
    }

    aBuf.append('%').append(StringUtility.toHex(aChar, true));
    return aBuf;
  }   // escapeUrlToBuffer

/**
 * Url Escape the given char, for example the char - becomes %2D
 * @param aChar char to encode
 * @param aIsEscapeAll boolean should all chars be encoded, if false, alph-numerics will
   *        not be encoded.
 * @return String the url encode of the given char
 */
  public static String escapeUrl(final char aChar, final boolean aIsEscapeAll) {
    StringBuffer buf= new StringBuffer(4);
    return escapeUrlToBuffer(buf, aChar, aIsEscapeAll).toString();
  }  // escapeUrl

/**
 * Url Escape the given char, for example the char - becomes %2D. Will not encode
 * alph-numerics chars
 * @param aChar char to encode
 * @return String the url encode of the given char
 */
  public static String escapeUrl(final char aChar) {
    return escapeUrl(aChar, false);
  }

/**
 * Url Escape the given string, for example AB-C1 becomes AB%2DC1 or %41%42%2D%43%31
 * based on the value of aIsEscapeAll flag
 * @param aString String string to encode.
 * @param aIsEscapeAll boolean should all chars be encoded, if false, alph-numerics will
 *        not be encoded.
 * @return String the url encode of the given string
 */
  public static String escapeUrl(final String aString, final boolean aIsEscapeAll) {
    if (StringUtility.isEmpty(aString)) return StringUtility.EMPTY;

    StringBuffer buf= new StringBuffer();
    for (int i=0, n= aString.length(); i<n; i++) {
      char c= aString.charAt(i);
      escapeUrlToBuffer(buf, c, aIsEscapeAll);
    }
    return buf.toString();
  } // escapeUrl

  public static String escapeUrl(final String aString) {
    return escapeUrl(aString, false);
  }

/**
 * Html escape the given char, appending it to the given string buffer. For example
 * A becomes &amp;#65;
 * @param aBuf StringBuffer
 * @param aChar char to encode
 * @return StringBuffer the string buffer which was appended to.
 */
  private static StringBuffer escapeHtmlToBuffer(final StringBuffer aBuf,
                                                 final char aChar) {
    aBuf.append('&').append('#').append((int)aChar).append(';');
    return aBuf;
  }

/**
 * Html escape the given char. For example A becomes &amp;#65;
 * @param aChar char to encode
 * @return String the encoded value of the char. Will return numbers only, e.g. the
 * quote character " will be returned as &amp;#34; and not &amp;quot;
 */
  public static String escapeHtml(final char aChar) {
    StringBuffer buf= new StringBuffer();
    return escapeHtmlToBuffer(buf, aChar).toString();
  }

/**
 * Html escape the given string. For example AB&lt;C1 becomes AB&amp;lt;C1 or
 * &amp;#65;&amp;#66;&amp;#60;&amp;#67;&amp;#49; depending on the value of aIsEscapeAll
 * flag.
 * @param aString String to be encoded
 * @param aIsEscapeAll boolean if true, all chars will be numerically encoded, if
 * false, the org.apache.commons.lang.StringEscapeUtils.escapeHtml() method will be used.
 * @return String The encoded string.
 */
  public static String escapeHtml(final String aString, final boolean aIsEscapeAll) {
    int n= StringUtility.length(aString);
    if (n==0) return StringUtility.EMPTY;
    if (!aIsEscapeAll) {
      return StringEscapeUtils.escapeHtml(aString);
    }

    StringBuffer buf= new StringBuffer(n*6);
    for (int i=0; i<n; i++) {
      escapeHtmlToBuffer(buf, aString.charAt(i));
    }
    return buf.toString();
  }  // escapeHtml

  public static void main(String[] a) {
    char ch= 'A';
    System.out.println(escapeUrl(ch, true) );
    System.out.println(escapeUrl(ch, false) );

    String st= "AB-C1";
    System.out.println(escapeUrl(st, true) );
    System.out.println(escapeUrl(st, false) );
    System.out.println("---------------");

    char ch2= 'A';
    String st2= "AB<C1";
    System.out.println(escapeHtml(ch2));
    System.out.println(escapeHtml(st2, true));
    System.out.println(escapeHtml(st2));

  }
}  // StringEscapeUtility
