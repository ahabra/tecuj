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
package com.tek271.util.collections.map;

import java.util.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
 * A map of string which can have multiple values for the same key.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class MapOfStringMV extends MapOfString {
/** The string used to separate values for multivalue strings, the defualt is comma */
  public String valueSeparator= StringUtility.COMMA;

  public MapOfStringMV() {
    super();
  }


/**
 * Create a MapOfStringMV object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 */
  public MapOfStringMV(final boolean aIsCaseSensetive) {
    super(aIsCaseSensetive);
  }

/**
 * Create a MapOfStringMV object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aInitialCapacity initial capacity and the default load factor (0.75).
 */
  public MapOfStringMV(final boolean aIsCaseSensetive,
                       final int aInitialCapacity) {
    super(aIsCaseSensetive, aInitialCapacity);
  }

/**
 * Create a MapOfStringMV object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aInitialCapacity initial capacity and the default load factor (0.75).
 * @param aLoadFactor specified load factor
 */
  public MapOfStringMV(final boolean aIsCaseSensetive,
                       final int aInitialCapacity,
                       final float aLoadFactor) {
    super(aIsCaseSensetive, aInitialCapacity, aLoadFactor);
  }

/**
 * Create a MapOfStringMV object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aMap a Map whose keys/value will be copied to this new map. aMap must contain
 * String keys and values.
 */
  public MapOfStringMV(final boolean aIsCaseSensetive,
                       final Map aMap) {
    super(aIsCaseSensetive, aMap);
  }

/**
 * Create a MapOfStringMV object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aListOfString Copy the name/values pairs in the list into this map as key/value
 * pairs. Items in the list that do not have name/value pairs will be ignored.
 */
  public MapOfStringMV(final boolean aIsCaseSensetive,
                       final ListOfString aListOfString) {
    super(aIsCaseSensetive, aListOfString);
  }


/**
 * Put the given name/valueList pair into this map.
 * @param aKey String
 * @param aValueList ListOfString Multiple values
 * @param aIsAppend boolean Should append values if key alread exist, or replace.
 * @return ListOfString The old value of the added key.
 */
  public ListOfString put(final String aKey,
                          final ListOfString aValueList,
                          final boolean aIsAppend) {
    boolean isNewKey= !containsKey(aKey);
    if ( pIsListOrder && isNewKey ) pOrderedKeys.add(aKey);

    if (isNewKey) {
      String k=caseIt(aKey);
      MapValueMV v=new MapValueMV(pIsCaseSensetive, aKey, aValueList, valueSeparator);
      addToBackers(k, v, aValueList.toString());
      putMapValue(k, v);
      return null;
    }

    // an existing key, append to its value
    MapValueMV v= (MapValueMV) getMapValue(aKey);
    if (aIsAppend)     v.appendValue(aValueList);
    else v.setValueList(aValueList);
    return v.getValueList();
  }  // put

/**
 * Put the given name/valueList pair into this map. If the key already exists, append
 * the given values.
 * @param aKey String
 * @param aValueList ListOfString Multiple values
 * @return ListOfString The old value of the added key
 */
  public ListOfString put(final String aKey, final ListOfString aValueList) {
    return put(aKey, aValueList, true);
  }  // put

/**
 * Put the given name/valueList pair into this map.
 * @param aKey String
 * @param aValueList String a list of values separated by <i>valueSeparator</i>
 * @param aIsAppend boolean Should append values if key alread exist, or replace.
 * @return String the old value of the key
 */
  public String put(final String aKey,
                    final String aValueList,
                    final boolean aIsAppend) {
    ListOfString list= new ListOfString(aValueList, valueSeparator);
    ListOfString r= put(aKey, list, aIsAppend);
    if (r==null) return null;
    return r.toString();
  }

/**
 * Put the given name/valueList pair into this map. If the key already exists, append
 * the given values.
 * @param aKey String
 * @param aValueList String a list of values separated by <i>valueSeparator</i>
 * @return String the old value of the key
 */
  public String put(final String aKey, final String aValueList) {
    return put(aKey, aValueList, true);
  }  // put

/**
 * Put an item in the format name=multivalue to the map.
 * @param aKeyEqualValue String A string formatted as name=multivalue. The multivalue
 * is separated by <i>valueSeparator</i>.
 * @param aEquator String The string used for equal, usually =
 * @param aIsAppend boolean Should append values if key alread exist, or replace.
 * @return String the old value of the key
 */
  public String putItem(final String aKeyEqualValue,
                        final String aEquator,
                        final boolean aIsAppend) {
    if (StringUtility.isBlank(aKeyEqualValue)) return null;
    String eq= aEquator==null? StringUtility.EQUAL : aEquator;

    int i= aKeyEqualValue.indexOf(eq);
    if (i<1) return null;   // equal cannot be first char

    String k= StringUtility.left(aKeyEqualValue, i);
    String v= StringUtility.substring(aKeyEqualValue, i + eq.length());
    return put(k, v, aIsAppend);
  }

/**
 * Put an item in the format name=multivalue to the map.
 * @param aKeyEqualValue String A string formatted as name=multivalue. The multivalue
 * is separated by <i>valueSeparator</i>.
 * @param aIsAppend boolean Should append values if key alread exist, or replace.
 * @return String the old value of the key
 */
  public String put(final String aKeyEqualValue,
                    final boolean aIsAppend) {
    return putItem(aKeyEqualValue, null, aIsAppend);
  }

/** Get the value for the given key as a ListOfString, null if not found */
  public ListOfString getAsList(final String aKey) {
    MapValueMV v= (MapValueMV) getMapValue(aKey);
    if (v==null) return null;
    return v.getValueList();
  }  // getAsList()

  /** for testing */
    public static void main(String[] args) {
      MapOfStringMV ms= new MapOfStringMV();
      ms.valueSeparator= ":";
      ms.setListOrder(true);
      ms.put("Carter", "39");
      ms.put("Reagan=40");
      ms.put("bush=41:43");
      ms.put("clinton", "42");
      ms.put("bush=50:51:52", false);  // :)

      System.out.println(ms.toString());

      ListOfString b= ms.getAsList("bush");
      System.out.println(b.size() + " " + b.toString());
      System.out.println("Map:    " + ms.toString());
    }

}  // MapOfStringMV
