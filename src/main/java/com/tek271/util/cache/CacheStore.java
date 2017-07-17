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

package com.tek271.util.cache;

import java.util.*;
import com.tek271.util.cache.key.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.collections.map.TimedLruMap;

/**
 * <p>Encapsulates a caches store with a LRU and TTL removal policy, all methods of this
 * class are <b>synchronized</b>.
 * A store is uniquly identified by its name.
 * Each store has a maximum size of items and a Time-To-Live (TTL) in seconds for its items.
 * <p>To create a cache store use the StoreFactory.createStore() static method.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
class CacheStore implements ICacheStore {
  private static final String pNL= StringUtility.NEW_LINE;
  private TimedLruMap pMap;
  private String pUser;
  private String pStoreName;
  private int pTtlSeconds;
  private boolean pIsClosing;

  private long pAttemptCount;
  private long pFoundCount;

/**
 * Create a new cache store, note that this has a default package scope, use the
 * StoreFactory.createStore() static method instead.
 * @param aUser String Name of user
 * @param aStoreName String Name of cache store.
 * @param aMaxSize int Maximum elements to be stored in the cache store.
 * @param aTtlSeconds int period in seconds after which items in the store will expire
 * and are available to be removed from store.
 */
  CacheStore(final String aUser, final String aStoreName,
             final int aMaxSize, final int aTtlSeconds) {
    pUser= StringUtility.defaultString(aUser).trim();
    pStoreName= aStoreName;
    pTtlSeconds= aTtlSeconds;
    pIsClosing= false;
    pMap= new TimedLruMap(aTtlSeconds*1000L, aMaxSize);
  }

/** Get this store's name */
  public synchronized String getUser() {
    return pUser;
  }

/** Get this store's name */
  public synchronized String getStoreName() {
    return pStoreName;
  }

/** Make this store available for closing, which will release its resources */
  public synchronized void close() {
    pIsClosing= true;
  }

/** Is the store being closed */
  public synchronized boolean isClosing() {
    return pIsClosing;
  }

/**
 * Put the given (key, item) pair into the store, if the store contains the same key,
 * the old value will be over-written.
 * @param aKey IKey Key of the cache item.
 * @param aItem Object The object to be cached, null objects are ignored (i.e. not put
 * to the store).
 */
  public synchronized void put(final IKey aKey, final Object aItem) {
    if (aItem==null) return;
    pMap.put(aKey, aItem);
  }

/**
 * Put the given (key, item) pair into the store, if the store contains the same key,
 * the old value will be over-written.
 * @param aKey String Key of the cache item. (one part key)
 * @param aItem Object The object to be cached, null objects are ignored (i.e. not put
 * to the store).
 */
  public synchronized void put(final String aKey, final Object aItem) {
    if (aItem==null) return;
    IKey k= new Key1(aKey);
    put(k, aItem);
  }

/** Get the non-expired object associated with the given key, null if no match or expired */
  public synchronized Object get(final IKey aKey) {
    pAttemptCount++;
    if (!pMap.containsKey(aKey)) return null;

    pFoundCount++;
    return pMap.get(aKey);
  }

/**
 * Get the non-expired object associated with the given key, null if no match
 * @param aKey String one part key
 * @return Object
 */
  public synchronized Object get(final String aKey) {
    IKey k= new Key1(aKey);
    return get(k);
  }

/**
 * Return a set of IKey objects, if String keys where used, the elements will actually
 * be of Key1 type. The returned set may include expired items that have not been
 * removed yet.
 */
  public synchronized Set keySet() {
    return pMap.keySet();
  }

/**
 * Check if the given key exist in this store. If the cache item has expired return false
 * and remove it from store.
 * @param aKey IKey Key of the item.
 * @return boolean true if found and not expired.
 */
  public synchronized boolean containsKey(final IKey aKey) {
    return pMap.containsKey(aKey);
  }

/** Check if the given key exist in this store. (one part key) */
  public synchronized boolean containsKey(final String aKey) {
    IKey k= new Key1(aKey);
    return containsKey(k);
  }

/** Remove the object which has the given key, return the removed object if found. */
  public synchronized Object remove(final IKey aKey) {
    return pMap.remove(aKey);
  }

/** Remove the object which has the given key, return the removed object if found. (one part key) */
  public synchronized Object remove(final String aKey) {
    IKey k= new Key1(aKey);
    return remove(k);
  }

/** Get the TTL in seconds */
  public synchronized int getTtlSeconds() {
    return pTtlSeconds;
  }

/** Clear all items of this store */
  public synchronized void clear() {
    pMap.clear();
  }

/** Check if the store has any elements */
  public synchronized boolean isEmpty() {
    return pMap.isEmpty();
  }

/** Number of items in the store */
  public synchronized int size() {
    return pMap.size();
  }

/** Get the maximum number of elements allowed in the store */
  public synchronized int getMaxSize() {
    return pMap.getMaxSize();
  }

/** Check if this store is equal to the given store object */
  public synchronized boolean equals(final Object aCacheStore) {
    if (aCacheStore==null) return false;
    if (!(aCacheStore instanceof ICacheStore)) return false;
    return equals( (ICacheStore) aCacheStore);
  }

/** Check if this store is equal to the given store object */
  public synchronized boolean equals(final ICacheStore aCacheStore) {
    if (aCacheStore==null) return false;
    if (this==aCacheStore) return true;
    CacheStore target= (CacheStore) aCacheStore;

    if (pTtlSeconds != target.pTtlSeconds) return false;
    if (pMap.getMaxSize() != target.pMap.getMaxSize()) return false;
    if (! StringUtility.equals(pStoreName, target.pStoreName)) return false;
    if (! pMap.equals( target.pMap )) return false;

    return true;
  }

/** Hash code of this store */
  public synchronized int hashCode() {
    return pMap.hashCode();
  }

/** Convert this store's data to a string */
  public synchronized String toString() {
    StringBuffer b= new StringBuffer(128);
    b.append("CacheStore:").append(pNL);
    b.append("User= ").append(pUser).append(pNL);
    b.append("StoreName= ").append(pStoreName).append(pNL);
    b.append("MaxSize= ").append(pMap.getMaxSize());
    b.append(",  CurrentSize= ").append(size()).append(pNL);
    b.append("TTL in Seconds= ").append(pTtlSeconds).append(pNL);
    b.append("Store's Data:").append(pNL);
    b.append(pMap.toString());
    return b.toString();
  }  // toString

/** Remove expired items from the store */
  public synchronized int removeExpired() {
    return pMap.removeExpired();
  } // removeExpired

/** How many times the get() method of this object has been called. */
  public synchronized long getAttemptCount() {
    return pAttemptCount;
  }

/** How many times the get() method of this object has returned a value. */
  public synchronized long getFoundCount() {
    return pFoundCount;
  }

/** Get the performance of the store as a percent of found pages/Total attempts */
  public synchronized float getPerformance() {
    if (pAttemptCount==0) return 0;
    float r= ((float)pFoundCount / (float)pAttemptCount) * (float) 100.0;
    return r;
  }  // getPerformance

}  // CacheStore
