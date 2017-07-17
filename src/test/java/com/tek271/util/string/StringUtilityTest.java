package com.tek271.util.string;

import junit.framework.TestCase;

public class StringUtilityTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testRemoveBetweenIndex() {
    String source= "0123abc456";
    String s= StringUtility.removeBetweenOnce(source, 4, 6);
    assertEquals("0123456", s);
    s= StringUtility.removeBetweenOnce(source, 4, 30);
    assertEquals("0123", s);
    s= StringUtility.removeBetweenOnce(source, -4, 2);
    assertEquals("3abc456", s);
    s= StringUtility.removeBetweenOnce(source, 4, 2);
    assertEquals("0123abc456", s);
    s= StringUtility.removeBetweenOnce(source, 2, 2);
    assertEquals("013abc456", s);
    s= StringUtility.removeBetweenOnce(source, 0, 32);
    assertEquals("", s);
    s= StringUtility.removeBetweenOnce(null, 0, 32);
    assertEquals("", s);
  }

  public void testRemoveBetweenStartEnd() {
    String source= "0123abc456";
    String s= StringUtility.removeBetweenOnce(source, "a", "c", true, true);
    assertEquals("0123456", s);
    s= StringUtility.removeBetweenOnce(source, "3ab", "45", true, true);
    assertEquals("0126", s);
    s= StringUtility.removeBetweenOnce(source, "ab", "45x", true, true);
    assertEquals("0123abc456", s);
    s= StringUtility.removeBetweenOnce(source, "", "45", true, true);
    assertEquals("0123abc456", s);
    s= StringUtility.removeBetweenOnce(source, "3", "45", false, true);
    assertEquals("0123456", s);
    s= StringUtility.removeBetweenOnce(source, "123", "45", false, true);
    assertEquals("0123456", s);
  }

  public void testRemoveBetweenAll() {
    String source= "hello<<tag>>World<<test>>Earth";
    String start= "<<";
    String end= ">>";
    String s= StringUtility.removeBetweenAll(source, start, end, 0, true, true);
    assertEquals("helloWorldEarth", s);
    s= StringUtility.removeBetweenAll(source, start, end, 0, false, true);
    assertEquals("hello<<>>World<<>>Earth", s);
    
  }
  
  public void testReplaceBetweenIndex() {
    String source= "01234567";
    String s= StringUtility.replaceBetweenOnce(source, "ABCDE", 3, 5);
    assertEquals("012ABCDE67", s);
    s= StringUtility.replaceBetweenOnce(source, "345", 3, 5);
    assertEquals(source, s);
    assertTrue(source==s);
  }

  public void testReplaceBetweenStartEnd() {
    String source= "hello<<tag>>World";
    String start= "<<";
    String end= ">>";
    String s= StringUtility.replaceBetweenOnce(source, "ABCD", start, end, true, true);
    assertEquals("helloABCDWorld", s);
  }
 
  
  public void testReplaceBetweenAll() {
    String source= "hello<1>World<2>Earth<3>Moon";
    String start= "<";
    String end= ">";
    String s= StringUtility.replaceBetween(source, "*", start, end, 0, -1, true, true);
    String expected= "hello*World*Earth*Moon";
    assertEquals(expected, s);
    
    s= StringUtility.replaceBetween(source, "*", start, end, 0, -1, false, true);
    expected= "hello<*>World<*>Earth<*>Moon";
    assertEquals(expected, s);
    
    s= StringUtility.replaceBetween(source, "*", start, end, 0, 1, true, true);
    expected= "hello*World<2>Earth<3>Moon";
    assertEquals(expected, s);

    s= StringUtility.replaceBetween(source, "*", start, end, 0, 53, true, true);
    expected= "hello*World*Earth*Moon";
    assertEquals(expected, s);
    
    s= StringUtility.replaceBetweenAll(source, "*", start, end);
    expected= "hello*World*Earth*Moon";
//    System.out.println("expected: " + expected);
//    System.out.println("  actual: " + s);
    assertEquals(expected, s);
    
  }
  
  public void testSubstringAfterOpenClose() {
    String source= "foo[bar]lan";
    String open= "[";
    String close= "]";
    String sub= StringUtility.substringAfterOpenClose(source, open, close);
    assertEquals("lan", sub);
    
    source= "foo<!--bar-->lan";
    open= "<!--";
    close= "-->";
    sub= StringUtility.substringAfterOpenClose(source, open, close);
    assertEquals("lan", sub);
    
    source= "foo<!--bar-->";
    sub= StringUtility.substringAfterOpenClose(source, open, close);
    assertEquals(null, sub);

    source= "foo<!--bar--lan";
    sub= StringUtility.substringAfterOpenClose(source, open, close);
    assertEquals(null, sub);

    source= "<!--hello-->foo<!--bar-->lan";
    sub= StringUtility.substringAfterOpenClose(source, open, close, 12);
    assertEquals("lan", sub);

    source= "<!--hello-->foo<!--bar-->lan";
    sub= StringUtility.substringAfterOpenClose(source, open, close, source.length()-1);
    assertEquals(null, sub);
  }
  
  public void testMutate() {
    String original= new String( "1111" );
    StringUtility.mutate(original, "2222", ' ');
    assertEquals("2222", original);
    
    StringUtility.mutate(original, "2", 'x');
    assertEquals("2xxx", original);
    
    StringUtility.mutate(original, "33333333", 'x');
    assertEquals("3333", original);
    
    StringUtility.mutate(original, "", 'x');
    assertEquals("xxxx", original);
    
    StringUtility.mutate(original, null, 'x');
    assertEquals("xxxx", original);
  }
  
  public void testMutateInterned() {
    String original= "11";
    String interned= "11";
    
    StringUtility.mutate(original, "22");
    assertEquals("22", original);
    
    // note that we did NOT change the value of interned, non the less, it has changed
    // because the JVM 'interned' original with interned and modified it.
    // This behavior is not guaranteed to work on all JVMs and may break this test. 
    assertEquals("22", interned);
  }

  public void testSelectOption() {
    String text= "[He|They] ha[s|ve] arrived";
    String expected= "They have arrived";
    String actual= StringUtility.selectOption(text, 1, "[", "]", '|');
    assertEquals(expected, actual);
  }
  
  public void testPluralize() {
    int count=1;
    String text= "I have " + count + " dog{|s}";
    String actual= StringUtility.pluralize(false, text);
    assertEquals("I have 1 dog", actual);
    
    count= 3;
    text= "I have " + count + " dog{|s}";
    actual= StringUtility.pluralize(true, text);
    assertEquals("I have 3 dogs", actual);
  }

  public void testLeftAndSoOn() {
    String suffix= "...";
    String actual= StringUtility.leftAndSoOn("12345", 5, suffix);
    assertEquals("12345", actual);
    
    actual= StringUtility.leftAndSoOn("1234567", 6, suffix);
    assertEquals("123...", actual);
    
    actual= StringUtility.leftAndSoOn("1234567", 2, suffix);
    assertEquals("..", actual);

    actual= StringUtility.leftAndSoOn("1234567", 2, null);
    assertEquals("12", actual);
  }
  
}
