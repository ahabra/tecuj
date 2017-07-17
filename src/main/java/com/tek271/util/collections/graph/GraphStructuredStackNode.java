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

package com.tek271.util.collections.graph;

/**
 * A node that has a key and contents.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class GraphStructuredStackNode extends AbstractGraphStructuredStackNode {
  private Object pKey;
  private Object pContents;

  public GraphStructuredStackNode() {}

  public GraphStructuredStackNode(final Object aKey, final Object aContents) {
    pKey= aKey;
    pContents= aContents;
  }

  public Object getKey() {
    return pKey;
  }

  public void setKey(final Object aKey) {
    pKey= aKey;
  }

  public boolean isEqualKey(final Object aKey) {
    return checkEqual(pKey, aKey);
  }

  private static boolean checkEqual(final Object aLocal, final Object aGiven) {
    if (aLocal==null && aGiven==null) return true;
    if (aLocal==null) return false;
    return aLocal.equals(aGiven);
  }

  public Object getContents() {
    return pContents;
  }

  public void setContents(final Object aContents) {
    pContents= aContents;
  }

  public boolean isEqualContents(final Object aContents) {
    return checkEqual(pContents, aContents);
  }


}  // GraphStructuredStackNode
