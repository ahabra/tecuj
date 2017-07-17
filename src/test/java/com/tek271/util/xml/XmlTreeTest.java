package com.tek271.util.xml;

import java.util.List;

import junit.framework.TestCase;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.log.SimpleConsoleLogger;

/**
 * <p>Copyright (c) 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class XmlTreeTest extends TestCase {

  public void testTree() {
    String xml= "<a> <b>a <b>b</b> </b> <b>c</b> </a>";
    XmlTree xt= XmlTree.create(xml, false, SimpleConsoleLogger.LOGGER);
    assertEquals( 2, xt.childrenCount() );
    List nodes= xt.getDescendants("b", true);
    assertEquals(3, nodes.size());

    ListOfString list= XmlTree.extractText(nodes);
    assertEquals(3, list.size());

    XmlTree nd= xt.find("b");
    assertNotNull(nd);
  }

  public void testTreePreserveSpace() {
    String xml= "<a>  </a>";
    XmlTree xt= XmlTree.create(xml, false, SimpleConsoleLogger.LOGGER, true);
    assertEquals("  ", xt.getText());

    xt= XmlTree.create(xml, false, SimpleConsoleLogger.LOGGER, false);
    assertEquals("", xt.getText());
    
    xml= "<a><b>   </b> </a>";
    xt= XmlTree.create(xml, false, SimpleConsoleLogger.LOGGER, true);
    assertEquals(" ", xt.getText());
    assertEquals("   ", xt.getChild(0).getText() );
    
  }
  
  
  public void testAttributesWithQuote() {
    String xml= "<t a1=\"v1\" a2='\"v2\"' a3=\"'v3'\">data</t>";
    XmlTree xt= XmlTree.create(xml, false, SimpleConsoleLogger.LOGGER);
    String newXml= xt.toString(false);
    assertEquals(xml, newXml);
  }
  
  private static final String XML= 
    "<root>" +
      "<lib>" +
        "<book>" +
          "<author>" +
            "<name>a1</name>" +
          "</author>" +
        "</book>" + 
        "<book>" +
          "<author>" +
            "<name>a2</name>" +
            "<age>50</age>" +
          "</author>" +
        "</book>" + 
        "<book>" +
          "<author>" +
            "<name>a3</name>" +
          "</author>" +
        "</book>" + 
        "<binder>" +
          "<address>add1</address>" +
        "</binder>" +
        "<magazine>" +
          "<agency>ABCD</agency>" +
        "</magazine>" +
      "</lib>" +
    "</root>";

  private static XmlTree createTree() {
    return XmlTree.create(XML, false, SimpleConsoleLogger.LOGGER);
  }
  
  public void testGetChildren() {
    XmlTree xml= createTree();
    String path= "lib/book/author/name";
    List children= xml.getChildren(path);
    assertEquals(1, children.size());
    XmlTree child= (XmlTree) children.get(0);
    assertEquals("a1", child.getText() );
  }
  
  public void testFindAllChildren() {
    XmlTree xml= createTree();
    String path= "lib/book/author/name";
    List children= xml.findAllChildren(path, false);
    assertNotNull(children);
    assertTrue(children.size() > 0);
    assertEquals(3, children.size());
    
    path= "lib/book/author/age";
    children= xml.findAllChildren(path, false);
    assertNotNull(children);
    assertTrue(children.size() > 0);
    assertEquals(1, children.size());
  }
  
  public void testFindAllChildrenForNonExistingPath() {
    XmlTree xml= createTree();
    List children= xml.findAllChildren("a/b/c", false);
    assertEquals(null, children);

    children= xml.findAllChildren("lib/b/c", false);
    assertEquals(null, children);

    children= xml.findAllChildren("lib/book/c", false);
    assertEquals(null, children);
  }
  
  public void testFindAllChildrenRegEx() {
    XmlTree xml= createTree();
    String path= "lib/b.*";
    List children= xml.findAllChildren(path, true);
    assertEquals(4, children.size());
    
    path= "lib/b.*/a.*";
    children= xml.findAllChildren(path, true);
    assertEquals(4, children.size());

    path= "lib/.*/a.*";
    children= xml.findAllChildren(path, true);
    assertEquals(5, children.size());
  }
  
  public void testClone() {
    XmlTree xml= createTree();
    XmlTree clon= (XmlTree) xml.clone();
    
    assertEquals(xml.toString(), clon.toString());
  }
  
  
 }
