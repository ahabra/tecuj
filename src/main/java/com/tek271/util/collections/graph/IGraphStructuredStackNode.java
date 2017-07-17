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

/**
 * Abstracts a Graph-structured Stack (GS) node. A GS is a directed acyclic graph where
 * each directed path is considered a stack. You can think of a GS as a structure
 * similar to a tree structure with the exception that a node can have more than
 * one parent.
 * See <a href="http://en.wikipedia.org/wiki/Graph-structured_stack">wikipedia</a>
 *  for more info on GS.
 * <p>Notice that this interface extends the IGraphNode interface.</p>
 * Each node consists of the following:<ol>
 * <li>Key</li>
 * <li>Contents</li>
 * <li>List of its children. Each child is an IGraphStructuredStackNode object. Children
 *     will have distinct keys. </li>
 * <li>List of its parents. Each parent is an IGraphStructuredStackNode object. A node
 *     CAN have more than one parents with the same key. However, the list of parents
 *     will not contain the same parent node more than once.</li>
 * </ol>
 * <p>Copyright (c) 2006. Technology Exponent
 * @author Abdul Habra
 * @version 1.0
 */
public interface IGraphStructuredStackNode extends IGraphNode {

/** Check if this IGraphStructuredStackNode has a parent */
  boolean hasParent();

/** Check if this IGraphStructuredStackNode has the given aParent as a parent */
  boolean hasParent(IGraphStructuredStackNode aParent);

/** Check if this IGraphStructuredStackNode has a parent with the given key */
  boolean hasParent(Object aParentKey);

/** Get the list of parents of this IGraphStructuredStackNode */
  List getParentList();

/**
 * Find the index of the child with the given key starting at the given index.
 * @param aParentKey Object The key to look for
 * @param aStartIndex int starting index
 * @return int index of the child, -1 if not found.
 */
  int indexOfParent(Object aParentKey, int aStartIndex);

/**
 * Find the index of the child with the given key starting at first child.
 * @param aParentKey Object The key to look for
 * @return int index of the child, -1 if not found.
 */
  int indexOfParent(Object aParentKey);

/** Get the child at the given index. Null if not a valid index */
  IGraphStructuredStackNode getParent(int aIndex);

/** Get the parent with the given key */
  IGraphStructuredStackNode getParent(Object aParentKey);

/** Add a parent of this IGraphStructuredStackNode object */
  void addParent(IGraphStructuredStackNode aParent);

/** Remove the given parent from list of parents */
  void removeParent(IGraphStructuredStackNode aParent);

/** Remove the parent at the given index from list of parents */
  void removeParent(int aParentIndex);

/** Remove the parent with the given key from list of parents */
  void removeParent(Object aParentKey);

/** Remove all parents of this IGraphStructuredStackNode object */
  void removeAllParents();

/** Get the total number of descendants of this IGraphStructuredStackNode object */
  int descendantsCount();

/** Get the number of (direct) children of this IGraphStructuredStackNode object */
  int childrenCount();

/** Does this IGraphStructuredStackNode object have any children */
  boolean hasChildren();

/** Get a list of the children of this object */
  List getChildren();

/** Set the children of this IGraphStructuredStackNode object */
  void setChildren(List aChildren);

/**
 * Find the index of the child with the given key starting at the given index.
 * @param aKey Object The key to look for
 * @param aStartIndex int starting index
 * @return int index of the child, -1 if not found.
 */
  int indexOfChild(Object aKey, int aStartIndex);

/**
 * Find the index of the child with the given key starting at first child.
 * @param aKey Object The key to look for
 * @return int index of the child, -1 if not found.
 */
  int indexOfChild(Object aKey);

/** Get the child at the given index. Null if not a valid index */
  IGraphStructuredStackNode getChild(int aIndex);

/** Get the child whose key matches the given aKey, null if not found */
  IGraphStructuredStackNode getChild(Object aKey);  // get the direct child

/** Add a child */
  void addChild(IGraphStructuredStackNode aChild);

/** Delete the child at the given index */
  void removeChild(int aIndex);

/** Delete the child equal to the given IGraphStructuredStackNode */
  void removeChild(IGraphStructuredStackNode aChild);

/** Delete all children with the given key */
  void removeChild(Object aKey);

/** Check if this IGraphStructuredStackNode is equal to the given aGraphStack */
  boolean isEqual(IGraphStructuredStackNode aGraphStack);

/** Check if this IGraphStructuredStackNode is equal to the given aGraphStack */
  boolean equals(Object aGraphStack);

/** Get the hashcode of this node */
  int hashCode();

/** Get this IGraphStructuredStackNode and all its children as a string */
  String toString();

/**
 * Get the descendant at the given path of keys.
 * @param aKeyList List of keys that will used as a path to find the descendants
 * @return IGraphStructuredStackNode The child, null if not found.
 */
  IGraphStructuredStackNode getDescendant(List aKeyList);

/**
 * Get the descendant at the given path of keys, the key list is slash / separated.
 * @param aKeyList String A slash eparated list of keys
 * @return IGraphStructuredStackNode The child, null if not found.
 */
  IGraphStructuredStackNode getDescendant(String aKeyList);

/** Find the first descendant with the given key */
  IGraphStructuredStackNode findDescendant(Object aKey);

/** Get a list of the ancestors of this IGraphStructuredStackNode. The first item in the list is the root */
  List getFirstRootPath();

}  // Directed graph
