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
 * Helper static methods for graph handling.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class GssUtils {

/**
 * Encapsulates (key, contents, parentKey) of a graph entry, a list of this object
 * can be used to construct a graph using the GssUtils.createGraph() method.
 *
 */
  public static class GraphEntry {
    public Object key;
    public Object contents;
    public Object parentKey;

    public GraphEntry(final Object aKey, final Object aContents, final Object aParentKey) {
      key= aKey;
      contents= aContents;
      parentKey= aParentKey;
    }
  }

/** provides a method to create a IGraphStructuredStackNode object */
  public interface IGssNodeFactory {
    IGraphStructuredStackNode create();
  }

  private static IGraphStructuredStackNode getNode(final MapOfGraphNode aMap,
                                                   final Object aKey) {
    return (IGraphStructuredStackNode) aMap.getNode(aKey);
  }

/**
 * Given a list of GraphEntry objects, create the appropriate graph nodes.
 * @param aGraphEntryList List of GraphEntry objects. No order is required.
 * @param aFactory IGssNodeFactory Used to create IGraphStructuredStackNode objects
 * @return List of IGraphStructuredStackNode root nodes.
 */
  public static List createGraph(final List aGraphEntryList,
                                 final IGssNodeFactory aFactory) {
    // create a map of nodes
    int n= aGraphEntryList.size();
    MapOfGraphNode map= new MapOfGraphNode(n);
    for (int i=0; i<n; i++) {
      GraphEntry ge= (GraphEntry) aGraphEntryList.get(i);
      IGraphStructuredStackNode node= getNode(map, ge.key);
      if (node==null) {
        node= aFactory.create();
        node.setKey(ge.key);
        map.put(node);
      }
      if (ge.contents != null && node.getContents() == null) {
        node.setContents(ge.contents);
      }
    }  // for

    // list of roots
    List roots= new ArrayList();

    // assign parents for each node
    for (int i=0; i<n; i++) {
      GraphEntry ge= (GraphEntry) aGraphEntryList.get(i);
      IGraphStructuredStackNode node= getNode(map, ge.key);
      if (ge.parentKey==null) {
        roots.add(node);
        continue;
      }

      IGraphStructuredStackNode parent= getNode(map, ge.parentKey);
      if (parent==null) {
        roots.add(node);
      } else {
        node.addParent(parent);
      }
    }  // for

    return roots;
  } // createGraph

/**
 * Sort a list of IGraphStructuredStackNode and all their descendants
 * @param aNodeList List of IGraphStructuredStackNode objects
 * @param aComparator The comparator to determine the order of the list. A null
 *        value indicates that the elements' natural ordering should be used.
 */
  public static void sortNodeList(final List aNodeList,
                                  final Comparator aComparator) {
    if (aNodeList==null) return;
    Collections.sort(aNodeList, aComparator);

    for(Iterator i= aNodeList.iterator(); i.hasNext();) {
      IGraphStructuredStackNode child= (IGraphStructuredStackNode) i.next();
      sortChildren(child, aComparator);
    }
  }  // sortNodeList


/**
 * Sort the children of the given node and all their descendants
 * @param aNode IGraphStructuredStackNode Node to sort its descendants
 * @param aComparator The comparator to determine the order of the list. A null
 *        value indicates that the elements' natural ordering should be used.
 */
  public static void sortChildren(final IGraphStructuredStackNode aNode,
                                  final Comparator aComparator) {
    if (aNode==null) return;
    if (! aNode.hasChildren()) return;

    sortNodeList(aNode.getChildren(), aComparator);
  }  // sortChildren


}  // GssUtils
