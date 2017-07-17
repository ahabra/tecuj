/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2006  Abdul Habra
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

package com.tek271.util.collections.map;

import java.util.*;
import com.tek271.util.collections.array.ArrayUtilities;
import org.apache.commons.collections4.MapUtils;

/**
 * Static utility map-related methods.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class MapUtility {

  /**
   * Create a map whose keys are the given keys array, and values are the given
   * values array.
   */
  public static Map toMap(final Object[] aKeys, final Object[] aValues) {
    if (aKeys==null)  return Collections.EMPTY_MAP;
    int n= aKeys.length;
    if (n==0) return Collections.EMPTY_MAP;
    int valuesLength= ArrayUtilities.length(aValues);

    Map map= new HashMap(n);
    for(int i=0; i<n; i++) {
      Object key= aKeys[i];
      Object v= i<valuesLength ? aValues[i] : null;
      map.put(key, v );
    }
    return map;
  }

/**
 * Creates a map from the given 2-dimensional array.
 * @param aKeyValues a 2-dimensional array, aKeyValues[i][0] is a key to aKeyValues[i][1].
 * <ol>
 * <li>if aKeyValues[i] has more than two elements, the extra elements will be ignored.
 * <li>if aKeyValues[i] is null or zero-length, it will be ignored.
 * <li>if aKeyValues[i] has one element only, it will be treated as a key to a map element
 * with a map value of null.
 * @return Map
 */
  public static Map toMap(final Object[][] aKeyValues) {
    if (aKeyValues==null) return Collections.EMPTY_MAP;
    int n= aKeyValues.length;
    if (n==0) return Collections.EMPTY_MAP;

    Map map= new HashMap(n);
    for(int i=0; i<n; i++) {
      Object[] kv= aKeyValues[i];
      int kvLen= ArrayUtilities.length(kv);
      if (kvLen <= 0) continue;

      Object key= kv[0];
      Object v= kvLen > 1 ? kv[1] : null;
      map.put(key, v );
    }
    return map;

  }

}
