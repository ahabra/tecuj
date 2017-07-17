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
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.string.StringUtility;

/**
 * Contains static methods to create/access/remove a cache store. Cache stores are
 * keyed by either User:StoreName or just StoreName. Note that this key is global
 * per JVM, i.e. In the same JVM, two classes can access the same cache store by the
 * same key value.<p>
 * <b>Warning 1</b>: When two classes access the same store/key, one can modify a
 * cached value that the other may use. Cached objects are available to all classes in
 * the JVM. If you are concerned about this issue, store only immutable objects in the
 * store.<br>
 * <b>Warning 2</b>: Some objects have restrictions on how to read them, for example,
 * the JDBC ResultSet after fully reading it, requires that you call the <i>first()</i>
 * method in order to read it again. (Assuming you have the right JDBC driver)
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public class StoreFactory {
  private static Map pStores= new HashMap();
  private static long pAttemptCountRemoved;
  private static long pFoundCountRemoved;

  private StoreFactory() {}

/**
 * Get a store from the factory if it already exists, or create a new one if
 * aIsCreatIfNotFound is true. The store is identified by the given user and store name.
 * If aUser has a value then the key will be aUser:aStoreName, otherwise, the key will
 * be aStoreName. If the key exist, its associated store will be returned.
 * @param aUser The name of the user, can be null.
 * @param aStoreName String name of store. To avoid conflicts, it is suggested to use
 * a store name that consists of the package and class name where the store is being
 * used, e.g. com.mycompany.myproject.MyClass.
 * @param aMaxSize int Maximum elements to be stored in the cache store.
 * @param aTtlSeconds int period in seconds after which items in the store will expire
 * and are available to be removed from store.
 * @param aIsCreatIfNotFound If true and if the store does not exist, create a new
 * store.
 * @return ICacheStore The found store. If not found and if aIsCreatIfNotFound is true
 * return a newly created store, otherwise return null.
 */
  public synchronized static ICacheStore getStore(final String aUser,
                                                  final String aStoreName,
                                                  final int aMaxSize,
                                                  final int aTtlSeconds,
                                                  final boolean aIsCreatIfNotFound) {
    closeStores();
    String k= makeKey(aUser, aStoreName);
    if (pStores.containsKey(k)) {
      return (ICacheStore) pStores.get(k);
    }
    if (!aIsCreatIfNotFound) return null;

    ICacheStore cs= new CacheStore(aUser, k, aMaxSize, aTtlSeconds);
    pStores.put(k, cs);
    return cs;
  } // getStore

  private static String makeKey(final String aUser, final String aStoreName) {
    String k= StringUtility.trim(aUser);
    if (StringUtility.isBlank(k)) k= aStoreName;
    else k= k + ":" + aStoreName;
    return k;
  }

/**
 * Get a store from the factory if it already exist, or create a new one if
 * aIsCreatIfNotFound is true. The store is identified by the given store name.
 * @param aStoreName String name of store. The store name is a key, if the name exist
 * its associated store will be returned. To avoid conflicts, it is suggested to use
 * a store name that consists of the package and class name where the store is being
 * used, e.g. com.mycompany.myproject.MyClass.
 * @param aMaxSize int Maximum elements to be stored in the cache store.
 * @param aTtlSeconds int period in seconds after which items in the store will expire
 * and are available to be removed from store.
 * @param aIsCreatIfNotFound If true and if the store does not exist, create a new
 * store.
 * @return ICacheStore The found store. If not found and if aIsCreatIfNotFound is true
 * return a newly created store, otherwise return null.
 */
  public synchronized static ICacheStore getStore(final String aStoreName,
                                                  final int aMaxSize,
                                                  final int aTtlSeconds,
                                                  final boolean aIsCreatIfNotFound) {
    return getStore(null, aStoreName, aMaxSize, aTtlSeconds, aIsCreatIfNotFound);
  } // getStore


/**
 * Create a new cache store if it does not already exist, or return an existing store
 * identified by the given store name.
 * @param aStoreName String name of store. The store name is a key, if the name exist
 * its associated store will be returned. To avoid conflicts, it is suggested to use
 * a store name that consists of the package and class name where the store is being
 * used, e.g. com.mycompany.myproject.MyClass.
 * @param aMaxSize int Maximum elements to be stored in the cache store.
 * @param aTtlSeconds int period in seconds after which items in the store will expire
 * and are available to be removed from store.
 * @return ICacheStore The created store.
 */
  public synchronized static ICacheStore getStore(final String aStoreName,
                                                  final int aMaxSize,
                                                  final int aTtlSeconds) {
    return getStore(null, aStoreName, aMaxSize, aTtlSeconds, true);
  } // getStore

