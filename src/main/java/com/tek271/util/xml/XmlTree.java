/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.tek271.util.collections.CollectionUtility;
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.io.FileIO;
import com.tek271.util.log.ILogger;
import com.tek271.util.log.SimpleConsoleLogger;
import com.tek271.util.string.StringUtility;

/**
 A tree data structure that represents an XML document in a simplified DOM style.
 It is a wrapper around the XML libraries that come with J2SE.
 <p>To use this class, use the static methods <code>create()</code> or
 <code>create<i>Xxx</i>()</code> which will return an
 <code>XmlTree</code> object.</p>
 <p>The program uses SAX to parse the XML document and build the tree. Some of its features:
<ol>
 <li>You can parse an XML document from a String, InputStream, Reader, File, or URL.</li>
 <li>Each <code>XmlTree</code> has a tag, optional text, an optional
     list of attributes, and an optional list of children.</li>
 <li>The children are <code>XmlTree</code> objects also. This structure is recursive.</li>
 <li>You can add, get, or delete children by index or path (slash / separated).</li>
 <li>You can get/set the tag, attributes, and text of an <code>XmlTree</code> object.</li>
 <li>You can get the string representation of an <code>XmlTree</code> object.</li>
</ol>

 * <p>Copyright (c) 2004-2007 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.1
 */
public class XmlTree implements Cloneable {
  private static final int pINDENT_SIZE= 2;  // used with toString()
  private static final char pSEPARATOR_CHAR= '/';  // separates tag names
  private static final String pSEPARATOR_STRING= String.valueOf(pSEPARATOR_CHAR);

  private StringBuffer pTextBuffer;
  private String pTag;
  private ListOfString pAttributes;
  private List pChildren;
  private XmlTree pParent;
  private String pOriginalXml;  // populated only for the root node
  private String pVersion= "1.0";
  private String pEncoding= StringUtility.EMPTY;
  private boolean isPreserveSpace;

