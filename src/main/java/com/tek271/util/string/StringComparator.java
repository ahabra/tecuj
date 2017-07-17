/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2006  Abdul Habra.
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

import java.util.Comparator;

/**
 * Implement the Comparator and Comparable interfaces for String objects, with support
 * for case sensitivity.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class StringComparator implements Comparator, Comparable {
  public static final StringComparator CASE_SENSETIVE= new StringComparator(true);
  public static final StringComparator CASE_NOT_SENSETIVE= new StringComparator(false);

  private boolean pIsCaseSensetive= true;

  public StringComparator(final boolean aIsCaseSensetive) {
    pIsCaseSensetive= aIsCaseSensetive;
  }

/**
 * Implement java.util.Comparator.compare() method,
 * compares its two arguments for order.
 * @param aObj1 the first object to be compared.
 * @param aObj2 the second object to be compared.
 * @return a negative integer, zero, or a positive integer as the first argument is
 *   less than, equal to, or greater than the second.
 * aObj1 = aObj2  => 0
 * aObj1 < aObj2  => -
 * aObj1 > aObj2  => +
 */
  public int compare(final Object aObj1, final Object aObj2) {
    if (aObj1==aObj2)   return 0;
    // now we know that both cannot be null
    // check if either is null
    if (aObj1==null) return -1;
    if (aObj2==null) return 1;

    // now, neither is null
    String s1= aObj1.toString();
    String s2= aObj2.toString();

    if (pIsCaseSensetive) return s1.compareTo(s2);
    return s1.compareToIgnoreCase(s2);
  }

/**
 * Implement java.util.Comparator.equals() method,
 * indicates whether some other object is &quot;equal to&quot; this Comparator.
 * @param aObj the reference object with which to compare.
 * @return <code>true</code> only if the specified object is also a comparator and it
 *   imposes the same ordering as this comparator.
 */
  public boolean equals(final Object aObj) {
    return compare(this, aObj)==0;
  }

/**
 * Implement java.ang.Comparable.compareTo() method,
 * compares this object with the specified object for order.
 * @param aObj the Object to be compared.
 * @return a negative integer, zero, or a positive integer as this object is less
 *   than, equal to, or greater than the specified object.
 */
  public int compareTo(final Object aObj) {
    return compare(this, aObj);
  }

  public int hashCode() {
    return pIsCaseSensetive? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
  }
  
}  // StringComparator
