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
import com.tek271.util.*;
import com.tek271.util.collections.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.collections.set.*;
import com.tek271.util.string.StringUtility;

/**

 * A map where both keys and values are strings. When you construct
 * an object of this class you can specify if you want it to be case sensetive or not.
 * Using the case insensetive option will not change the case of either keys or values.
 * Additionally, using the setListOrder() method you can have the class keep track of
 * the order in which items where added to the map.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class MapOfString extends HashMap {
  private static final long serialVersionUID = 1;
  protected boolean pIsCaseSensetive;
  protected boolean pIsListOrder= false;
  private boolean pIsRemovingValue= false;
  private NotifyingSet pEntrySet;
  private NotifyingSet pKeySet;
  private NotifyingCollection pValues;
  protected ListOfString pOrderedKeys;

  public MapOfString() {
    pIsCaseSensetive= true;
  }


/**
 * Create a MapOfString object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 */
  public MapOfString(final boolean aIsCaseSensetive) {
    pIsCaseSensetive= aIsCaseSensetive;
  }

/**
 * Create a MapOfString object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aInitialCapacity initial capacity and the default load factor (0.75).
 */
  public MapOfString(final boolean aIsCaseSensetive,
                     final int aInitialCapacity) {
    super(aInitialCapacity);
    pIsCaseSensetive= aIsCaseSensetive;
  }

/**
 * Create a MapOfString object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aInitialCapacity initial capacity and the default load factor (0.75).
 * @param aLoadFactor specified load factor
 */
  public MapOfString(final boolean aIsCaseSensetive,
                     final int aInitialCapacity,
                     final float aLoadFactor) {
    super(aInitialCapacity, aLoadFactor);
    pIsCaseSensetive= aIsCaseSensetive;
  }

/**
 * Create a MapOfString object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aMap a Map whose keys/value will be copied to this new map. aMap must contain
 * String keys and values.
 * <p>Abdul Habra: 2005.09.01 - Corrected to use putAll() insted of super()</p>
 */
  public MapOfString(final boolean aIsCaseSensetive,
                     final Map aMap) {
    pIsCaseSensetive= aIsCaseSensetive;
    putAll(aMap);
  }

/**
 * Create a MapOfString object.
 * @param aIsCaseSensetive When the case is not sensetive, you can find items with a key
 * regardless of its case. E.g. get("name") will be similar to get("nAMe")
 * @param aListOfString Copy the name/values pairs in the list into this map as key/value
 * pairs. Items in the list that do not have name/value pairs will be ignored.
 */
  public MapOfString(final boolean aIsCaseSensetive,
                     final ListOfString aListOfString) {
    pIsCaseSensetive= aIsCaseSensetive;
    putAll(aListOfString);
  }

/** Check if this object was constructed with the case sensetivity flag on or off. */
  public boolean isCaseSensetive() {
    return pIsCaseSensetive;
  }

  private final static String pERR_NOT_EMPTY=
      "MapOfString must be empty when you call setListOrder(true).";

/**
 * Instruct this object to keep track of the order in which (key,value) where put in
 * the map. Call getOrderedKeys() to get the keys ordered as they where added to the
 * map. This method must be called when th map size is ZERO, otherwise an exception
 * is thrown.<p>
 * Note that when you keep track of the order, there is some performance and memory
 * overhead beacuse the keys are stored into a list.
 * @param aIsListOrder boolean true means keep track of the order.
 * @throws IllegalStateException if this map has a size > 0 when this method is called.
 */
  public void setListOrder(final boolean aIsListOrder) {
    pIsListOrder= aIsListOrder;
    if (! aIsListOrder) {
      pIsListOrder= false;
      pOrderedKeys= null;
      return;
    }
    if (size() >0) {
      throw new IllegalStateException(pERR_NOT_EMPTY);
    }
    pOrderedKeys= new ListOfString();
    pOrderedKeys.isCaseSensitive= pIsCaseSensetive;
  }  // setListOrder();

/** Check if this object is to keep track of entries order or not */
  public boolean isListOrder() {
    return pIsListOrder;
  }

