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
 * Abstracts a node in a graph. The node has a key and contents. This interface
 * does not enforce any restrictions on the keys or contents.
 * <p>Copyright (c) 2006. Technology Exponent
 * @author Abdul Habra
 * @version 1.0
 */
public interface IGraphNode {
/** Set the key of this IGraphNode object */
  void setKey(Object aKey);

/** Get the key of this IGraphNode object */
  Object getKey();

/** Set the contents of this object to the given aContents */
  void setContents(Object aContents);

/** Get a reference to the contents of this object */
  Object getContents();

/** Check if the key of this IGraphNode object matches the given key */
  boolean isEqualKey(Object aKey);

/** Check if the contents of this IGraphNode object matches the given aContents */
  boolean isEqualContents(Object aContents);

}  // IGraphNode
