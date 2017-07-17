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
package com.tek271.util.collections.tree;

import com.tek271.util.collections.graph.*;

/**
 * Implements the ITree interface. Classes that need to implement ITree should extend
 * this class and provide implementation for its abstract methods. The abstract
 * methods of this class are:<pre>
  abstract public Object getContents() ;
  abstract public void setContents(final Object aContents);
  abstract public boolean isEqualContents(final Object aContents);

  abstract public Object getKey();
  abstract public void setKey(final Object aKey);
  abstract public boolean isEqualKey(final Object aKey);
</pre>
 * <p>Copyright (c) 2005. Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public abstract class AbstractTree
    extends AbstractGraphStructuredStackNode
    implements ITree {

/**
 * Add a parent of this ITree object. Overrides the method in IGraphStructuredStackNode.
 * @param aParent IGraphStructuredStackNode. Must be an object that implements the
 * ITree interface.
 */
  public void addParent(IGraphStructuredStackNode aParent) {
    removeAllParents();
    super.addParent(aParent);
  }


/** Set the parent of this ITree object */
  public void setParent(final ITree aParent) {
    addParent(aParent);
  }  // setParent

/** Get the parent of this ITree */
  public ITree getParent() {
    return (ITree) getParent(0);
  }


// abstract methods
  abstract public Object getContents() ;
  abstract public void setContents(final Object aContents);
  abstract public boolean isEqualContents(final Object aContents);

  abstract public Object getKey();
  abstract public void setKey(final Object aKey);
  abstract public boolean isEqualKey(final Object aKey);

}  // AbstractTree
