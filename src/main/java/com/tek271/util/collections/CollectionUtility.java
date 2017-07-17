/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2006  Abdul Habra
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
package com.tek271.util.collections;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * Different collections utility methods. Extends the Jakarta CollectionUtils class.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class CollectionUtility extends CollectionUtils {

/** Check if the given index is a valid index for the given collection */
  public static boolean isValidIndex(final Collection aCollection, final int aIndex) {
    if (aCollection==null) return false;
    if (aIndex<0) return false;
    if (aIndex >= aCollection.size()) return false;
    return true;
  }

/** Check if the given index is a not valid index for the given collection */
  public static boolean isNotValidIndex(final Collection aCollection, final int aIndex) {
    return ! isValidIndex(aCollection, aIndex);
  }

/** Get the size of the collection, if aCollection is null return zero */
  public static int size(final Collection aCollection) {
    if (aCollection==null) return 0;
    return aCollection.size();
  }

/** Checks if the given collection is null or has no elements */
  public static boolean isNullOrEmpty(final Collection aCollection) {
    return size(aCollection)==0;
  }
  
/** Add the given array to the given collection */  
  public static void addAll(final Collection aCollection,
                            final Object[] aArray) {
    for (int i=0, n=aArray.length; i<n; i++) {
      aCollection.add(aArray[i]);
    }
  }
  
}  // CollectionUtility
