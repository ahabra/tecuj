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

import java.util.*;

import com.tek271.util.string.*;
import com.tek271.util.collections.CollectionUtility;
import com.tek271.util.collections.list.*;

/**
 * Implements IGraphStructuredStackNode which abstracts a Graph-structured Stack (GS) node.
 * A GS is a directed acyclic graph where
 * each directed path is considered a stack. You can think of a GS as a structure
 * similar to a tree structure with the exception that a node can have more than
 * one parent.
 * See <a href="http://en.wikipedia.org/wiki/Graph-structured_stack">wikipedia</a>
 *  for more info on GS.
 * <p>Copyright (c) 2006.  Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public abstract class AbstractGraphStructuredStackNode implements IGraphStructuredStackNode {
  private static final int pINDENT_SIZE=2;  // used with toString()

  protected List pParentList= new ArrayList(); // of AbstractGraphStructuredStackNode objects
  protected List pChildren= new ArrayList();   // of AbstractGraphStructuredStackNode objects


/** Check if this IGraphStructuredStackNode has a parent */
  public boolean hasParent() {
    return CollectionUtility.size(pParentList) > 0;
  }

/** Check if this IGraphStructuredStackNode has the given aParent as a parent */
  public boolean hasParent(final IGraphStructuredStackNode aParent) {
    if (!hasParent()) return false;
    return pParentList.contains(aParent);
  }

/** Check if this IGraphStructuredStackNode has a parent with the given key */
  public boolean hasParent(final Object aParentKey) {
    return indexOfParent(aParentKey, 0) >= 0;
  }  // hasParent

/**
 * Search the list of IGraphStructuredStackNode objects for an object with the given key
 * @param aListOfIGraphStructuredStackNode List of IGraphStructuredStackNode objects.
 * @param aKey Object Key to find
 * @param aStartIndex int starting index in the list
 * @return int index of found object, -1 if not found
 */
  private static int searchList(final List aListOfGssNodes,
                                final Object aKey,
                                final int aStartIndex) {
    if (aListOfGssNodes==null) return -1;
    for (int i=aStartIndex, n= aListOfGssNodes.size(); i<n; i++) {
      IGraphStructuredStackNode gs= (IGraphStructuredStackNode) aListOfGssNodes.get(i);
      if (gs.isEqualKey(aKey)) return i;
    }
    return -1;
  }  // searchList

/** Get the list of parents of this IGraphStructuredStackNode */
  public List getParentList() {
    return pParentList;
  }

/**
 * Find the index of the child with the given key starting at the given index.
 * @param aParentKey Object The key to look for
 * @param aStartIndex int starting index
 * @return int index of the child, -1 if not found.
 */
  public int indexOfParent(final Object aParentKey, final int aStartIndex) {
    return searchList(pParentList, aParentKey, aStartIndex);
  }

/**
 * Find the index of the child with the given key starting at first child.
 * @param aParentKey Object The key to look for
 * @return int index of the child, -1 if not found.
 */
  public int indexOfParent(final Object aParentKey) {
    return indexOfParent(aParentKey, 0);
  }

/** Get the child at the given index. Null if not a valid index */
  public IGraphStructuredStackNode getParent(final int aIndex) {
    if (CollectionUtility.isNotValidIndex(pParentList, aIndex)) return null;
    return (IGraphStructuredStackNode) pParentList.get(aIndex);
  }

/** Get the parent with the given key */
  public IGraphStructuredStackNode getParent(final Object aParentKey) {
    if (! hasParent()) return null;
    int i= indexOfParent(aParentKey);
    if (i<0) return null;
    return getParent(i);
  }

/**
 * Add a parent of this IGraphStructuredStackNode object. If aParent is already a parent
 * of this object, it will not be added.
 * @param aParent IGraphStructuredStackNode Parent to be added.
 */
  public void addParent(final IGraphStructuredStackNode aParent) {
    hookParentToChild(aParent, this);
  }

/** Remove the given parent from list of parents */
  public void removeParent(final IGraphStructuredStackNode aParent) {
    if (! hasParent()) return;
    unhookParentFromChild(aParent, this);
  }

/** Remove the parent at the given index from list of parents */
  public void removeParent(final int aParentIndex) {
    IGraphStructuredStackNode parent= getParent(aParentIndex);
    if (parent==null) return;
    removeParent(parent);
  }

/** Remove the parent with the given key from list of parents */
  public void removeParent(final Object aParentKey) {
    IGraphStructuredStackNode parent= getParent(aParentKey);
    if (parent==null) return;
    removeParent(parent);
  }

/** Remove all parents of this IGraphStructuredStackNode object */
  public  void removeAllParents() {
    if (! hasParent()) return;
    for (Iterator i=pParentList.iterator(); i.hasNext();) {
      IGraphStructuredStackNode p= (IGraphStructuredStackNode) i.next();
      unhookParentFromChild(p, this);
    }
  }  // removeAllParents

