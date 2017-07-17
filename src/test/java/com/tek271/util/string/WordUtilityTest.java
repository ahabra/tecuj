package com.tek271.util.string;

import junit.framework.TestCase;

public class WordUtilityTest extends TestCase {

  public void testCamelcaseToWords() {
    assertEquals("Camel Case", WordUtility.camelcaseToWords("CamelCase"));
    assertEquals("camel Case", WordUtility.camelcaseToWords("camelCase"));
  }

  public void testIsSimilarCase() {
    assertTrue(WordUtility.isSimilarCase('a', 'a'));
    assertTrue(WordUtility.isSimilarCase('a', 'b'));
    assertTrue(WordUtility.isSimilarCase('A', 'A'));
    assertTrue(WordUtility.isSimilarCase('A', 'B'));
    assertTrue(WordUtility.isSimilarCase('A', 'Z'));
    assertFalse(WordUtility.isSimilarCase('a', '1'));
    assertFalse(WordUtility.isSimilarCase('A', '1'));
    assertFalse(WordUtility.isSimilarCase('a', '-'));
    assertFalse(WordUtility.isSimilarCase('A', '/'));
    
  }
  
}