  private XmlTree() {}

/**
 * Create an XmlTree node without adding this node to its parent.
 * @param aTag String
 * @param aAttributes ListOfString A name=value list of attributes. Can be null.
 * @param aParent XmlTree the parent of this node. Can be null
 * @return XmlTree the created node.
 */
  public static XmlTree create(final String aTag,
                               final ListOfString aAttributes,
                               final XmlTree aParent) {
    return create(aTag, aAttributes, aParent, false);
  }  // create

/**
 * Create an XmlTree node without adding this node to its parent.
 * @param aTag String
 * @param aAttributes ListOfString A name=value list of attributes. Can be null.
 * @param aParent XmlTree the parent of this node. Can be null
 * @param isPreserveSpace should spaces in the data of an element be preserved
 * @return XmlTree the created node.
 */
  public static XmlTree create(final String aTag,
                               final ListOfString aAttributes, 
                               final XmlTree aParent,
                               final boolean isPreserveSpace) {
    XmlTree xt = new XmlTree();
    xt.pTag = aTag;
    xt.pAttributes = aAttributes;
    xt.pParent = aParent;
    xt.isPreserveSpace= isPreserveSpace;
    return xt;
  } // create

/**
 * Create an XmlTree node and add it to its parent.
 * @param aTag String
 * @param aAttributes ListOfString A name=value list of attributes. Can be null.
 * @param aParent XmlTree the parent of this node. Can be null
 * @return XmlTree the created node.
 */
  public static XmlTree createAndAddToParent(final String aTag,
                                             final ListOfString aAttributes,
                                             final XmlTree aParent) {
    XmlTree xt= create(aTag, aAttributes, aParent);
    if (aParent != null) {
      aParent.addChild(xt);
    }
    return xt;
  }  // createAndAddToParent


/**
 * Create an XmlTree object from a string.
 * @param aXml String The xml string to parse.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger ILogger A logger used for loging parsing errors.
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree create(final String aXml,
                               final boolean aValidate,
                               final ILogger aLogger) {
    return create(aXml, aValidate, aLogger, false);
  } // create

/**
 * Create an XmlTree object from a string.
 * @param aXml String The xml string to parse.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger ILogger A logger used for loging parsing errors.
 * @param isPreserveSpace should spaces in the data of an element be preserved
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree create(final String aXml, final boolean aValidate,
                               final ILogger aLogger,
                               final boolean isPreserveSpace) {
    if (StringUtility.isBlank(aXml)) return null;
    XmlTreeParser parser = new XmlTreeParser(isPreserveSpace);
    boolean ok = XmlUtil.parseSax(aXml, parser, aValidate, aLogger);
    if (!ok) return null;

    XmlTree root = parser.getRoot();
    root.pOriginalXml = aXml;
    return root;
  } // create

/**
 * Create an XmlTree object from a Reader.
 * @param aXml Reader A reader that returns the xml to parse.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger ILogger A logger used for loging parsing errors.
 * @throws IOException if failed reader the Reader object.
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree create(final Reader aXml,
                               final boolean aValidate,
                               final ILogger aLogger) throws IOException {
    ListOfString xml= new ListOfString();
    xml.readFromReader(aXml);
    return create(xml.toString(), aValidate, aLogger);
  } // create

/**
 * Create an XmlTree object from an InputStream.
 * @param aXml InputStream An InputStream that returns the xml to parse.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger ILogger A logger used for loging parsing errors.
 * @throws IOException if failed reader the InputStream object.
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree create(final InputStream aXml,
                               final boolean aValidate,
                               final ILogger aLogger) throws IOException {
    ListOfString xml= new ListOfString();
    xml.readFromStream(aXml);
    return create(xml.toString(), aValidate, aLogger);
  } // create

/**
 * Create an XmlTree object from a file.
 * @param aFileName String The name of a file that contains xml data.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *   they are parsed.
 * @param aLogger ILogger A logger used for loging parsing errors.
 * @throws IOException if cannot access the file.
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree createFromFile(final String aFileName,
                                       final boolean aValidate,
                                       final ILogger aLogger) throws IOException {
    if (aFileName==null) return null;
    String xml=FileIO.read(aFileName);
    return create(xml, aValidate, aLogger);
  } // createFromFile


/**
 * Create an XmlTree object from a URL.
 * @param aUrl String URL to the xml to parse.
 * @param aValidate boolean Specifies that the parser will validate documents as
 *         they are parsed.
 * @param aLogger ILogger  A logger used for loging parsing errors.
 * @throws IOException if cannot access the url.
 * @return XmlTree null if error. if ok return the root of the xml tree.
 */
  public static XmlTree createFromUrl(final String aUrl,
                                      final boolean aValidate,
                                      final ILogger aLogger) throws IOException {
    if (aUrl==null) return null;
    URL url= new URL(aUrl);
    return create(url.openStream(), aValidate, aLogger);
  } // createFromFile


/**
 * Append aText the the pTextBuffer stringBuffer
 * @param aText String The text to append.
 * Note that this method has a package scope, and is not exposed to this class users.
 */
  void appendText(String aText) {
    if (StringUtility.isEmpty(aText)) return;
    if (! isPreserveSpace) {
      if (StringUtility.isBlank(aText)) return;
    }
    aText= checkControlChars(aText);
    if (pTextBuffer==null) pTextBuffer= new StringBuffer(aText);
    else pTextBuffer.append(aText);
  } // appendText