/** Hook a parent with a child */
  private static void hookParentToChild(final IGraphStructuredStackNode aParent,
                                        final IGraphStructuredStackNode aChild) {
    if (aParent==null || aChild==null) return;
    // first add aChild to the parent's list of children
    int i= aParent.indexOfChild(aChild.getKey());
    if (i < 0) { // is new child
      aParent.getChildren().add(aChild);
    } else {  // child exists, replace its contents and children
      IGraphStructuredStackNode ch= aParent.getChild(i);
      ch.setContents( aChild.getContents() );
      ch.setChildren( aChild.getChildren() );
    }

    // second, set aParent as parent of aChild
    if (! aChild.hasParent(aParent)) {    // child points to this object
      aChild.getParentList().add(aParent);
    }
  }  // hookParentToChild

/** Remove association between a parent and a child */
  private static void unhookParentFromChild(final IGraphStructuredStackNode aParent,
                                            final int aChildIndex) {
    if (aParent==null || aChildIndex<0) return;
    // first, remove aParent from aChild's parents
    IGraphStructuredStackNode child= aParent.getChild(aChildIndex);
    child.getParentList().remove(aParent);

    // second remove aChild from the parent's list of children
    aParent.getChildren().remove(aChildIndex);
  }  // unhookParentToChild


/** Remove association between a parent and a child */
  private static void unhookParentFromChild(final IGraphStructuredStackNode aParent,
                                            final IGraphStructuredStackNode aChild) {
    if (aParent==null || aChild==null) return;
    int i= aParent.indexOfChild(aChild.getKey());

    if (i>=0)   unhookParentFromChild(aParent, i);
  }  // unhookParentToChild


/** Get the total number of descendants of this IGraphStructuredStackNode object */
  public int descendantsCount() {
    int n=childrenCount();
    int total=n;
    for (int i=0; i<n; i++) {
      total += getChild(i).descendantsCount();
    }
    return total;
  }

/** Get the number of (direct) children of this IGraphStructuredStackNode object */
  public int childrenCount() {
    return CollectionUtility.size(pChildren);
  }

/** Does this IGraphStructuredStackNode object have any children */
  public boolean hasChildren() {
    return childrenCount() > 0;
  }

/** Get a list of the children of this object */
  public List getChildren() {
    return pChildren;
  }

/** Set the children of this IGraphStructuredStackNode object */
  public void setChildren(final List aChildren) {
    // unhook old children from this parent
    for (int i=0, n=childrenCount(); i<n; i++) {
      unhookParentFromChild(this, i);
    }

    // hook new children to this parent
    pChildren.clear();
    for (Iterator i=aChildren.iterator(); i.hasNext();) {
      IGraphStructuredStackNode ch= (IGraphStructuredStackNode) i.next();
      hookParentToChild(this, ch);
    }
  }  // setChildren

/**
 * Find the index of the child with the given key starting at the given index.
 * @param aKey Object The key to look for
 * @param aStartIndex int starting index
 * @return int index of the child, -1 if not found.
 */
  public int indexOfChild(final Object aKey, final int aStartIndex) {
    return searchList(pChildren, aKey, aStartIndex);
  }

/**
 * Find the index of the child with the given key starting at first child.
 * @param aKey Object The key to look for
 * @return int index of the child, -1 if not found.
 */
  public int indexOfChild(final Object aKey) {
    return searchList(pChildren, aKey, 0);
  }

/** Get the child at the given index. Null if not a valid index */
  public IGraphStructuredStackNode getChild(final int aIndex) {
    if (CollectionUtility.isNotValidIndex(pChildren, aIndex)) return null;
    return (IGraphStructuredStackNode) pChildren.get(aIndex);
  }

/** Get the child whose key matches the given aKey, null if not found */
  public IGraphStructuredStackNode getChild(final Object aKey) {
    int i= indexOfChild(aKey);
    if (i<0) return null;
    return getChild(i);
  }

/**
 * Add a child at the end of the children's list. If another child exists with the
 * same key, the new child will replace the old existing child.
 * @param aChild IGraphStructuredStackNode Child to add.
 */
  public void addChild(final IGraphStructuredStackNode aChild) {
    hookParentToChild(this, aChild);
  }  // addChild

/** Delete the child at the given index */
  public void removeChild(final int aIndex) {
    unhookParentFromChild(this, aIndex);
  }

/** Delete the child equal to the given IGraphStructuredStackNode */
  public void removeChild(final IGraphStructuredStackNode aChild) {
    unhookParentFromChild(this, aChild);
  }

/** Delete all children with the given key */
  public void removeChild(final Object aKey) {
    int n=childrenCount()-1;
    for (int i=n; i>=0; i--) {
      if (getChild(i).isEqualKey(aKey)) removeChild(i);
    }
  }

