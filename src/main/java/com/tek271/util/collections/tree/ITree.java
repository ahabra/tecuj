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
 * Defines the interface of a generic tree node. A tree node consists of <ol>
 * <li>Key: must be unique for children of the same parent
 * <li>Contents
 * <li>List of children, each child is a also an ITree. (a recursive structure)
 * <li>A reference to the parent ITree node of this ITree.
 * </ol>
 * <p>Copyright (c) 2006. Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public interface ITree extends IGraphStructuredStackNode {

/**
 * Add a parent of this ITree object. Overrides the method in IGraphStructuredStackNode.
 * @param aParent IGraphStructuredStackNode. Must be an object that implements the
 * ITree interface.
 */
  void addParent(IGraphStructuredStackNode aParent);

/** Get the parent of this ITree */
  ITree getParent();

/** Set the parent of this ITree object */
  void setParent(ITree aParent);


}  // ITree