  private static final char[] pCONTROL_CHARS = {'"', '&', '\'', '<', '>' };
  private static final String[] pCONTROL_STRINGS = {"&quot;", "&amp;", "&apos;",
                                                    "&lt;",   "&gt;"};

/** Check if aText is < or ' or ... */
  private String checkControlChars(final String aText) {
    if (aText.length()!=1) return aText;

    char c= aText.charAt(0);
    for (int i=0, n=pCONTROL_CHARS.length; i<n; i++) {
      if (pCONTROL_CHARS[i] == c) return pCONTROL_STRINGS[i];
    }
    return aText;
  }  // checkControlChars

/** Get the text of this node in the XmlTree */
  public String getText() {
    if (pTextBuffer==null) return StringUtility.EMPTY;
    return pTextBuffer.toString();
  }

/** Set the text of this node in the XmlTree */
  public void setText(final String aText) {
    pTextBuffer= new StringBuffer(StringUtility.defaultString(aText));
  }

/** Get the tag of this node in the XmlTree */
  public String getTag() {
    return pTag;
  }

/** Set the tag of this node in the XmlTree */
  public void setTag(final String aTag) {
    pTag= aTag;
  }

/**
 * Get the attributes of this node in the XmlTree as a ListOfString object with
 * name=value items.
 */
  public ListOfString getAttributes() {
    return pAttributes;
  }

/**
 * Set the attributes of this node in the XmlTree as a ListOfString object with
 * name=value items.
 */
  public void setAttributes(final ListOfString aAttributes) {
    pAttributes= aAttributes;
  }

/**
 * Get a list of children for this node in the XmlTree. The list will have items
 * of XmlTree objects
 */
  public List getChildren() {
    return pChildren;
  }

/**
 * Set the list of children for this node in the XmlTree. The list will have items
 * of XmlTree objects
 */
  public void setChildren(final List aChildren) {
    pChildren= aChildren;
  }

/** Get the parent node for this object */
  public XmlTree getParent() {
    return pParent;
  }

/** Set the parent node for this object */
  public void setParent(final XmlTree aParent) {
    pParent= aParent;
  }

  private void appendHeader(final StringBuffer aBuffer) {
    aBuffer.append("<?xml ");
    if (StringUtility.isNotBlank(pVersion))
      aBuffer.append("version=\"").append(pVersion).append("\" ");
    if (StringUtility.isNotBlank(pEncoding))
      aBuffer.append("encoding=\"").append(pEncoding).append("\" ");
    aBuffer.append("?>").append(StringUtility.NEW_LINE);
  }  // appendHeader


/** Convert a list of XmlTree objects into a string */
  public static String toString(final List aChildren) {
    if (CollectionUtility.isNullOrEmpty(aChildren)) return StringUtility.EMPTY;

    StringBuffer buf= new StringBuffer(128);
    for(int i=0, n= aChildren.size(); i<n; i++) {
      if (i>0) buf.append(StringUtility.NEW_LINE);
      XmlTree child= (XmlTree) aChildren.get(i);
      buf.append(child.toString(false) );
    }
    return buf.toString();
  }

/**
 * Convert this object into an XML string.
 * @param aPutXmlHeader boolean control if the standard xml header should be included.
 * @return String The xml representation of this object.
 */
  public String toString(final boolean aPutXmlHeader) {
    StringBuffer b= new StringBuffer(128);
    if (aPutXmlHeader) appendHeader(b);
    appendFullElement(b, this, 0);
    return b.toString().trim();
  } // toString

/** Convert this object into an XML string */
  public String toString() {
    return toString(true);
  } // toString

/**
 * Convert the contents of aNode into an xml string and append it to aBuf.
 * @param aBuf StringBuffer The string buffer to append to.
 * @param aNode aNode The XmlTree node to convert to xml.
 * @param aIndent int Indentation size.
 * @history 2006.06.14 - Abdul Habra: fixed a bug where special characters are encoded
 * twice. Fix created by Doug Estep.
 */
  private static void appendFullElement(final StringBuffer aBuf,
                                        final XmlTree aNode,
                                        final int aIndent) {
    if (! aNode.hasChildren()) {
      String txt= StringEscapeUtils.unescapeXml(StringUtility.toString(aNode.pTextBuffer));
      XmlUtil.tagFull(aBuf, aNode.pTag, aNode.pAttributes, txt, aIndent, true);
      return;
    }

    XmlUtil.tagStart(aBuf, aNode.pTag, aNode.pAttributes, aIndent, true);
    if (! StringUtility.isEmpty(aNode.pTextBuffer)) {
      aBuf.append(StringUtility.blanks(aIndent+pINDENT_SIZE));
      aBuf.append(aNode.pTextBuffer).append(StringUtility.NEW_LINE);
    }

    appendChildren(aBuf, aNode, aIndent+pINDENT_SIZE);
    XmlUtil.tagEnd(aBuf, aNode.pTag, aIndent, true);
  }  // elementToBuffer

/**
 * Convert the children of the given node into xml string and append it to aBuf.
 * @param aBuf StringBuffer The string buffer to append to.
 * @param aNode XmlTree The node whose children will be converted to xml string.
 * @param aIndent int Indentation size.
 */
  private static void appendChildren(final StringBuffer aBuf, final XmlTree aNode, final int aIndent) {
    if (! aNode.hasChildren()) return;
    for (int i=0, n= aNode.pChildren.size(); i<n; i++) {
      appendFullElement(aBuf, aNode.getChild(i), aIndent);
    }
  }  // appendChildren

/** Add the given aChild to the children of this object */
  public void addChild(final XmlTree aChild) {
    if (pChildren==null) pChildren= new ArrayList();
    pChildren.add(aChild);
  }

