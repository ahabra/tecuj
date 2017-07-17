/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2008  Abdul Habra
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

package com.tek271.util.internet.html.menu;

import java.io.IOException;
import java.util.List;

import com.tek271.util.collections.CollectionUtility;
import com.tek271.util.io.FileIO;
import com.tek271.util.log.ILogger;
import com.tek271.util.xml.XmlTree;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Create a MultiLevelMenu from XML.
 * An example XML could be:<pre>
&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?>
&lt;menu>
&nbsp; &lt;mi text='Home' href='home.html' />
&nbsp;  &lt;mi text='Affiliate' href='http://www.google.com' target='_blank' />
&nbsp;  &lt;mi text='Books' href='books.html'>
&nbsp;    &lt;mi text='Fiction' href='Fiction.html'>
&nbsp;      &lt;mi text='Mystery' href='Mystry.html'>
&nbsp;        &lt;mi text='Cold Prey' href='coldPrey.html' />
&nbsp;        &lt;mi text='Silent Prey' href='silentPrey.html' />
&nbsp;        &lt;mi text='Blood Memory' href='bloodMemory.html' />
&nbsp;      &lt;/mi>
&nbsp;    &lt;/mi>
&nbsp;  &lt;/mi>
&lt;/menu>
 * </pre>
 * 
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class XmlMultiLevelMenu {
  
  private static XmlTree createTree(ILogger logger, String xml) {
    return XmlTree.create(xml, false, logger);
  }
  
  private static MultiLevelMenu createMenu(XmlTree xmlTree) {
    if (xmlTree==null) return null;
    
    String text= xmlTree.getAttribute("text");
    String href= xmlTree.getAttribute("href");
    String target= xmlTree.getAttribute("target");
    
    MultiLevelMenu menu= new MultiLevelMenu(text, href, target);
    List children= xmlTree.getChildren("mi");
    if (CollectionUtils.isEmpty(children)) return menu;
    
    for (int i=0, n=children.size(); i<n; i++) {
      XmlTree xmlChild= (XmlTree) children.get(i);
      MultiLevelMenu menuChild= createMenu(xmlChild);
      menu.addChild(menuChild);
    }
    
    return menu;
  }
  
/**
 * Create a MultiLevelMenu from the given xml string
 * @param logger an ILogger
 * @param xml representation of the menu
 * @return a MultiLevelMenu object
 */  
  public static MultiLevelMenu create(ILogger logger, String xml) {
    XmlTree xmlTree= createTree(logger, xml);
    MultiLevelMenu menu= createMenu(xmlTree);
    return menu;
  }
  
/**
 * Create a MultiLevelMenu from the given file
 * @param logger an ILogger
 * @param fileName name of a file which contains the xml for a menu
 * @return a MultiLevelMenu object
 * @throws IOException if file access fails.
 */
  public static MultiLevelMenu createFromFile(ILogger logger, String fileName)
         throws IOException {
    String xml= FileIO.read(fileName);
    return create(logger, xml);
  }
  
}