/**
 * Create a new cache store if it does not already exist, or return an existing store
 * identified by the given store name. The max size is 64 and the TTL is 120 seconds.
 * @param aStoreName String name of store. The store name is a key, if the name exist
 * its associated store will be returned.
 * @return ICacheStore The created store.
 */
  public synchronized static ICacheStore getStore(final String aStoreName) {
    return getStore(null, aStoreName, 64, 120, true);
  }

/** Get an existing store, null if not found */
  public synchronized static ICacheStore getExistingStore(final String aUser,
                                                          final String aStoreName) {
    return getStore(aUser, aStoreName, -1, -1, false);
  }

/** Get an existing store, null if not found */
  public synchronized static ICacheStore getExistingStore(final String aStoreName) {
    return getStore(null, aStoreName, -1, -1, false);
  }


/** Close stores whose isClosing flag is true */
  private static void closeStores() {
    Set es= pStores.entrySet();
    for (Iterator i= es.iterator(); i.hasNext();) {
      Map.Entry e= (Map.Entry) i.next();
      ICacheStore v= (ICacheStore) e.getValue();
      v.removeExpired();
      if (v.isClosing()) {
        pAttemptCountRemoved += v.getAttemptCount();
        pFoundCountRemoved += v.getFoundCount();
        i.remove();
      }
    }  // for
  }  // closeStores

/** Check if the given store exist */
  public synchronized static boolean isStoreExist(final String aUser,
                                                  final String aStoreName) {
    closeStores();
    String k= makeKey(aUser, aStoreName);
    return pStores.containsKey(k);
  }

/** Check if the given store exist */
  public synchronized static boolean isStoreExist(final String aStoreName) {
    closeStores();
    return pStores.containsKey(aStoreName);
  }

/** Number of current open cache stores */
  public synchronized static int storeCount() {
    closeStores();
    return pStores.size();
  }  // storeCount

/** Remove the given cache store */
  public synchronized static void removeStore(final String aUser,
                                              final String aStoreName) {
    String k= makeKey(aUser, aStoreName);
    pStores.remove(k);
  }

/** Remove the given cache store */
  public synchronized static void removeStore(final String aStoreName) {
    pStores.remove(aStoreName);
  }

/** Remove all stores */
  public synchronized static void removeAllStores() {
    pStores.clear();
  }

/**
 * Get a list of all store names and their users. If the store has a user, the list entry
 * will be User:StoreName.
 * @return ListOfString
 */
  public synchronized static ListOfString getStoreNames() {
    closeStores();
    ListOfString r= new ListOfString();
    Set keys= pStores.keySet();
    for (Iterator i= keys.iterator(); i.hasNext();) {
      r.add( i.next() );
    }
    return r;
  }  // getStoreNames()

/**
 * Get access counts for all cache stores produced by this factory. This is a mehtod
 * for reporting and administration.
 * @return long[] an array of four elements: <ul>
 * <li>[0]: Total number of all attempts to read from the cache.
 * <li>[1]: Number of times the cache contained the desired object.
 * <li>[2]: Total number of all attempts to read from the cache for removed stores.
 * <li>[3]: Number of times the cache contained the desired object for removed stores.
 */
  public synchronized static long[] getAccessCount() {
    long at=0;
    long f=0;

    Set es= pStores.entrySet();
    for (Iterator i= es.iterator(); i.hasNext();) {
      Map.Entry e= (Map.Entry) i.next();
      ICacheStore v= (ICacheStore) e.getValue();
      v.removeExpired();
      at += v.getAttemptCount();
      f  += v.getFoundCount();
      if (v.isClosing()) {
        i.remove();
      }
    }  // for
    long[] r= {at, f, pAttemptCountRemoved, pFoundCountRemoved};
    return r;
  }  // getAccessCount

/** Get the cache performance as a percent of found pages/Total attempts */
  public synchronized static float getPerformance() {
    long[] ac= getAccessCount();
    long at= ac[0] + ac[2];
    long f=  ac[1] + ac[3];

    if (at==0) return 0;  // avoid devid by zero errors
    float r= ((float)f / (float)at) * (float) 100.0;
    return r;
  } // getPerformance

}  // StoreFactory class
