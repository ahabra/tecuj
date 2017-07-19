package com.tek271.util.internet.html.menu;

import java.io.IOException;

import junit.framework.TestCase;

import com.tek271.util.log.SimpleConsoleLogger;
import com.tek271.util.string.StringUtility;

public class XmlMultiLevelMenuTest extends TestCase {

  private static final String XML=
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<menu>\n" +
    "  <mi text='Home' href='home.html' />\n" +
    "  <mi text='Affiliate' href='http://www.google.com' target='_blank' />\n" +
    "  <mi text='Books' href='books.html'>\n" +
    "    <mi text='Fiction' href='Fiction.html'>\n" +
    "      <mi text='Mystery' href='Mystry.html'>\n" +
    "        <mi text='Cold Prey' href='coldPrey.html' />\n" +
    "        <mi text='Silent Prey' href='silentPrey.html' />\n" +
    "        <mi text='Blood Memory' href='bloodMemory.html' />\n" +
    "        <mi text='Lettuce Prey' />\n" +
    "      </mi>\n" +
    "    </mi>\n" +
    "  </mi>\n" +
    "</menu>";
  
  public void testCreate() {
    MultiLevelMenu menu= XmlMultiLevelMenu.create(SimpleConsoleLogger.LOGGER, XML);
    assertEquals(3, menu.chilrenCount());
    assertEquals("Home", menu.getChild(0).text );
    assertEquals("home.html", menu.getChild(0).href );
    MultiLevelMenu lettuce= menu.getChild(2).getChild(0).getChild(0).getChild(3);
    assertEquals("Lettuce Prey", lettuce.text);
    
    String html= lettuce.toString();
    assertEquals("#noWhere",  StringUtility.substringBetween(html, "href='", "'" ));
    
  }

}
