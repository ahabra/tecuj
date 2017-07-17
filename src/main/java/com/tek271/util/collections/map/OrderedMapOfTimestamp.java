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
import org.apache.commons.collections.map.LinkedMap;

/**
 * A map whose values are the time stamp of addition to the map. The elements in the
 * map are ordered by their insertion order.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class OrderedMapOfTimestamp extends LinkedMap {
  private static final String pERR_SMALL_VALUE=
                       "value cannot be less than the last value inserted in this Map";

  public OrderedMapOfTimestamp() {
    super();
  }

  public OrderedMapOfTimestamp(int initialCapacity) {
    super(initialCapacity);
  }

  public OrderedMapOfTimestamp(int initialCapacity, float aLoadFactor) {
    super(initialCapacity, aLoadFactor);
  }

  public OrderedMapOfTimestamp(Map map) {
    super(map);
  }

  /**
   * Put a key/value pair to the map.
   * @param key Object
   * @param value Cannot be null. This value cannot be less than the value of the last
   * added item in the map.
   * @return Object previous value associated with specified key, or null if there was
   * no mapping for key
   */
  public Object putLong(Object key, Long value) {
    if (value==null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    long val= value.longValue();
    if (val<getLastValue()) {
      throw new IllegalArgumentException(pERR_SMALL_VALUE);
    }

    if (containsKey(key)) {
      // make the added key, the last item in the map
      Object oldVal= remove(key);
      super.put(key, value);
      return oldVal;
    }
    return super.put(key, value);
  }

  /**
   * Put a key/value pair to the map.
   * @param key Object
   * @param value This value cannot be less than the value of the last added item in the map.
   * @return Object previous value associated with specified key, or null if there was
   * no mapping for key
   */
  public Object putLong(Object key, long value) {
    Long val= new Long(value);
    return putLong(key, val);
  }

  /**
   * Put a key/value pair to the map.
   * @param key Object
   * @param value A time stamp value of type Long. Cannot be null. This value cannot be
   * less than the value of the last added item in the map.
   * @return Object previous value associated with specified key, or null if there was
   * no mapping for key
   */
  public Object put(Object key, Object value) {
    if (! (value instanceof Long)) {
      throw new IllegalArgumentException("value should be of Long type");
    }
    return putLong(key, (Long) value);
  }

  /**
   * Put a key/value pair to the map, where the value is System.currentTimeMillis()
   * @param key Object
   * @return Object previous value associated with specified key, or null if there was
   * no mapping for key
   */
  public Object put(Object key) {
    return this.putLong(key, System.currentTimeMillis());
  }

  /**
   * Get the first value that was added to the map.
   * @return zero if the map is empty.
   */
  public long getFirstValue() {
    if (isEmpty()) return 0;
    Long val= (Long) getValue(0);
    return val.longValue();
  }

  /**
   * Get the last added value to the map.
   * @return zero if the map is empty.
   */
  public long getLastValue() {
    if (isEmpty()) return 0;
    Long val= (Long) get( lastKey() );
    return val.longValue();
  }

  /**
   * Remove the first item in the map
   * @return long The value of the removed item. Returns zero if map is empty.
   */
  public long removeFirst() {
    if (isEmpty()) return 0;
    Object first= remove(0);
    return ((Long)first).longValue();
  }

  /**
   * Remove the last item in the map
   * @return long The value of the removed item. Returns zero if map is empty.
   */
  public long removeLast() {
    if (isEmpty()) return 0;
    Object last= remove(lastKey());
    return ((Long)last).longValue();
  }

  /**
   * Remove all items in the map whose time stamp is before the given time stamp.
   * @param timeStamp long Timestamp in milli seconds.
   * @param including when true, items with timestamp equal to the given
   * timestamp will be removed.
   */
  public void removeBefore(final long timeStamp, final boolean including) {
    while (size() > 0) {
      long val= getFirstValue();
      if (val > timeStamp) break;
      if (!including && val==timeStamp) break;
      remove(0);
    }
  }

  /**
   * Remove all items the map whose time stamp is after the given time stamp.
   * @param timeStamp long Timestamp in milli seconds.
   * @param including when true, items with timestamp equal to the given
   * timestamp will be removed.
   */
  public void removeAfter(final long timeStamp, final boolean including) {
    while (size() > 0) {
      long val= getLastValue();
      if (val<timeStamp) break;
      if (!including && val==timeStamp) break;
      removeLast();
    }
  }

}
