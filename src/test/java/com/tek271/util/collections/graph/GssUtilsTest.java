package com.tek271.util.collections.graph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.tek271.util.collections.graph.GssUtils.GraphEntry;
import com.tek271.util.collections.graph.GssUtils.IGssNodeFactory;

import junit.framework.TestCase;

public class GssUtilsTest extends TestCase {
  private static List testGraph() {
    GraphEntry[] ge= {
        new GraphEntry("1", "1", null),
        new GraphEntry("2", "2", "1"),
        new GraphEntry("4", "4", "1"),
        new GraphEntry("2", "2", "4"),
        new GraphEntry("5", "5", "2"),
        new GraphEntry("3", "3", "2"),
    };
    List list= Arrays.asList(ge);

    class NodeFactory implements IGssNodeFactory {
      public IGraphStructuredStackNode create() {
        return new GraphStructuredStackNode();
      }
    }
    
    NodeFactory fac= new NodeFactory();
    return GssUtils.createGraph(list, fac);
  }  // testGraph

  public void test1() {
    List roots= testGraph();
    IGraphStructuredStackNode root= (IGraphStructuredStackNode) roots.get(0);
    IGraphStructuredStackNode three= root.getDescendant("2/3");
    IGraphStructuredStackNode five= root.findDescendant("5");
    three.setContents("three");
    five.setContents("five");
    // IGraphStructuredStackNode two= three.getParent(0);

    System.out.println(roots);
  }  // test1

  public void test2() {

    class Comp implements Comparator {
      public int compare(Object o1, Object o2) {
        GraphStructuredStackNode n1= (GraphStructuredStackNode) o1;
        GraphStructuredStackNode n2= (GraphStructuredStackNode) o2;
        String k1= (String) n1.getKey();
        String k2= (String) n2.getKey();
        return k1.compareTo(k2);
      }

      public boolean equals(Object obj) {
        return compare(this, obj)==0;
      }
      public int hashCode() { return 7; }
    }
    Comparator comp= new Comp();

    List roots= testGraph();
    IGraphStructuredStackNode root= (IGraphStructuredStackNode) roots.get(0);
    System.out.println(roots);
    GssUtils.sortChildren(root, comp);
    System.out.println(roots);
  }

}