/**
 * Get a list of the keys in this map ordered as they where added to the map. This list
 * is half backed by the map; deleting entries frim the map will delete them from the
 * list, but deleting items from this list will NOT delete them from the map.
 * @return ListOfString List of keys. Will return null if this map does not maintain
 * the order of the keys, in other words if getListOrder() returns false, this
 * method will return null.
 */
  public ListOfString getOrderedKeys() {
    return pOrderedKeys;
  }

  protected String caseIt(final String aString) {
    return pIsCaseSensetive? aString : aString.toLowerCase();
  }

  protected Object getMapValue(final String aKey) {
    String k= caseIt(aKey);
    return super.get(k);
  }

/** Get the value of the given key as a string*/
  public String get(final String aKey) {
    MapValue v= (MapValue) getMapValue(aKey);
    if (v==null) return null;
    return v.getValueAsString();
  }

/** Get the value of the given key as an Object that is a String */
  public Object get(final Object aKey) {
    return get((String) aKey);
  }

  protected Object putMapValue(final String aKey, final MapValue aMapValue) {
    return super.put(aKey, aMapValue);
  }

  protected void addToBackers(final String aKey,
                              final MapValue aMapValue,
                              final String aStringValue) {
    if (pEntrySet!=null) pEntrySet.add(aMapValue);
    if (pKeySet!=null) pKeySet.add(aKey);
    if (pValues!=null) pValues.add(aStringValue);
  }

/**
 * Put a new value into the map.
 * @param aKey String must be unique. If the map contains this key, the new value will
 * override the old value.
 * @param aValue String
 * @return String The ols value that was associated with this key if any.
 */
  public String put(final String aKey, final String aValue) {
    if ( pIsListOrder && !containsKey(aKey) ) {
      pOrderedKeys.add(aKey);
    }

    String k= caseIt(aKey);
    MapValue v= new MapValue(pIsCaseSensetive, aKey, aValue);
    addToBackers(k, v, aValue);
    v= (MapValue) putMapValue(k, v);
    if (v==null) return null;
    return v.getValueAsString();
  }  // putString

/** Put a key/value into the map */
  public Object put(final Object aKey, final Object aValue) {
    return put( (String)aKey, (String)aValue);
  }

/**
 * Put an entry into the map where the entry is given as a single string formatted as
 * key=value.
 * @param aKeyEqualValue String The new entry in the format key=value.
 * @param aEquator String You can change what is considered an equal sign. For example
 * if you use key:=value, use an aEquator of :=<br>
 * If you pass a null into this parameter, the = sign will be used.
 * @return String The ols value that was associated with this key if any.
 */
  public String putItem(final String aKeyEqualValue, final String aEquator) {
    if (StringUtility.isBlank(aKeyEqualValue)) return null;
    String eq= aEquator==null? StringUtility.EQUAL : aEquator;

    int i= aKeyEqualValue.indexOf(eq);
    if (i<1) return null;   // equal cannot be first char

    String k= StringUtility.left(aKeyEqualValue, i);
    String v= StringUtility.substring(aKeyEqualValue, i + eq.length());
    return put(k, v);
  }  // putItem

/**
 * Put an entry into the map where the entry is given as a single string formatted as
 * key=value.
 * @param aKeyEqualValue String The new entry in the format key=value.
 * @return String The ols value that was associated with this key if any.
 */
  public String put(final String aKeyEqualValue) {
    return putItem(aKeyEqualValue, null);
  }

/**
 * Read items from the given list that are formatted as name=value and put them into
 * this map as key/value entries.
 * @param aListOfString ListOfString If the list contains items that are not formatted
 * appropriately, these items will be ignored.
 */
  public void putAll(final ListOfString aListOfString) {
    if (aListOfString==null) return;
    String eq= aListOfString.valueEquator;
    for (int i=0, n=aListOfString.size(); i<n; i++) {
      putItem(aListOfString.getItem(i), eq);
    }
  }  // putAll

/**
 * Check if the map contains this key taking the case sensetivity flag into account.
 * @param aKey String Key to check for.
 * @return boolean true if found, false if not.
 */
  public boolean containsKey(String aKey) {
    String k= caseIt(aKey);
    return super.containsKey(k);
  }

