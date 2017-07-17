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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tek271.util.collections.CollectionUtility;
import com.tek271.util.internet.html.HtmlUtil;
import com.tek271.util.string.StringUtility;

/**
 * Generate a cross browser multi-level html menu.
 * It expects that the style sheet <code>multiLevelTopMenu.css</code> is 
 * available to the browser.
 * <p>The html and css idea of this class was taken and refactored from:
 * www.cssplay.co.uk/menus/simple_vertical.html
 * </p>
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class MultiLevelMenu {
  
  public static class Config {
    public String idMultiLevelTopMenu= "multiLevelTopMenu";
    public String indent= "  ";
    public String noWhereHref= "#noWhere";
    public String classArrowDown= "arrowDown";
    public String classArrowRight= "arrowRight";
    public String classBorderLine= "borderLine";
    public String ie6CheckStart= "<!--[if lte IE 6]><table><tr><td><![endif]-->\n";
    public String ie6CheckEnd= "<!--[if lte IE 6]></td></tr></table></a><![endif]-->\n";
    public String ie7Check= "<!--[if IE 7]><!--></a><!--<![endif]-->";
  }

  private Config config;
  
  private MultiLevelMenu parent;
  public String text;
  public String href;
  public String target;
  
  private List children; 
  
/** Create a menu object */  
  public MultiLevelMenu(MultiLevelMenu parent, String text, String href, String target) {
    this.parent= parent;
    this.text= text;
    this.href= href;
    this.target= target;
  }
  
  public MultiLevelMenu(MultiLevelMenu parent, String text, String href) {
    this(parent, text, href, null);
  }
  
  public MultiLevelMenu(String text, String href, String target) {
    this(null, text, href, target);
  }
  
  public MultiLevelMenu(String text, String href) {
    this(null, text, href, null);
  }
  
  public MultiLevelMenu() {
    this(null, null, null, null);
  }
  
  public MultiLevelMenu getParent() {
    return parent;
  }
  
/** Get the root node of this menu */  
  public MultiLevelMenu getRoot() {
    MultiLevelMenu r= this;
    while (r.parent !=null) {
      r= r.parent;
    }
    return r;
  }

/**
 * Get the child at the given index.
 * @param index
 * @return return null of no children or if an invalid index.
 */
  public MultiLevelMenu getChild(int index) {
    if (!hasChildren()) return null;
    if (index<0 || index>=chilrenCount()) return null;
    return (MultiLevelMenu) children.get(index);
  }
  
/** Get a list of this menu's children */
  public List getChildren() {
    return children;
  }

/** Remove this menu's children */  
  public void removeChildren() {
    children= null;
  }
  
/** Check if this menu has any children */
  public boolean hasChildren() {
    return ! CollectionUtility.isNullOrEmpty(children);
  }
  
/** Get the number of this menu's children */
  public int chilrenCount() {
   return CollectionUtility.size(children); 
  }
  
/** Add a child menu this this menu */
  public MultiLevelMenu addChild(MultiLevelMenu multiLevelMenu) {
    if (children==null) children= new ArrayList();
    children.add(multiLevelMenu);
    multiLevelMenu.parent= this;
    return multiLevelMenu;
  }

/**
 * Add a child menu this this menu
 * @param text Text of the child
 * @param href href of the child
 * @param target teaget of the child
 * @return the added child
 */
  public MultiLevelMenu addChild(String text, String href, String target) {
    MultiLevelMenu child= new MultiLevelMenu(this, text, href, target);
    addChild(child);
    return child;
  }

/**
 * Add a child menu this this menu
 * @param text Text of the child
 * @param href href of the child
 * @return the added child
 */
  public MultiLevelMenu addChild(String text, String href) {
    MultiLevelMenu child= new MultiLevelMenu(this, text, href);
    addChild(child);
    return child;
  }

/** 
 * The depth of this menu in its tree, iow, how many ancestors does it have.
 * The depth of root is zero. 
 */
  public int depth() {
    int depth=0;
    MultiLevelMenu p= parent;
    while (p != null) {
      p= p.parent;
      depth++;
    }
    return depth;
  }
  
/**
 * Index of this menu among its parent's children
 */
  public int indexAmongSiblings() {
    if (parent==null) return 0;
    
    for (int i=0, n=parent.chilrenCount(); i<n; i++) {
      if (this == parent.children.get(i)) return i;
    }
    throw new IllegalStateException("Parent of a MenuItem cannot locate its child");
  }
  
/** Check if this is the first child of its parent */
  public boolean isFirstChild() {
    return indexAmongSiblings()==0;
  }
  
/** Check if this is the last child of its parent */
  public boolean isLastChild() {
    if (parent==null) return true;
    
    List siblings= parent.children;
    int lastIndex= siblings.size()-1;
    return this==siblings.get(lastIndex);
  }
  
/** get the indentation for a line */
  private String prefix(MultiLevelMenu multiLevelMenu) {
    if (multiLevelMenu==null) return StringUtility.EMPTY;
    
    int depth= multiLevelMenu.depth();
    if (depth>1) depth++;
    return StringUtils.repeat(config.indent, depth);
  }
  