  public void addChild(final String aTag, final String aText, final ListOfString aAttributes) {
    XmlTree c= create(aTag, aAttributes, this);
    c.setText(aText);
    addChild(c);
  }  // void

  public void addChild(final String aTag, final String aText) {
    addChild(aTag, aText, null);
  }  // void


/** Get the number of children that this object has. */
  public int childrenCount() {
    if (pChildren==null) return 0;
    return pChildren.size();
  }

/** Check if this object has any children */
  public boolean hasChildren() {
    return childrenCount()>0;
  }

/**
 * Get the child at the given index, return null if no children
 * @param aIndex int index of element to return.
 * @return XmlTree the child at the specified position in this object's children list
 * @throws IndexOutOfBoundsException - if the index is out of range.
 */
  public XmlTree getChild(final int aIndex) {
    if (! hasChildren()) return null;
    return (XmlTree) pChildren.get(aIndex);
  }

/** Get the first direct child with the given tag, null if not found */
  private XmlTree getDirectChild(final String aChildTag) {
    int i= indexOfChild(aChildTag);
    if (i==-1) return null;
    return getChild(i);
  }

/**
 * get the direct parent of the given child tag, the tag may specify a path.
 * @param aChildTag String The tag of the child. May specify slash separated tags.
 * @return XmlTree return null if child does not exist
 */
  private XmlTree getParent(final String aChildTag) {
    XmlTree child= getChild(aChildTag);
    if (child==null) return null;
    return child.getParent();
  }  // getParent

/** Check if a tag contains the slash char */
  private static boolean isMultiTag(final String aTag) {
    return StringUtility.contains(aTag, pSEPARATOR_CHAR);
  }

/** Check if a tag does not contain the slash char */
  private static boolean isSingleTag(final String aTag) {
    return !isMultiTag(aTag);
  }

/**
 * Get the first child or descendant with the given tag, null if not found.
 * @param aChildTag String The tag name, may specify a path of tags separated by
 *        the forward slash /. <b>Note</b> that you should not include the tag name of the root
 *        (or the XmlTree you start from) in the path, because the path is relevant to
 *        the object you are working with.
 * @return XmlTree null if not found.
 */
  public XmlTree getChild(final String aChildTag) {
    if (! hasChildren()) return null;
    if (isSingleTag(aChildTag))   return getDirectChild(aChildTag);

    String[] path= StringUtility.split(aChildTag, pSEPARATOR_CHAR);
    XmlTree cur= this;
    for (int i=0, n=path.length; i<n; i++) {
      cur= cur.getDirectChild(path[i]);
      if (cur==null) return null;
    }
    return cur;
  }

/**
 * Get all the direct children with the given tag
 * @param aChildTag String
 * @return null if no children found
 */
  private List getDirectChildren(final String aChildTag, final boolean aIsRegExp) {
    int n= childrenCount();
    if (n==0) return null;

    List r= new ArrayList();
    for (int i=0; i<n; i++) {
      XmlTree child= getChild(i);
      if (child.isEqualTag(aChildTag, aIsRegExp)) r.add(child);
    }
    if (r.isEmpty()) return null;
    return r;
  }  // getDirectChildren

/**
 * Get a list of all children that have the given tag. The tag may specify a path.
 * The children are for the last tag specified in aChildTag path.
 * @param aChildTag String The tag name, may specify a path of tags separated by
 *        the forward slash /
 * @return List null if not found.
 */
  public List getChildren(final String aChildTag) {
    if (! hasChildren()) return null;
    if (isSingleTag(aChildTag))   return getDirectChildren(aChildTag, false);

    XmlTree prnt= getParent(aChildTag);
    if (prnt==null) return null;

    return prnt.getDirectChildren(extractLastTag(aChildTag), false);
  }  // getChildren

/** Get the last tag in a tag path. e.g. T1/T2/T3 => T3  */
  private static String extractLastTag(final String aTagPath) {
    if (StringUtility.isBlank(aTagPath)) return StringUtility.EMPTY;
    if (isSingleTag(aTagPath)) return aTagPath;
    return StringUtility.substringAfterLast(aTagPath, pSEPARATOR_STRING);
  }  // extractLastTag

/**
 * Find ALL children who are at the given tag path.
 * @param aTagPath A forward slash / separated path for tags
 * @param aIsRegExp When true, the items in the given aTagPath will be treated
 * as regular expressions to be matched against tag names.
 * @return List of found children. Null if not found.
 */  
  public List findAllChildren(String aTagPath, final boolean aIsRegExp) {
    if (aTagPath==null) return null;
    aTagPath= StringUtility.trimToEmpty(aTagPath);
    if (aTagPath.length()==0) return null;
    if (! hasChildren()) return null;

    String[] path= StringUtility.splitPreserveAllTokens(aTagPath, pSEPARATOR_CHAR);
    List result= getDirectChildren(path[0], aIsRegExp);
    
    for (int i=1, n= path.length; i<n; i++) {
      String tag= path[i];
      List temp= new ArrayList();
      for (int j=0, m=CollectionUtility.size(result); j<m; j++) {
        XmlTree child= (XmlTree) result.get(j);
        List children= child.getDirectChildren(tag, aIsRegExp);
        if (CollectionUtility.size(children)>0 ) {
          temp.addAll( children );
        }
      }
      result= temp;
    }
    return CollectionUtility.isNullOrEmpty(result) ? null : result;
  } // findAllChildren
  
/**
 * Find the index of the child with the given tag starting at the child with the
 * given index. The child must be a direct child of this object and not just a descendant,
 * in other words, aChildTag cannot contain the forward slash /.
 * @param aChildTag String The tag of the child to find.
 * @param aStartIndex int Start index of the first child to search
 * @return int -1 if not found
 */
  public int indexOfChild(final String aChildTag, final int aStartIndex) {
    for (int i=aStartIndex, n=childrenCount(); i<n; i++) {
      if (getChild(i).isEqualTag(aChildTag)) return i;
    }
    return -1;
  }  // indexOfChild

/**
 * Find the index of the child with the given tag. The child must be a direct child of
 * this object and not just a descendant, in other words, aChildTag cannot contain
 * the forward slash /.
 * @param aChildTag String The tag of the child to find.
 * @return int -1 if not found
 */
  public int indexOfChild(final String aChildTag) {
    return indexOfChild(aChildTag, 0);
  }

/** Get the value of the given attribute, return "" if no exist */
  public String getAttribute(final String aChildTag, final String aName) {
    if (StringUtility.isBlank(aChildTag) || StringUtility.isBlank(aName)) return null;
    XmlTree child= getChild(aChildTag);
    if (child==null) return null;
    return child.getAttribute(aName);
  } // getAttribute


/** Get the value of the given attribute, return "" if no exist */
  public String getAttribute(final String aName) {
    if (pAttributes==null || pAttributes.size()==0) return StringUtility.EMPTY;
    return pAttributes.getValueOfName(aName);
  }

/** Set the value of the given attribute */
  public void setAttribute(final String aName, final String aValue) {
    if (pAttributes==null)  pAttributes= new ListOfString();
    pAttributes.setValueOfName(aName, aValue);
  }

/** Delete the given attribute */
  public void deleteAttribute(final String aName) {
    if (pAttributes==null || pAttributes.size()==0) return;
    int i= pAttributes.indexOfName(aName);
    if (i<0) return;
    pAttributes.remove(i);
  } // deleteAttribute

/** Delete the child at the given index */
  public void deleteChild(final int aIndex) {
    int n= childrenCount();
    if (n==0 || aIndex>=n) return;
    pChildren.remove(aIndex);
  }  // deleteChild

/**
 * Delete all children with the given tag name.
 * @param aChildTag String The tag name, may specify a path of tags separated by
 *        the forward slash /
 */
  public void deleteChild(final String aChildTag) {
    if (StringUtility.isBlank(aChildTag)) return;

    XmlTree parent= getParent(aChildTag);
    if (parent==null) return;

    String tag= extractLastTag(aChildTag);
    if (tag.length()==0) return;

    int n= parent.childrenCount()-1;
    String t;
    for (int i=n; i>=0; i--) {
      t= parent.getChild(i).getTag();
      if (tag.equals(t))  parent.deleteChild(i);
    }
  }  // deleteChild

/** Get the root of this XmlTree object */
  public XmlTree getRoot() {
    XmlTree p= getParent();
    if (p==null) return this;
    return p.getRoot();
  }  // getRoot

/** Get the original xml that was assigned to the root node */
  public String getOriginalXml() {
    return getRoot().pOriginalXml;
  }  // getOriginalXml

/**
 * Create a new copy of this XmlTree object. This is a recursive method.
 * @param aParent XmlTree the parent of the object to copy.
 * @return XmlTree
 */
  private XmlTree copy(final XmlTree aParent) {
    XmlTree r= (XmlTree) clone();
    r.pParent= aParent;
    return r;
  }  // copy

/**
 * Create a copy of this XmlTree object, the parent of the returned object WILL be null
 * even if this object has a non-null parent.
 * @return XmlTree A deep-copy of this object.
 */
  public XmlTree copy() {
    return copy(null);
  }

/**
 * Create a copy of this XmlTree object, the parent of the returned object WILL be null
 * even if this object has a non-null parent.
 * @return Object A deep-copy of this object.
 */
  public Object clone() {
    try {
      XmlTree r= (XmlTree) super.clone();
      if (pTextBuffer!= null) {
        r.pTextBuffer= new StringBuffer(pTextBuffer.toString());
      }
      r.pAttributes= pAttributes.copy();
      r.pParent= null;

      for (int i=0, n=childrenCount(); i<n; i++) {
        XmlTree ch= (XmlTree) getChild(i).clone();
        ch.pParent= r;
        r.addChild(ch);
      }
      
      return r;
    } catch (CloneNotSupportedException e) {
      // this should not happen because this class implements Cloneable
      throw new IllegalStateException(e.toString());
    }
  }

