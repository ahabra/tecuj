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

import com.tek271.util.string.StringUtility;

/**
 * A Tree structure of string objects. This tree has the same object for key and contents.
 * <p>Copyright (c) 2006. Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class TreeOfString extends AbstractTree {
/** In this implemantation, key and contents are the same object */
  private String pKey;

/** create a TreeOfString object with the given parent */
  public TreeOfString(final ITree aParent) {
    this(aParent, null);
  }

/** create a TreeOfString object with the given parent and contents. */
  public TreeOfString(final ITree aParent, final String aKey) {
    setParent(aParent);
    pKey= aKey;
  }

  public void setKey(final Object aKey) {
    if (aKey==null) {
      pKey= null;
      return;
    }

    if (aKey instanceof String) pKey= (String) aKey;
    else pKey= aKey.toString();
  }

  public Object getKey() {
    return pKey;
  }

  public void setContents(final Object aContents) {
    setKey(aContents);
  }  // setContents

  public Object getContents() {
    return pKey;
  }

  public boolean isEqualKey(final Object aKey) {
    if (aKey==null) {
      return pKey==null;
    }
    if (aKey==pKey) return true;

    if (! (aKey instanceof String) ) return false;

    return StringUtility.equals(pKey, (String) aKey);
  }

  public boolean isEqualContents(final Object aContents) {
    return isEqualKey(aContents);
  }

}  // TreeOfString
