package com.tek271.util.internet.html.menu;

import junit.framework.TestCase;

public class MultiLevelMenuTest extends TestCase {

  public void testNoChildren() {
    MultiLevelMenu mi= new MultiLevelMenu("text", "page.html");
    assertNull(mi.getChildren());
  }
 
  public void testChildren() {
    MultiLevelMenu mi= new MultiLevelMenu("text", "page.html");
    mi.addChild("c1", "p1.html");
    mi.addChild("c2", "p2.html");
    mi.addChild("c3", "p3.html");
    assertEquals(3, mi.chilrenCount());
  }
  
  public void testDepth() {
    MultiLevelMenu root= new MultiLevelMenu("text", "page.html");
    assertEquals(0, root.depth());
    
    MultiLevelMenu child1= root.addChild("", "");
    assertEquals(1, child1.depth());
    
    MultiLevelMenu child2= child1.addChild("", "");
    assertEquals(2, child2.depth());
  }
  
  public void testParent() {
    MultiLevelMenu root= new MultiLevelMenu("text", "page.html");
    assertNull(root.getParent());
    
    MultiLevelMenu child= root.addChild("", "");
    assertEquals(root, child.getParent());
    
    assertEquals(1, root.chilrenCount());
  }
  
  public void testRoot() {
    MultiLevelMenu root= new MultiLevelMenu("text", "page.html");
    MultiLevelMenu child= root.addChild("", "");
    child= child.addChild("", "");
    child= child.addChild("", "");
    
    
    assertEquals(3, child.depth());
    assertEquals(root, child.getRoot());
  }
  
  public void testIndexAmongSiblings() {
    MultiLevelMenu root= new MultiLevelMenu("text", "page.html");
    assertEquals(0, root.indexAmongSiblings());
    
    MultiLevelMenu child= root.addChild("", "");
    assertEquals(0, child.indexAmongSiblings());
    
    child= root.addChild("", "");
    assertEquals(1, child.indexAmongSiblings());
    
    child= root.addChild("", "");
    assertEquals(2, child.indexAmongSiblings());
  }

  public void testIsLastChild() {
    MultiLevelMenu root= new MultiLevelMenu("text", "page.html");
    assertTrue(root.isFirstChild());
    assertTrue(root.isLastChild());
    
    MultiLevelMenu child= root.addChild("", "");
    assertTrue(child.isFirstChild());
    assertTrue(child.isLastChild());
    
    child= root.addChild("", "");
    
    child= root.addChild("", "");
    assertFalse(child.isFirstChild());
    assertTrue(child.isLastChild());
  }

  public void testAppendHtmlOneRowNoChildren() {
    MultiLevelMenu root= new MultiLevelMenu();
    root.addChild("text1", "link1");
    root.addChild("text2", "link2");
    // System.out.println(root.toString());
    String expected=
      "  <ul id='multiLevelTopMenu' >\n" +
      "    <li><a href='link1' >text1</a></li>\n" +
      "    <li><a class='borderLine' href='link2' >text2</a></li>\n" +
      "  </ul>\n";
    assertEquals(expected, root.toString());
  }
  
  public void testAppendHtmlOneRowWithChildren() {
    MultiLevelMenu root= new MultiLevelMenu();
    root.addChild("t1", "link1");
    MultiLevelMenu child= root.addChild("t2", "link2");
    child.addChild("t2.t1", "link");
    child.addChild("t2.t2", "link");
    
    assertEquals(2, child.chilrenCount());
    
    //System.out.println(root.toString());
    String expected=
      "  <ul id='multiLevelTopMenu' >\n" +
      "    <li><a href='link1' >t1</a></li>\n" +
      "    <li class='arrowDown'><a class='borderLine' href='link2' >t2<!--[if IE 7]><!--></a><!--<![endif]-->\n" +
      "      <!--[if lte IE 6]><table><tr><td><![endif]-->\n" +
      "      <ul>\n" +
      "        <li><a href='link' class='borderLine'>t2.t1</a></li>\n" +
      "        <li><a href='link' >t2.t2</a></li>\n" +
      "      </ul>\n" +
      "      <!--[if lte IE 6]></td></tr></table></a><![endif]-->\n" +
      "    </li>\n" +
      "  </ul>\n";
    
    assertEquals(expected, root.toString());
  }
  
  public void testAppendHtmlTree() {
    MultiLevelMenu root= new MultiLevelMenu();
    root.addChild("L1.1", "link1");
    MultiLevelMenu child= root.addChild("L1.2", "link2");
    child.addChild("L1.2/1", "link");
    child= child.addChild("L1.2/2", "link");
    
    child.addChild("L1.2/2/1", "link");
    child.addChild("L1.2/2/2", "link");
    //System.out.println(root.toString());
    
    String expected=
      "  <ul id='multiLevelTopMenu' >\n" +
      "    <li><a href='link1' >L1.1</a></li>\n" +
      "    <li class='arrowDown'><a class='borderLine' href='link2' >L1.2<!--[if IE 7]><!--></a><!--<![endif]-->\n" +
      "      <!--[if lte IE 6]><table><tr><td><![endif]-->\n" +
      "      <ul>\n" +
      "        <li><a href='link' class='borderLine'>L1.2/1</a></li>\n" +
      "        <li class='arrowRight'><a href='link' >L1.2/2<!--[if IE 7]><!--></a><!--<![endif]-->\n" +
      "          <!--[if lte IE 6]><table><tr><td><![endif]-->\n" +
      "        <ul>\n" +
      "          <li><a href='link' class='borderLine'>L1.2/2/1</a></li>\n" +
      "          <li><a href='link' >L1.2/2/2</a></li>\n" +
      "        </ul>\n" +
      "          <!--[if lte IE 6]></td></tr></table></a><![endif]-->\n" +
      "        </li>\n" +
      "      </ul>\n" +
      "      <!--[if lte IE 6]></td></tr></table></a><![endif]-->\n" +
      "    </li>\n" +
      "  </ul>\n"
      ;
    
    assertEquals(expected, root.toString());
  }

  
}