/**
 * Check if the map contains this key taking the case sensetivity flag into account.
 * @param aKey String Key to check for.
 * @return boolean true if found, false if not.
 */
  public boolean containsKey(Object aKey) {
    return containsKey((String) aKey);
  }

/**
 * Check if the map contains this value taking the case sensetivity flag into account.
 * @param aValue String Value to find.
 * @return boolean true if found, false if not.
 */
  public boolean containsValue(final String aValue) {
    Set set= super.entrySet();
    for (Iterator i= set.iterator(); i.hasNext(); ) {
      Entry e= (Entry) i.next();
      MapValue v= (MapValue) e.getValue();
      if (v.isEqualValue(aValue)) return true;
    }
    return false;
  }

/**
 * Check if the map contains this value taking the case sensetivity flag into account.
 * @param aValue String Value to find.
 * @return boolean true if found, false if not.
 */
  public boolean containsValue(final Object aValue) {
    return containsValue((String) aValue);
  }


/**
 * <i>The following description is copied verbatim from Sun's documentation of the
 * HashMap: </i>
 * Returns a collection view of the values contained in this map. The collection is
 * backed by the map, so changes to the map are reflected in the collection, and
 * vice-versa. The collection supports element removal, which removes the
 * corresponding mapping from this map, via the Iterator.remove, Collection.remove,
 * removeAll, retainAll, and clear operations. It does not support the add or
 * addAll operations.
 * @return Set a collection view of the mappings contained in this map.
 * <p> <i>Additional docs: </i>
 * The returned set will also maintain the case sensetivity mode defined when this
 * map was created.
 */
  public Set entrySet() {
    if (pEntrySet != null) return pEntrySet;

    Set s= super.entrySet();
    Set r= new HashSet(s.size());
    for (Iterator i= s.iterator(); i.hasNext();) {
      Entry e= (Entry) i.next();
      MapValue v= (MapValue) e.getValue();  // Note that Value implements Map.Entry
      r.add(v);
    }
    pEntrySet= new NotifyingSet( r, new EntrySetAdd(), new EntrySetRemove() );
    return pEntrySet;
  }  // entrySet

/** Refuse to allow caller to add to the entries set */
  private static class EntrySetAdd implements ICallback {
    public Object call(final Object o) {
      return null;  // cannot add to entry set, per Set specs.
    }
  }

/** If an entry is removed from the entrySet, remove it from this map as well */
  private class EntrySetRemove implements ICallback {
    public Object call(final Object aEntry) {
      MapValue v= (MapValue) aEntry;
      String k= caseIt(v.orgKey);
      remove(k, false, true, true);
      return this;
    }
  }

/**
 * <i>The following description is copied verbatim from Sun's documentation of the
 * HashMap: </i>
 * Returns a set view of the keys contained in this map. The set is backed by the map,
 * so changes to the map are reflected in the set, and vice-versa. The set supports
 * element removal, which removes the corresponding mapping from this map, via the
 * Iterator.remove, Set.remove, removeAll, retainAll, and clear operations. It does not
 * support the add or addAll operations.
 * @return Set a set view of the keys contained in this map.
 * <p> <i>Additional docs: </i>
 * The returned set will also maintain the case sensetivity mode defined when this
 * map was created.
 */
  public Set keySet() {
    if (pKeySet != null) return pKeySet;

    Set s= super.entrySet();
    Set r= new HashSet(s.size());
    for (Iterator i= s.iterator(); i.hasNext();) {
      Entry e= (Entry) i.next();
      MapValue v= (MapValue) e.getValue();  // Note that Value implements Map.Entry
      r.add(v.orgKey);
    }
    pKeySet= new NotifyingSet(r, new KeySetAdd(), new KeySetRemove());
    return pKeySet;
  }  // keySet

/** Refuse to allow caller to add to the keys set */
  private static class KeySetAdd implements ICallback {
    public Object call(final Object o) {
      return null;  // cannot add to entry set, per Set specs.
    }
  }

