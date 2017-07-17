/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2008  Abdul Habra.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class WordUtility extends WordUtils {

/**
 * Convert a CamelCase String into words, e.g. CamelCase -> Camel Case.
 * @param camelCase
 * @return the input string separated into words
 */
  public static String camelcaseToWords(String camelCase) {
    if (StringUtils.isBlank(camelCase)) return camelCase;
    
    char[] in= camelCase.toCharArray();
    int length= camelCase.length();
    StringBuffer buf= new StringBuffer(length + 16);
    for (int i=0; i<length; i++) {
      char current= in[i];
      if (isAddSpace(current, buf)) buf.append(' ');
      buf.append(current);
    }
    return buf.toString();
  }
  
  private static boolean isAddSpace(char current, StringBuffer buf) {
    if (isCurrentSimilarToLast(current, buf)) return false;
    if (Character.isUpperCase(current)) return true;
    return false;
  }
  
  private static boolean isCurrentSimilarToLast(char current, StringBuffer buf) {
    int size= buf.length();
    if (size==0) return true;
    
    char last= buf.charAt(size-1);
    return isSimilarCase(current, last);
  }

  public static boolean isSimilarCase(char ch1, char ch2) {
    if (ch1==ch2) return true;
    if (Character.isLowerCase(ch1) && Character.isLowerCase(ch2)) return true;
    if (Character.isUpperCase(ch1) && Character.isUpperCase(ch2)) return true;
    
    return false;
  }
  
}