/** put the given number of indentations into the stringBuffer */
  private StringBuffer indent(StringBuffer sb, int count) {
    for (int i=0; i<count; i++) {
      sb.append(config.indent);
    }
    return sb;
  }
  
/** Append the html of a child */
  private StringBuffer appendChild(StringBuffer sb, MultiLevelMenu child, int depth, String prefix) {
    if (depth>1)   indent(sb, 1);
    sb.append(prefix);
    child.appendHtml(sb, config);
    sb.append('\n');
    return sb;
  }
  
/** Append the html of this menu's children */
  private StringBuffer appendChildren(StringBuffer sb, String id) {
    if (children==null) return sb;
    int size= children.size();
    if (size==0) return sb;

    MultiLevelMenu firstChild= (MultiLevelMenu) children.get(0);
    int depth= firstChild.depth();
    String prefix= prefix(firstChild);
    sb.append(prefix).append("<ul");
    HtmlUtil.appendAttribute(sb, "id", id);
    sb.append(">\n");
    for (int i=0; i<size; i++) {
      appendChild(sb, (MultiLevelMenu) children.get(i), depth, prefix);
    }
    
    sb.append(prefix).append("</ul>\n");
    return sb;
  }
  
/** Append the html of this menu's children, with browser specific code */
  private StringBuffer appendChildrenWithBrowserCode(StringBuffer sb) {
    if (!hasChildren()) return sb;

    String prefix= prefix(this);
    sb.append('\n');
    indent(sb, 2);
    sb.append(prefix).append(config.ie6CheckStart);
    appendChildren(sb, null);
    indent(sb, 2);
    sb.append(prefix).append(config.ie6CheckEnd);
    sb.append(prefix);
    indent(sb, 1);
    return sb;
  }
  
/** Append the href and target attributes */
  private void appendLinkAttributes(StringBuffer sb) {
    if (StringUtility.isBlank(href)) {
      HtmlUtil.appendAttribute(sb, "href", config.noWhereHref );
    } else {
      HtmlUtil.appendAttribute(sb, "href", href);
    }
    HtmlUtil.appendAttribute(sb, "target", target);
  }
  
/** Append the html for menu items at the first level, ie, their depth is 1 */
  private StringBuffer appendHtmlFirstLevel(StringBuffer sb) {
    String prefix= prefix(this);
    sb.append(prefix);
    if (hasChildren()) {
      sb.append("<li class='" + config.classArrowDown + "'>");
    } else {
      sb.append("<li>");
    }
    sb.append("<a");
    if (isLastChild()) {
      sb.append(" class='" + config.classBorderLine + "'");
    }
    appendLinkAttributes(sb);
    sb.append(">");
    if (StringUtility.isNotBlank(text)) {
      sb.append(text);
    } else {
      sb.append(HtmlUtil.SPACE);
    }
    if (hasChildren()) {
      sb.append(config.ie7Check);
    } else {
      sb.append("</a>");
    }
    
    appendChildrenWithBrowserCode(sb);
    
    sb.append("</li>");
    return sb; 
  }

/** Append html for menu items whose depth is greater than 1 */
  private StringBuffer appendHtmlAfterFirstLevel(StringBuffer sb) {
    if (hasChildren()) {
      sb.append("<li class='").append(config.classArrowRight).append("'>");
    } else {
      sb.append("<li>");
    }
    sb.append("<a ");
    appendLinkAttributes(sb);
    if (isFirstChild()) {
      sb.append("class='").append(config.classBorderLine).append("'");
    }
    sb.append(">");
    sb.append(text);
    if (hasChildren()) {
      sb.append(config.ie7Check);
    } else {
      sb.append("</a>");
    }
    
    appendChildrenWithBrowserCode(sb);
    
    sb.append("</li>");
    return sb;
  }
  
/** 
 * Append the html of this menu to the given StringBuffer.
 * @param sb StringBuffer to append to
 * @param config A config object allows you to change some defaults.
 * @return The StringBuffer which was appended to.
 */  
  public StringBuffer appendHtml(StringBuffer sb, Config config) {
    if (config==null) config= new Config();
    this.config= config;
    int depth= depth();
    if (depth==0) {
      return appendChildren(sb, config.idMultiLevelTopMenu);
    }
    
    if (depth==1) {
      return appendHtmlFirstLevel(sb);
    }
    
    // depth > 1
    appendHtmlAfterFirstLevel(sb);
    return sb;
  }
  
/** 
 * Append the html of this menu to the given StringBuffer.
 * @param sb StringBuffer to append to
 * @return The StringBuffer which was appended to.
 */  
  public StringBuffer appendHtml(StringBuffer sb) {
    return appendHtml(sb, null);
  }
  
/**
 * Get the html String of this menu
 * @param config A config object allows you to change some defaults.
 * @return the html of the menu
 */  
  public String toString(Config config) {
    StringBuffer sb= new StringBuffer(256);
    appendHtml(sb, config);
    return sb.toString();
  }
  
/**
 * Get the html String of this menu.
 * @return the html of the menu
 */  
  public String toString() {
    return toString(null);
  }
  
}
