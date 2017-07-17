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
package com.tek271.util.collections;

import java.util.*;
import com.tek271.util.*;
import com.tek271.util.collections.iterator.NotifyingIterator;

/**
 * A collection implementation that notifies when items are added
 * or deleted. It decorates another collection be making it notifying.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class NotifyingCollection implements Collection {
  private Collection pOrgCollection;
  private List pRemoveListeners = new ArrayList();  // of ICallback
  private List pAddListeners = new ArrayList();     // of ICallback

/**
 * Decorate a given collection by making it notifying. Use addAddListener() or
 * addRemoveListener() to define listeners.
 * @param aCollection Collection to decorate.
 */
  public NotifyingCollection(final Collection aCollection) {
    pOrgCollection= aCollection;
  }

/**
 * Decorate a given collection by making it notifying.
 * @param aCollection Collection to decorate.
 * @param aAddListener ICallback The call back when an item is added.
 * @param aRemoveListener ICallback The call back when an item is removed.
 */
  public NotifyingCollection(final Collection aCollection,
                             final ICallback aAddListener,
                             final ICallback aRemoveListener) {
    pOrgCollection= aCollection;
    if (aAddListener != null)  pAddListeners.add(aAddListener);
    if (aRemoveListener != null)  pRemoveListeners.add(aRemoveListener);
  }

/** Add a listener to an 'ADD' operation */
  public void addAddListener(final ICallback aListener){
    pAddListeners.add(aListener);
  }

/** Add a listener to an 'REMOVE' operation */
  public void addRemoveListener(final ICallback aListener){
    pRemoveListeners.add(aListener);
  }


  private static void notifyListeners(final List aListeners, final Object aParam) {
    if (aListeners==null || aListeners.isEmpty()) return;

    for (Iterator i= aListeners.iterator(); i.hasNext();) {
      ICallback cb= (ICallback) i.next();
      cb.call(aParam);
    }
  }  // notifyListeners

/** Add a member to the collection. Will cause an addCallback */
  public boolean add(Object o) {
    boolean r= pOrgCollection.add(o);
    notifyListeners(pAddListeners, o);
    return r;
  }

/** Add a collection to this collection. Will cause an addCallBack for each item */
  public boolean addAll(Collection c) {
    if (c==null) return false;
    boolean r= false;
    for (Iterator i= c.iterator(); i.hasNext();) {
      r= r || add(i.next());
    }
    return r;
  } // addAll

/** Clear this collection. Will cause a removeCallBack for each deleted item */
  public void clear() {
    if (size() ==0) return;
    for (Iterator i= iterator(); i.hasNext(); ) {
      i.next();
      i.remove();
    }
  }  // clear

/** check if this collection contains the given object */
  public boolean contains(Object o) {
    return pOrgCollection.contains(o);
  }

/** Check if this collection contains all members in the given collection */
  public boolean containsAll(Collection c) {
    return pOrgCollection.containsAll(c);
  }

/** Check if this collection is equal to the given collection */
  public boolean equals(Object o) {
    return pOrgCollection.equals(o);
  }

/** Hashcode of this object */
  public int hashCode() {
    return pOrgCollection.hashCode();
  }

/** Is this collection empty */
  public boolean isEmpty() {
    return pOrgCollection.isEmpty();
  }

/**
 * Get an iterator on this collection. The iterator is notifying; if you use it to
 * remove items.
 * @return Iterator a notifying iterator.
 */
  public Iterator iterator() {
    return new NotifyingIterator(pOrgCollection.iterator(), pRemoveListeners);
  }

/**
 * Remove the given object from the collection.
 * @param o Object to remove
 * @param aIsNotify boolean notify or not
 * @return boolean true if object existed before remove.
 */
  public boolean remove(Object o, final boolean aIsNotify) {
    boolean r= pOrgCollection.remove(o);
    if (aIsNotify) notifyListeners(pRemoveListeners, o);
    return r;
  }

/** Remove the given object, notifying the listeners */
  public boolean remove(Object o) {
    return remove(o, true);
  }

/** Remove from this collection all members of the given collection */
  public boolean removeAll(Collection c) {
    if (c==null || c.isEmpty() || size()==0 ) return false;

    boolean r= false;
    for (Iterator i=c.iterator(); i.hasNext();) {
      r= remove(i.next()) || r;
    }
    return r;
  }

/** Remove from this collection all items except those in the given collection */
  public boolean retainAll(Collection c) {
    if (c==null || c.isEmpty()) {
      if (size()==0) return false;
      clear();
      return true;
    }

    boolean r= false;
    for (Iterator i= pOrgCollection.iterator(); i.hasNext();) {
      Object item= i.next();
      if (!c.contains(item)) {
        remove(item);
        r= true;
      }
    }
    return r;
  }  // retainAll()

/** Return the size of this collection */
  public int size() {
    return pOrgCollection.size();
  }

  public Object[] toArray() {
    return pOrgCollection.toArray();
  }

  public Object[] toArray(Object[] a) {
    return pOrgCollection.toArray(a);
  }

  public String toString() {
    return pOrgCollection.toString();
  }

}  // NotifyingCollection
