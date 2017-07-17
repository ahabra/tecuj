/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004,2006  Abdul Habra
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
package com.tek271.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.tek271.util.collections.list.ListOfString;

/**
 Handles events received from SAX parser to populate an <code>XmlTree</code>
 object.
 <p>Note that this class has a package scope and is not intended to be used by classes
 other than <code>XmlTree</code>.</p>
 <p>This parser will ignore all comments in the XML data.
 <p>This class was refactored in 2006.08.15 to extend DefaultHandler instead of
 HandlerBase which was deprecated.
 * <p>Copyright (c) 2004, 2006 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
class XmlTreeParser extends DefaultHandler {
  private XmlTree pRoot;
  private XmlTree pCurrent;
  private boolean isPreserveSpace;

/** Create a XmlTreeParser object. */
  public XmlTreeParser(boolean isPreserveSpace) {
    pRoot= XmlTree.create(null, null, null, isPreserveSpace);
    this.isPreserveSpace= isPreserveSpace;
    pCurrent= pRoot;
  }

  /** Callback method called by SAX parser when an element start tag is found */
  public void startElement(String aUri, String aLocalName,
                           String aQName, Attributes aAttributes) {
    ListOfString atts= XmlUtil.attributeList2List(aAttributes);
    XmlTree parent= pCurrent;
    pCurrent= XmlTree.create(aQName, atts, parent, isPreserveSpace);
    parent.addChild(pCurrent);
  }

  /** Callback method called by SAX parser when an element end tag is found */
  public void endElement(String aUri, String aLocalName, String aQName) {
    pCurrent= pCurrent.getParent();
  }

  /** Callback method called by SAX parser when an element data is found */
  public void characters(char[] aChars, int aStart, int aLength) {
    if (aChars==null || aChars.length==0) return;
    String s= new String(aChars, aStart, aLength);  // .trim();
    pCurrent.appendText(s);
  } // characters

  /**
   * Get the root of the parsed XmlTree.
   * @return XmlTree null if the no xml has been parsed
   */
    public XmlTree getRoot() {
      // The pRoot object is a place holder that has one child which is the actual root
      if (!pRoot.hasChildren()) return null;
      XmlTree r= pRoot.getChild(0);
      r.setParent(null);
      return r;
    } // getRoot

}
