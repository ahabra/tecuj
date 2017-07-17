/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2007  Abdul Habra
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

/**
 * A map with a maximum size and time-to-live for its entries. When trying to add to
 * the map which reached its max size, the Least Recently Used (LRU) item is removed.
 * Items that have been in the map for a period greater than the time-to-live period
 * will be removed when trying to use any map-related method, in-other-words, methods
 * of the Object class do not cause the removal of expired entries.
 * This class extends java.utils.LinkedHashMap with accessOrder feature.
 * <p>Copyright (c) 2007 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class TimedLruMap extends LinkedHashMap {
  private static final int pDEFAULT_INITIAL_CAPACITY= 16;
  private static final float pDEFAULT_LOAD_FACTOR= 0.75f;
  private static final boolean pDEFAULT_LRU_ORDER= true;

  private long pTimeToLive;
  private int pMaxSize;
  private OrderedMapOfTimestamp pTimeStamps= new OrderedMapOfTimestamp();


  /**
   * Create a TimedLruMap with a default initial capacity of 16 and load factor of 0.75.
   * @param timeToLive long The time to live in milli seconds. This is the duration in
   * which entries can stay in this map before they expire.
   * @param maxSize int Maximum size of the LRU entries.
   */
  public TimedLruMap(final long timeToLive, final int maxSize) {
    super(pDEFAULT_INITIAL_CAPACITY, pDEFAULT_LOAD_FACTOR, pDEFAULT_LRU_ORDER);
    initThis(timeToLive, maxSize);
  }

  /**
   * Create a TimedLruMap with a default load factor of 0.75.
   * @param timeToLive long The time to live in milli seconds. This is the duration in
   * which entries can stay in this map before they expire.
   * @param maxSize int Maximum size of the LRU entries.
   * @param initialCapacity int The initial capacity of the map.
   */
  public TimedLruMap(final long timeToLive, final int maxSize, int initialCapacity) {
    super(initialCapacity, pDEFAULT_LOAD_FACTOR, pDEFAULT_LRU_ORDER);
    initThis(timeToLive, maxSize);
  }

  /**
   * Create a TimedLruMap
   * @param timeToLive long The time to live in milli seconds. This is the duration in
   * which entries can stay in this map before they expire.
   * @param maxSize int Maximum size of the LRU entries.
   * @param initialCapacity int The initial capacity of the map.
   * @param loadFactor float the load factor of the map.
   */
  public TimedLruMap(final long timeToLive, final int maxSize, int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor, pDEFAULT_LRU_ORDER);
    initThis(timeToLive, maxSize);
  }

  /**
   * Create a TimedLruMap with the same mappings as the given map.
   * The TimedLruMap instance is created with a a default load factor (0.75) and an
   * initial capacity sufficient to hold the mappings in the specified map.
   * @param timeToLive long The time to live in milli seconds. This is the duration in
   * which entries can stay in this map before they expire.
   * @param maxSize int Maximum size of the LRU entries.
   * @param map Map the map whose mappings are to be placed in this map.
   */
  public TimedLruMap(final long timeToLive, final int maxSize, Map map) {
    super(map);
    initThis(timeToLive, maxSize);
  }

  private void initThis(final long timeToLive, final int maxSize) {
    pTimeToLive= timeToLive;
    pMaxSize= maxSize;
  }

/** Returns true if this map should remove its eldest entry. */
  protected boolean removeEldestEntry(final Map.Entry eldest) {
    removeExpired();
    return size() > pMaxSize;
  }

  public long getTimeToLive() {
    return pTimeToLive;
  }

  public int getMaxSize() {
    return pMaxSize;
  }

  private void touchTimeStamp(final Object key) {
    pTimeStamps.put(key);
  }

  private boolean isExpired(final long now, final long timeStamp) {
    return (now-timeStamp) > pTimeToLive;
  }

/** Remove expired entries from the map */
  public int removeExpired() {
    int removedCount=0;
    long now= System.currentTimeMillis();
    while (! pTimeStamps.isEmpty()) {
      if (! isExpired(now, pTimeStamps.getFirstValue()) ) break;
      Object key= pTimeStamps.firstKey();
      pTimeStamps.removeFirst();
      super.remove(key);
      removedCount++;
    }
    return removedCount;
  }

  public Object put(final Object key, final Object value) {
    removeExpired();
    touchTimeStamp(key);
    return super.put(key, value);
  }

  public Object get(final Object key) {
    removeExpired();
    touchTimeStamp(key);
    return super.get(key);
  }

  public Object remove(Object key) {
    pTimeStamps.remove(key);
    return super.remove(key);
  }

  public void clear() {
    removeExpired();
  }

  public Object clone() {
    removeExpired();
    return super.clone();
  }

  public boolean containsKey(Object key) {
    removeExpired();
    return super.containsKey(key);
  }

  public boolean containsValue(Object value) {
    removeExpired();
    return super.containsValue(value);
  }

  public Set entrySet() {
    removeExpired();
    return super.entrySet();
  }

  public boolean isEmpty() {
    removeExpired();
    return super.isEmpty();
  }

  public Set keySet() {
    removeExpired();
    return super.keySet();
  }

  public void putAll(Map map) {
    removeExpired();
    super.putAll(map);
  }

  public int size() {
    removeExpired();
    return super.size();
  }

  public Collection values() {
    removeExpired();
    return super.values();
  }

  public boolean equals(Object o) {
    removeExpired();
    return super.equals(o);
  }

  public int hashCode() {
    removeExpired();
    return super.hashCode();
  }

  public String toString() {
    removeExpired();
    return super.toString();
  }


}