  public void setVersion(final String aVersion) {
    pVersion= StringUtility.defaultString(aVersion);
  }

/** Default is 1.0 */
  public String getVersion() {
    return pVersion;
  }

  public void setEncoding(final String aEncoding) {
    pEncoding= StringUtility.defaultString(aEncoding);
  }

/** default is "" */
  public String getEncoding() {
    return pEncoding;
  }

/**
 * Check if this node's tag is equal to the given tag optionally matching a regular
 * expression.
 * @param aTag The tag value to compare against.
 * @param aIsRegExp If true, then aTag will be treated as a RegEx and this node's tag
 *        will be matched against aTag.
 * @return true if this node's tag equals/matches the given tag.
 */  
  public boolean isEqualTag(final String aTag, final boolean aIsRegExp) {
    if (aIsRegExp) {
      String tag= StringUtility.defaultString(getTag());
      return tag.matches(aTag);
    } 
    return StringUtility.equals(getTag(), aTag);
  }
  
/** Check if this node's tag is equal to the given tag */
  public boolean isEqualTag(final String aTag) {
    return isEqualTag(aTag, false);
  }

/** Check if this node's tag is equal to the given node's tag */
  public boolean isEqualTag(final XmlTree aNode) {
    String t= aNode==null? null : aNode.getTag();
    return isEqualTag(t);
  }

/**
 * Extract this node or any of its children if has a tag matching given tag and add to
 * given list.
 * @param aChildTag String tag to match
 * @param aBuffer List of XmlTree objects to add into.
 */
  private void findDescendantsRecursive(final String aChildTag,
                                        final List aBuffer) {
    if (isEqualTag(aChildTag)) aBuffer.add(this);

    for (int i=0, n= childrenCount(); i<n; i++) {
      XmlTree child= getChild(i);
      child.findDescendantsRecursive(aChildTag, aBuffer);
    }
  } // findDescendantsRecursive

/**
 * Search current node to get a list of nodes whose tags equal the given tag.
 * <br>author Doug Estep, Abdul Habra.
 * @param aChildTag String tag to find, this must be a simple tag, ie. no /
 * @param aIsRecursive boolean if true, will search all of the children. When this is
 *        true, a node and its child can be returned as two elements in the list if
 *        both have the same tag.
 * @return List of XmlTree objects whose tags is equal to the given aChildTag.
 */
  public List getDescendants(final String aChildTag,
                             final boolean aIsRecursive) {
    List r= new ArrayList();
    for (int i=0, n= childrenCount(); i<n; i++) {
      XmlTree child= getChild(i);
      if (aIsRecursive) {
        child.findDescendantsRecursive(aChildTag, r);
      } else {
        if (child.isEqualTag(aChildTag)) r.add(child);
      }
    }
    return r;
  }  // getDescendants

/**
 * For the given list of XmlTree node objects, get their text.
 * @param aNodeList List of XmlTree objects.
 * @return ListOfString of the text of each node.
 */
  public static ListOfString extractText(final List aNodeList) {
    if (aNodeList==null) return null;

    ListOfString r= new ListOfString(aNodeList.size());
    for (Iterator it= aNodeList.iterator(); it.hasNext(); ) {
      XmlTree node= (XmlTree) it.next();
      r.add( node.getText() );
    }
    return r;
  }  // extractText

/** Search the children of the given node for first child with given tag */
  private static XmlTree searchChildren(final String aTag, final XmlTree aNode) {
    for (int i=0, n= aNode.childrenCount(); i<n; i++) {
      XmlTree child= aNode.getChild(i);
      XmlTree found= find(aTag, child);
      if (found != null) return found;
    }
    return null;
  }

  private static XmlTree find(final String aTag, final XmlTree aNode) {
    if (aNode.isEqualTag(aTag)) return aNode;
    return searchChildren(aTag, aNode);
  }

/**
 * Find the first child or descendants with the given tag.
 * <br>author Doug Estep, Abdul Habra.
 * @param aTag String Tag to find
 * @return XmlTree null if not found
 */
  public XmlTree find(final String aTag) {
    return searchChildren(aTag, this);
  }

/** For XmlFormatter command line application */
  private static void commandLineFormatter(String[] args) throws Exception {
    if (args.length==0) {
      System.out.println("Please specify an xml file name to reformat");
      return;
    }

    String fileName;
    fileName= args[0];
    if (!FileIO.exists(fileName)) {
      System.out.println("File " +  fileName + " Does not exist");
      return;
    }

    XmlTree root= XmlTree.createFromFile(fileName, true, new SimpleConsoleLogger());
    if (root==null) return;
    System.out.println(root);
  }

/** Expects the name of an xml file to reformat */
  public static void main(String[] args) throws Exception {
    commandLineFormatter(args);
  }  // main

}  // XmlTree
