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
package com.tek271.util.collections.set;


import java.util.*;
import com.tek271.util.*;
import com.tek271.util.collections.*;

/**
 * A set implementation that notifies when items are added or deleted.
 * It decorates another set be making it notifying.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class NotifyingSet extends NotifyingCollection implements Set {

/**
 * Decorate a set by making it notifying set.
 * @param aSet Set
 */
  public NotifyingSet(final Set aSet) {
    super(aSet);
  }

/**
 * Decorate a set by making it notifying set.
 * @param aSet Set The set to work on.
 * @param aAddListener ICallback The call back when an item is added.
 * @param aRemoveListener ICallback The call back when an item is removed.
 */
  public NotifyingSet(final Set aSet,
                      final ICallback aAddListener,
                      final ICallback aRemoveListener) {
    super(aSet, aAddListener, aRemoveListener);
  }

/**
 * Decorate a collection by making it notifying set.
 * @param aCollection Collection
 */
  public NotifyingSet(final Collection aCollection) {
    super(aCollection);
  }

/**
 * Decorate a collection by making it notifying set.
 * @param aCollection Collection The collection to decorate.
 * @param aAddListener ICallback The call back when an item is added.
 * @param aRemoveListener ICallback The call back when an item is removed.
 */
  public NotifyingSet(final Collection aCollection,
                      final ICallback aAddListener,
                      final ICallback aRemoveListener) {
    super(aCollection, aAddListener, aRemoveListener);
  }


}  // NotifyingSet