/** Check if the children of this object are equal to the children of the given node */
  private boolean isEqualChildren(final IGraphStructuredStackNode aGraphStack) {
    int n= childrenCount();
    if (n != aGraphStack.childrenCount() ) return false;

    for (int i=0; i<n; i++) {
      IGraphStructuredStackNode t1= getChild(i);
      IGraphStructuredStackNode t2= aGraphStack.getChild(i);
      if (! t1.isEqual(t2)) return false;
    }
    return true;
  }  // isEqualChildren


/** Check if this IGraphStructuredStackNode is equal to the given aGraphStack */
  public boolean isEqual(final IGraphStructuredStackNode aGraphStack) {
    if (aGraphStack==null) return false;
    if (this == aGraphStack) return true;
    if (! isEqualKey(aGraphStack.getKey()) )  return false;
    if (! isEqualContents(aGraphStack.getContents()) ) return false;
    if (! isEqualChildren(aGraphStack)) return false;
    return true;
  }

/** Check if this IGraphStructuredStackNode is equal to the given aGraphStack */
  public boolean equals(final Object aGraphStack) {
    if (aGraphStack==null) return false;
    if (! (aGraphStack instanceof IGraphStructuredStackNode) ) return false;
    return isEqual( (IGraphStructuredStackNode) aGraphStack);
  }

/** Returns the hashcode of the key, if key is null, return zero. */
  public int hashCode() {
    Object k= getKey();
    if (k==null) return 0;
    return k.hashCode();
  }

/**
 * Add this IGraphStructuredStackNode object to the given list
 * @param aList ListOfString List that contains IGraphStructuredStackNode objects serialized to strings
 * @param aIndent int Size of indentation before the serialized string
 */
  private void addToListOfString(final ListOfString aList, final int aIndent) {
    int n= childrenCount();
    StringBuffer buf= new StringBuffer(64);
    buf.append( StringUtility.blanks(aIndent) );
    buf.append("(Key=").append( getKey() ).append(") ");
    buf.append("(Contents=").append( getContents() ).append(")");
    if (n>0) {
      buf.append(", (Children count=").append(n).append(")");
    }
    aList.addItem( buf.toString() );

    int indent= aIndent + pINDENT_SIZE;
    for (int i=0; i<n; i++) {
      AbstractGraphStructuredStackNode child= (AbstractGraphStructuredStackNode) getChild(i);
      child.addToListOfString(aList, indent);
    }
  }  // addToListOfString

/**
 * Convert this ITree and all its children to a list of string
 * @return ListOfString
 */
  private ListOfString toListOfString() {
    int n= descendantsCount();
    ListOfString r= new ListOfString(n + 1);
    addToListOfString(r, 0);
    return r;
  }  // toListOfString

/** Get this IGraphStructuredStackNode and all its children as a string */
  public String toString() {
    return toListOfString().getText();
  }

/**
 * Get the descendant at the given path of keys.
 * @param aKeyList List of keys that will used as a path to find the descendants
 * @return IGraphStructuredStackNode The child, null if not found.
 */
  public IGraphStructuredStackNode getDescendant(final List aKeyList) {
    int n= CollectionUtility.size(aKeyList);
    if (n==0) return null;

    IGraphStructuredStackNode child=this;
    for(int i=0; i<n; i++) {
      Object key= aKeyList.get(i);
      child= child.getChild(key);
      if (child==null) return null;
    }
    return child;
  }  // getDescendant

/**
 * Get the descendant at the given path of keys, the key list is slash / separated.
 * @param aKeyList String A slash eparated list of keys
 * @return IGraphStructuredStackNode The child, null if not found.
 */
  public IGraphStructuredStackNode getDescendant(final String aKeyList) {
    if (StringUtility.isBlank(aKeyList)) return null;
    ListOfString list= ListOfString.createFromString(aKeyList, "/");
    return getDescendant(list);
  }

/** Find the first descendant with the given key */
  public IGraphStructuredStackNode findDescendant(final Object aKey) {
    IGraphStructuredStackNode r= getChild(aKey);
    if (r != null) return r;

    for (int i=0, n= childrenCount(); i<n; i++) {
      r= getChild(i).findDescendant(aKey);
      if (r != null) return r;
    }
    return null;
  }  // findDescendant

/** Get a list of the ancestors of this IGraphStructuredStackNode. The first item in the list is the root */
  public List getFirstRootPath() {
    List r= new LinkedList();
    IGraphStructuredStackNode p= getParent(0);
    while (p != null) {
      r.add(0, p);
      p= p.getParent(0);
    }
    return r;
  }

  // Abstract methods
  // They do not have to be listed here, but this is kind of documentation for the
  // programmer extending this class.

  public abstract void setKey(Object aKey);
  public abstract Object getKey();
  public abstract boolean isEqualKey(Object aKey);

  public abstract void setContents(Object aContents);
  public abstract Object getContents();
  public abstract boolean isEqualContents(Object aContents);

}  // AbstractGraphStructuredStackNode
