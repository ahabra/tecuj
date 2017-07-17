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
 * A map of IGraphNode objects. The key of the map is the node's key, and the value
 * is the node itself.
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class MapOfGraphNode extends HashMap {

  public MapOfGraphNode() {}

  public MapOfGraphNode(final int aInitialCapacity) {
    super(aInitialCapacity);
  }


  public MapOfGraphNode(final MapOfGraphNode aMap) {
    super(aMap);
  }

  public IGraphNode getNode(final Object aKey) {
    return (IGraphNode) get(aKey);
  }

  public Object put(final IGraphNode aNode) {
    return put(aNode.getKey(), aNode);
  }

  // ??? move these to test class

  private static class TestNode implements IGraphNode {
    private String pKey;
    private String pContents;

    public TestNode(String aKey, String aContents) { pKey=aKey; pContents= aContents; }

    public void setKey(Object aKey) { pKey= (String) aKey; }
    public Object getKey() { return pKey; }
    public void setContents(Object aContents) { pContents= (String) aContents; }
    public Object getContents() { return pContents; }
    public boolean isEqualKey(Object aKey) { return pKey.equals(aKey); }
    public boolean isEqualContents(Object aContents) { return pContents.equals(aContents); }

    public String toString() { return pContents; }

  }

  /** For testing */
  public static void main(String[] a) {
    MapOfGraphNode map= new MapOfGraphNode();

    map.put( new TestNode("Bob", "Director") );
    map.put( new TestNode("Lee", "Manager") );
    map.put( new TestNode("Abdul", "Programmer") );
    map.put( new TestNode("Tom", "Programmer") );
    map.put( new TestNode("Tom", "Programmer2") );


    MapOfGraphNode map2= new MapOfGraphNode(map);

    map.remove("Tom");

    System.out.println(map);
    System.out.println(map2);
  }

} // MapOfGraphNode