/** If a key is removed from the keySet, remove it from this map as well */
  private class KeySetRemove implements ICallback {
    public Object call(final Object aKey) {
      remove((String) aKey, true, false, true);
      return this;
    }
  }

  private String remove(final String aKey,
                        final boolean aIsRemoveEntry,
                        final boolean aIsRemoveKey,
                        final boolean aIsRemoveValue) {
    if (pIsListOrder) pOrderedKeys.remove(aKey);

    String k= caseIt(aKey);
    MapValue v= (MapValue) super.remove(k);
    if (aIsRemoveKey && pKeySet!=null) pKeySet.remove(k, false);
    if (v==null) return null;

    if (aIsRemoveEntry && pEntrySet!=null) pEntrySet.remove(v, false);
    if (!pIsRemovingValue) {
      if (aIsRemoveValue && pValues!=null) pValues.remove(v.value, false);
    }
    return v.value;
  }

/**
 * Remove the entry with the given key.
 * @param aKey String
 * @return String the value of the removed key if found.
 */
  public String remove(String aKey) {
    return remove(aKey, true, true, true);
  }

/**
 * Remove the entry with the given key.
 * @param aKey Object
 * @return Object the value of the removed key if found.
 */
  public Object remove(Object aKey) {
    return remove((String) aKey);
  }

  private void nullBackers() {
    pEntrySet=null;
    pKeySet= null;
    pValues= null;
  }

/** Clear the contents of this map */
  public void clear() {
    super.clear();
    if (pEntrySet != null)     pEntrySet.clear();
    if (pKeySet != null)       pKeySet.clear();
    if (pValues != null)       pValues.clear();
    nullBackers();
  }  // clear

/** A collection of the values that exist in this map */
  public Collection values() {
    if (pValues!=null) return pValues;

    Collection c= super.values();
    List r= new ArrayList(c.size());
    for (Iterator i= c.iterator(); i.hasNext(); ) {
      MapValue v= (MapValue) i.next();
      r.add( v.getValue() );
    }
    pValues= new NotifyingCollection(r, new ValuesAdd(), new ValuesRemove() );
    return pValues;
  }  // values

/** Refuse to allow caller to add to the values collection */
  private static class ValuesAdd implements ICallback {
    public Object call(final Object o) {
      return null;  // cannot add to entry set, per Set specs.
    }
  }

/**
 * If a value is removed from the values collection, remove it from this map as well.
 * This will remove all entries with this value
 */
  private class ValuesRemove implements ICallback {
    public Object call(final Object aValueToRemove) {
      Set set= entrySet();
      String vs= (String) aValueToRemove;
      for (Iterator i= set.iterator(); i.hasNext(); ) {
        MapValue v= (MapValue) i.next();
        if (v.isEqualValue(vs)) {
          pIsRemovingValue= true;
          i.remove();
          pIsRemovingValue= false;
        }
      }
      return this;
    }
  }  // class ValuesRemove

  public ListOfString toListOfString() {
    ListOfString r= new ListOfString();
    if (isListOrder()) {
      for (int i=0, n= pOrderedKeys.size(); i<n; i++) {
        String k= pOrderedKeys.getItem(i);
        String v= get(k);
        r.add(k, v);
      }
      return r;
    }

    Set es= entrySet();
    for (Iterator i= es.iterator(); i.hasNext();) {
      MapValue mv= (MapValue) i.next();
      r.add(mv.orgKey, mv.value );
    }
    return r;
  }  // toListOfString()

/** for testing */
  public static void main(String[] args) {
    MapOfString ms= new MapOfString(false);
    ms.setListOrder(true);
    ms.put("Carter", "39");
    ms.put("Reagan=40");
    ms.put("bush1", "41");
    ms.put("clinton", "42");
    ms.put("bush2=43");
    ms.put("bush2=43");

    System.out.println( ms.toListOfString().toString() );

    System.out.println(ms.toString());
    Collection v= ms.values();
    Set k= ms.keySet();
    Set e= ms.entrySet();
    ListOfString ok= ms.getOrderedKeys();

    ms.remove("reagan");
//    v.remove("40");
//    k.remove("bush1");
//    ms.clear();

    System.out.println(ms.containsKey("cArTeR"));

    System.out.println();
    if (ok!=null)
      System.out.println("Ordered Keys: " + ok.getText(", "));
    System.out.println("Entries:" + e.toString());
    System.out.println("Values: " + v.toString() );
    System.out.println("Keys:   " + k.toString() );
    System.out.println("Map:    " + ms.toString());
  }


}  // MapOfString
