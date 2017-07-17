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

/**
 * An interface to represent a cache store.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public interface ICacheStore {
  // constructor params:
  // 1. user name
  // 2. store name
  // 3. max size
  // 4. ttl

/** Get user associated with this store */
  public String getUser();

/** Get the store's name */
  public String getStoreName();

/**
 * Close the store, closing the store will indicate to the cache framework that this
 * store is ready to be closed, the store does not have to be closed immediately.
 */
  public void close();

/** The close method has been called on this store, but did not close yet. */
  public boolean isClosing();

/**
 * Put an object in the cache store.
 * @param aKey IKey Key of the object.
 * @param aItem Object The object to be cached.
 */
  public void put(final IKey aKey, final Object aItem);

/**
 * Put an object in the cache store.
 * @param aKey String Key of the object. This is a single-part key.
 * @param aItem Object The object to be cached.
 */
  public void put(final String aKey, final Object aItem);

/** get the cached object associated with the key, null if not found */
  public Object get(final IKey aKey);

/** get the cached object associated with the single-part key, null if not found */
  public Object get(final String aKey);

/** The set of keys for this store */
  public Set keySet();

/** Does the store contain a cached object with the given key */
  public boolean containsKey(final IKey aKey);

/** Does the store contain a cached object with the given key */
  public boolean containsKey(final String aKey);

/** Remove the cached object with the given key */
  public Object remove(final IKey aKey);

/** Remove the cached object with the given key */
  public Object remove(final String aKey);

/** Remove all expired items from this cache store */
  public int removeExpired();

  /** Time-To-Live in seconds */
  public int getTtlSeconds();

/** Clear this cache store */
  public void clear();

/** Is the cache store empty */
  public boolean isEmpty();

/** Number of items in the store */
  public int size();

/** Maximum # of items to store in this cache store */
  public int getMaxSize();

/** Check if the given aCacheStore equals this cache store */
  public boolean equals(final Object aCacheStore);

/** Check if the given aCacheStore equals this cache store */
  public boolean equals(final ICacheStore aCacheStore);

/** Hash code of this object */
  public int hashCode();

/** Get a string representation of this cache store */
  public String toString();

/** Number of time the get() method was called on this store */
  public long getAttemptCount();


/** Number of time the get() method was called and returned a value on this store */
  public long getFoundCount();

/** Get the cache performance of the store as a percent of found pages/Total attempts */
  public float getPerformance();

}  // ICacheStore
