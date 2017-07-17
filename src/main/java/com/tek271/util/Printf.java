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

package com.tek271.util;

import com.tek271.util.string.StringUtility;

/**
 * Different overloaded printf methods.
 * Not as extensive as Java 1.5. Very handy for building dynamic SQL statements or
 * error messages.
 * <p>Copyright (c) 2003 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Printf {
  private static final char pQM= '?';   // question mark
  private static final char pBS= '\\';  // back slash

/** Do not use, allows extending */
  public Printf() {}

  /**
   * Replace question marks in aStr with items of aFillers. This method is overloaded
   * to support different fillers types.
   * @param aStr A string that contains question marks. e.g. "one=?, two=?, name=?"
   * @param aFillers An array whos elements will replace each question mark.
   *   e.g. {"1", "2", "abdul"}
   * @return A string where question marks are replaced by the fillers.
   * e.g. one=1, two=2, name=abdul
   * <p>If aFillers has items less than the question marks, the remaining question marks
   * are ignored.
   * <p>A question mark can be escaped with the backslash \ character.
   */
  public static String p(String aStr, String[] aFillers) {
    if ((aFillers==null) || (StringUtility.isBlank(aStr))) return aStr;
    int fn= aFillers.length;
    if (fn==0) return aStr;

    StringBuffer b= new StringBuffer(aStr.length() + StringUtility.getLengthSum(aFillers) );
    char c;   // current char
    int f=0;  // fillers index
    boolean ignoreQ= false; // ignore next question mark
    for (int i=0, n= aStr.length(); i<n; i++) {
      if (ignoreQ) { ignoreQ=false; continue; }
      if (f>=fn) { b.append(aStr.substring(i)); break; }  // no more fillers
      c= aStr.charAt(i);
      if ( (c!=pBS) && (c!=pQM) ) { b.append(c);  continue; } // not ? nor /
      if (c==pBS) {
        if (i==n-1) { b.append(c); continue; } // last char
        if (aStr.charAt(i+1)!=pQM)  { b.append(c); continue; } // / followed by not ?
        b.append(pQM);  // the / is followed by ?
        ignoreQ= true;
        continue;
      }
      // now c is ?
      if (f<fn)  b.append(aFillers[f++]);
      else b.append(pQM);
    }
    return b.toString();
  } // p

  /** @see #p(String, String[]) */
  public static String p(String aStr, String aFiller1) {
    return p(aStr, new String[] {aFiller1} );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, String aFiller1, String aFiller2) {
    return p(aStr, new String[] {aFiller1, aFiller2} );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, String aFiller1, String aFiller2, String aFiller3) {
    return p(aStr, new String[] {aFiller1, aFiller2, aFiller3} );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, String aFiller1, String aFiller2,
                         String aFiller3, String aFiller4) {
    return p(aStr, new String[] {aFiller1, aFiller2, aFiller3, aFiller4} );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, String aFiller1, String aFiller2,
                         String aFiller3, String aFiller4, String aFiller5) {
    return p(aStr, new String[] {aFiller1, aFiller2, aFiller3, aFiller4, aFiller5} );
  }


  /** @see #p(String, String[]) */
  public static String p(String aStr, int[] aFillers) {
    int size= aFillers.length;
    String[] fill= new String[size];
    for(int i=0; i<size; i++) fill[i]= String.valueOf(aFillers[i]);
    return p(aStr, fill);
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, int aFiller1) {
    return p(aStr, String.valueOf(aFiller1) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, int aFiller1, int aFiller2) {
    return p(aStr, String.valueOf(aFiller1), String.valueOf(aFiller2) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, int aFiller1, int aFiller2, int aFiller3) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, int aFiller1,
                         int aFiller2, int aFiller3, int aFiller4) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3),
             String.valueOf(aFiller4));
  }

  public static String p(String aStr, int aFiller1,
                         int aFiller2, int aFiller3, int aFiller4, int aFiller5) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3),
             String.valueOf(aFiller4),
             String.valueOf(aFiller5));
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, long[] aFillers) {
    int size= aFillers.length;
    String[] fill= new String[size];
    for(int i=0; i<size; i++) fill[i]= String.valueOf(aFillers[i]);
    return p(aStr, fill);
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, long aFiller1) {
    return p(aStr, String.valueOf(aFiller1) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, long aFiller1, long aFiller2) {
    return p(aStr, String.valueOf(aFiller1), String.valueOf(aFiller2) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, long aFiller1, long aFiller2, long aFiller3) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3) );
  }

  /** @see #p(String, String[]) */
  public static String p(String aStr, long aFiller1,
      long aFiller2, long aFiller3, long aFiller4) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3),
             String.valueOf(aFiller4));
  }

  public static String p(String aStr, long aFiller1,
                         long aFiller2, long aFiller3, long aFiller4, long aFiller5) {
    return p(aStr,
             String.valueOf(aFiller1),
             String.valueOf(aFiller2),
             String.valueOf(aFiller3),
             String.valueOf(aFiller4),
             String.valueOf(aFiller5));
  }
  
} // Printf
