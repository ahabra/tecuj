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
package com.tek271.util.collections.iterator;

import java.util.*;
import com.tek271.util.*;


/**
 * Decorate an Iterator such that remove operations will cause a callback
 *  through aRemoveNotifier.execute().
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class NotifyingIterator implements Iterator {
  private Iterator pOrgIterator;
  private List pRemoveListeners;  // of ICallback
  private Object pCurrentItem;

/**
 * Decorate an iterator by making it notifying on remove operations.
 * @param aIterator Iterator The iterator to decorate.
 * @param aRemoveListener List A list of listeners which will be notified when an item
 * is removed from the iterator. The list contains objects of type ICallback
 */
  public NotifyingIterator(final Iterator aIterator, final List aRemoveListener) {
    pOrgIterator= aIterator;
    if (aRemoveListener != null) {
      pRemoveListeners= aRemoveListener;
    }
  }

/** Is there a next item */
  public boolean hasNext() {
    return pOrgIterator.hasNext();
  }

/** Get the next item from the iterator */
  public Object next() {
    pCurrentItem= pOrgIterator.next();
    return pCurrentItem;
  }

  private int listenerCount() {
    if (pRemoveListeners==null) return 0;
    return pRemoveListeners.size();
  }

/**
 * Remove the current item and notify all listeners by calling their execute() methods.
 */
  public void remove() {
    if (pCurrentItem==null) return;

    int n= listenerCount();
    for (int i=0; i<n; i++) {
      ICallback cb= (ICallback) pRemoveListeners.get(i);
      cb.call(pCurrentItem);
    }
    pOrgIterator.remove();
    pCurrentItem= null;
  } // remove

}  // NotifyingIterator
