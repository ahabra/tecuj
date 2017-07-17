package com.tek271.util.collections.tree;

import java.util.List;

import junit.framework.TestCase;

public class TreeOfStringTest extends TestCase {
  public void test1() {
    ITree earth= new TreeOfString(null, "Earth");
    ITree africa= new TreeOfString(earth, "Africa");
    ITree america= new TreeOfString(null, "America");
    america.setParent(earth);
    
    ITree canada= new TreeOfString(america, "Canada");
    ITree usa= new TreeOfString(america, "USA");
    ITree mexico= new TreeOfString(america, "Mexico");
    
    assertEquals(earth, africa.getParent());
    assertEquals(earth, america.getParent());
    assertEquals(5, earth.descendantsCount());
    assertEquals(america, canada.getParent());
    assertEquals(america, usa.getParent());
    assertEquals(america, mexico.getParent());
    
    ITree found= (ITree) earth.getDescendant("America/USA");
    assertEquals(found.getKey(), "USA");

    found= (ITree) earth.getChild("America").getChild("Mexico");
    found.setKey("Mehico");
    found= (ITree) earth.getDescendant("America/Mehico");
    assertEquals(found.getKey(), "Mehico");
    
    List ans= canada.getFirstRootPath();
    assertEquals(2, ans.size());
    assertEquals(earth, ans.get(0));
  }
}
