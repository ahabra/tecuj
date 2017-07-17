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

package com.tek271.util.io;

import java.io.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.io.IOUtils;

import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;
import com.tek271.util.log.*;

/**
 * Encapsulates a configuration file interface. This is a better handler
 *  for properties files that can support inline comments and grouping.
 * <p>
 * The user can (optionally) specify in the file what is the equality character and the comment
 * start character. For example: <pre>
 * _equal=>]
 * _comment=%</pre>
 * If the above two lines are the first two lines in the file, values will be separated
 * from their names by the > or ] char, while comments will start with %.
 * <p>The default Equal chars are = or : while the default Comment chars are ; or #.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ConfigFile implements Cloneable {
  private final static String pCLASS_NAME= ConfigFile.class.getName();

/** If 1'st line starts with _equal= it will define the equal chars. */
  private static final String pUSER_DEF_EQUAL= "_equal";

/** If 2'nd line starts with _commant= it will define the comment chars. */
  private static final String pUSER_DEF_COMMENT= "_comment";

  private static final String pINLINE_COMMENT_PAD= "    ";  // four spaces
  private static final String pBLANK= StringUtility.BLANK;
  private static final String pEMPTY= StringUtility.EMPTY;
  private static final String TAG_START= "${";
  private static final String TAG_END= "}";


/** The chars that may be used as an equal operator, any can be used. */
  private String pEqualChars= "=:";

/** The chars that start a comment, any char will start a comment */
  private String pCommentChars= ";#";

  private ListOfString pData= new ListOfString();
  private String pFileName;
  private String pEqual= "=";    // caches the equal char
  private String pComment= ";";  // caches the comment char
  private String includeFilePropertyName= "includeFile"; // properties named propertyFile will include the file
  private boolean isProcessTags;
  private boolean isProcessIncludeFile;

/** Create an empty config file */
  public ConfigFile() {}

/** Create a ConfigFile from a ListOfString */
  public ConfigFile(final ListOfString aData) {
    pData= aData.copy();
  }

/** Create a ConfigFile from the given file name */
  public ConfigFile(final String aFileName) throws IOException {
    read(aFileName);
  }  // ConfigFile

/** Create a ConfigFile from the given reader */
  public ConfigFile(final BufferedReader aReader) throws IOException {
    read(aReader);
  }  // ConfigFile

/** Create a ConfigFile from the given stream */
  public ConfigFile(final InputStream aStream) throws IOException {
    read(aStream);
  }  // ConfigFile

  private void init() {
    if (pData==null) return;
    if (pData.size()==0) return;
    setUserDefinedChars(0);
    if (pData.size()==1) return;
    setUserDefinedChars(1);
  } // init

  private void setUserDefinedChars(final int aIndex) {
    String n= pData.getNameAtIndex(aIndex);
    String v= pData.getValueAtIndex(aIndex);
    if (n.equalsIgnoreCase(pUSER_DEF_EQUAL)) setEqualChars(v);
    else
    if (n.equalsIgnoreCase(pUSER_DEF_COMMENT)) setCommentChars(v);
  }  // setUserDefinedChars

/** Read a configuration file into this object */
  public void read(final String aFileName) throws IOException {
    pFileName= aFileName;
    pData.readFromTextFile(pFileName);
    init();
  }

/** Read from a Reader into this object */
  public void read(final BufferedReader aReader) throws IOException {
    pData.readFromBufferedReader(aReader);
    init();
  }

/** Read from a Reader into this object */
  public void read(final InputStream aStream) throws IOException {
    pData.readFromStream(aStream);
    init();
  }

/** Write the contents of this object into a configuration file */
  public void write() throws IOException {
    pData.writeToTextFile(pFileName);
  }

/** Write the contents of this object into the given configuration file */
  public void write(final String aFileName) throws IOException {
    pFileName= aFileName;
    write();
  }

/** Get the set of chars where any of them can be used as an equal operator */
  public String getEqualChars() {
    return pEqualChars;
  }

/** Set the set of chars where any of them can be used as an equal operator */
  public void setEqualChars(final String aEqualChars) {
    if (StringUtility.isEmpty(aEqualChars)) return;
    pEqualChars= aEqualChars;
    pEqual= String.valueOf(pEqualChars.charAt(0));
    pData.valueEquator= pEqual;
  }

/** Get the set of chars where any of them can be used to start a comment */
  public String getCommentChars() {
    return pCommentChars;
  }

/** Set the set of chars where any of them can be used to start a comment */
  public void setCommentChars(final String aCommentChars) {
    if (StringUtility.isEmpty(aCommentChars)) return;
    pCommentChars= aCommentChars;
    pComment= String.valueOf(pCommentChars.charAt(0));
    pData.commentStartSingleLine= pComment;
  }

/** Add a configuration name/value pair */
  public void add(final String aName, final String aValue) {
    pData.add(aName, aValue);
  }  // add

/** Add a configuration name/value pair with a comment */
  public void add(final String aName, final String aValue, final String aComment) {
    pData.add(aName + pEqual + aValue + pINLINE_COMMENT_PAD + pComment + pBLANK + aComment);
  }  // add

/** Add the contents of another ConfigFile into this object */
  public void add(final ConfigFile aConfigFile){
    pData.addAll(aConfigFile.pData);
  }

/** Add a comment line */
  public void addComment(final String aComment) {
    pData.add(pComment + pBLANK + aComment);
  }  // addComment

/** Check if the line at the given index is a comment */
  public boolean isComment(final int aIndex) {
    String line= pData.getItem(aIndex).trim();
    return isComment(line, pCommentChars);
  } // isComment

  /** Check if the line is a comment */
  private static boolean isComment(final String line, String commentChars) {
    if (line.length()==0) return false;
    char ch= line.charAt(0);
    return commentChars.indexOf(ch) >= 0;
  } // isComment
  
/** Check if the line at the given index is a comment or empty */
  public boolean isCommentOrEmpty(final int aIndex) {
    String line= pData.getItem(aIndex).trim();
    if (line.length()==0) return true;
    char ch= line.charAt(0);
    return pCommentChars.indexOf(ch) >= 0;
  } // isComment

/** # of lines in the configuration file */
  public int size() {
    return pData.size();
  }  // size

/** Get the name of the configuration item at the given line */
  public String getName(final int aIndex) {
    if (isComment(aIndex)) return pEMPTY;
    return pData.getNameAtIndex(aIndex);
  } // getName

/** Get the value of the configuration item at the given line */
  public String getValue(final int aIndex) {
    if (isComment(aIndex)) return pEMPTY;
    String v= pData.getValueAtIndex(aIndex).trim();
    int i= StringUtility.indexOfAny(v, pCommentChars);
    if (i<0) return v;
    return StringUtility.left(v, i).trim();
  }  // getValue

/** Get the value of the configuration item name */
  public String getValue(final String aName) {
    int i= pData.indexOfName(aName);
    if (i<0) return pEMPTY;
    return getValue(i);
  }  // getValue

  /** Get the value of the configuration item at the given line, if not exist return null */
  public String getValueIfExist(final int aIndex) {
    if (isComment(aIndex)) return null;
    String v= pData.getValueAtIndex(aIndex).trim();
    int i= StringUtility.indexOfAny(v, pCommentChars);
    if (i<0) return v;
    return StringUtility.left(v, i).trim();
  }  // getValueIfExist
  
  /** Get the value of the configuration item name, if not exist return null */
  public String getValueIfExist(final String aName) {
    int i= pData.indexOfName(aName);
    if (i<0) return null;
    return getValueIfExist(i);
  }  // getValueIfExist
  
/**
 * Get the value of the configuration item at the given line as a boolean
 * @param aIndex int index of line
 * @param aDefault boolean Value if line does not exist.
 * @return boolean true if value is any of yYtT1. return false otherwise.
 */
  public boolean getValueAsBoolean(final int aIndex, final boolean aDefault) {
    String v= getValue(aIndex);
    if (v.length()==0) return aDefault;
    char c= v.charAt(0);
    return c=='y' || c=='Y' || c=='t' || c=='T' || c=='1';
  }

/**
 * Get the value of the configuration item name as a boolean
 * @param aName String name of item.
 * @param aDefault boolean Value if line does not exist.
 * @return boolean true if value is any of yYtT1. return false otherwise.
 */
  public boolean getValueAsBoolean(final String aName, final boolean aDefault) {
    int i= pData.indexOfName(aName);
    if (i<0) return aDefault;
    return getValueAsBoolean(i, aDefault);
  }

/**
 * Get the value of the configuration item at the given line as an int
 * @param aIndex int index of line
 * @param aDefault int Default value if line does not exist.
 * @return boolean the value as an int.
 */
  public int getValueAsInt(final int aIndex, final int aDefault) {
    String v= getValue(aIndex);
    if (v.length()==0) return aDefault;
    return NumberUtils.toInt(v, aDefault);
  }

/**
 * Get the value of the configuration item at the given line as an int
 * @param aName String name of item.
 * @param aDefault int Default value if line does not exist.
 * @return boolean the value as an int.
 */
  public int getValueAsInt(final String aName, final int aDefault) {
    int i= pData.indexOfName(aName);
    if (i<0) return aDefault;
    return getValueAsInt(i, aDefault);
  }

/** Get comment at given index */
  public String getComment(final int aIndex) {
    if (isComment(aIndex)) return pData.getItem(aIndex).trim();

    String v= pData.getValueAtIndex(aIndex).trim();
    int i= StringUtility.indexOfAny(v, pCommentChars);
    if (i<0) return pEMPTY;
    return StringUtility.substring(v, i+1).trim();
  }  // getComment

/** Get comment at the line with the given name */
  public String getComment(final String aName) {
    int i= pData.indexOfName(aName);
    if (i<0) return pEMPTY;
    return getComment(i);
  }  // getComment

/** check if the given name exists */
  public boolean isNameExist(final String aName) {
    return indexOf(aName)>=0;
  }

/** Get index of the given name. -1 if not found  */
  public int indexOf(final String aName) {
    return pData.indexOfName(aName);
  }  // indexOf

/** Get index of the given name  starting at the given index. -1 if not found */
  public int indexOf(final String aName, final int aStartIndex) {
    return pData.indexOfName(aName, aStartIndex);
  }  // indexOf

/** Get a list of all names */
  public ListOfString getAllNames() {
    return getAllNames(null);
  }  // getAllNames

/** Get a list of all names that start with the given prefix */
  public ListOfString getAllNames(final String aNamePrefix) {
    return getAllNames(aNamePrefix, false);
  }  // getAllNames

/** Get a list of unique names that start with the given prefix */
  public ListOfString getAllNames(final String aNamePrefix,
                                  final boolean aIsUnique) {
    ListOfString r= new ListOfString();
    String name;
    boolean isEmptyPrefix= StringUtility.isEmpty(aNamePrefix);
    for (int i=0, n=size(); i<n; i++) {
      if (isCommentOrEmpty(i)) continue;
      name= getName(i);
      if (name.length()==0) continue;
      if (isEmptyPrefix || name.startsWith(aNamePrefix)) {
        if (aIsUnique) r.addUnique(name);
        else r.add(name);
      }
    }
    return r;
  }  // getAllNames

/** Get a new ConfigFile object with names that start with the given prefix */
  public ConfigFile getSubset(final String aPrefix) {
    ConfigFile cf= new ConfigFile();
    cf.pData= pData.subsetItemStart(aPrefix);
    cf.setCommentChars(pCommentChars);
    cf.setEqualChars(pEqualChars);
    return cf;
  }  // getSubset

/** Find the index of the first item whose name starts with the given prefix */
  public int indexOfPrefix(final String aNamePrefix) {
    return indexOfPrefix(aNamePrefix, 0);
  }  // indexOfPrefix

/** Find the index of the item whose name starts with the given prefix, starting at aStartIndex */
  public int indexOfPrefix(final String aNamePrefix, final int aStartIndex) {
    String name;
    for (int i=aStartIndex, n=size(); i<n; i++) {
      name= getName(i);
      if (name.startsWith(aNamePrefix)) return i;
    }
    return -1;
  }  // indexOfPrefix

/** Remove all comments and empty lines from this object */
  public void removeComments() {
    ListOfString data= new ListOfString();
    data.valueEquator= pEqual;
    for (int i=0, n=size(); i<n; i++) {
      if (isCommentOrEmpty(i)) continue;
      data.add(getName(i), getValue(i));
    }
    pData= data;
  }  // removeComments

/** Remove the item at the given index */
  public void remove(final int aIndex) {
    pData.remove(aIndex);
  }

/** remove item(s) with the given name, starting at the given index */
  public void remove(final String aName, final int aStartIndex, final boolean aIsRemoveAll) {
    int si=indexOf(aName, aStartIndex);
    if (si<0)return;
    if (!aIsRemoveAll) {
      remove(si);
      return;
    }
    for(int i=si, n=size(); i<n; i++) {
      if (aName.equals(getName(i))) remove(i);
    }
  }

/** remove item(s) with the given name */
  public void remove(final String aName, final boolean aIsRemoveAll) {
    remove(aName, 0, aIsRemoveAll);
  }

/** remove item(s) with the given name */
  public void remove(final String aName) {
    remove(aName, 0, true);
  }

/** Get a copy of this object's data as a list of string */
  public ListOfString getData() {
    return pData.copy();
  }

/** Make a copy of this object */
  public ConfigFile copy() {
    return (ConfigFile) clone();
  }

/** Clone this object */
  public Object clone() {
    try {
      ConfigFile r= (ConfigFile) super.clone();
      r.pData= pData.copy();
      return r;
    } catch (CloneNotSupportedException e) {
      // this should not happen because this class implements Cloneable
      throw new IllegalStateException(e.toString());
    }
  }

/** Convert this object into a string */
  public String toString() {
    return pData.toString();
  }  // toString

/**
 * Create a ConfigFile object from the given file name.
 * @param aFileName String the properties file name.
 *        The path should be relative to the current context class loader, not
 *        an absolute path. In a servlet, this is the WEB-INF/classes directory.
 * @param aLogger ILogger Used for error logging
 * @return ConfigFile null if error.
 */
  public static ConfigFile create(final String aFileName, final ILogger aLogger) {
    String method= "create";
    ClassLogger clog= new ClassLogger(aLogger, pCLASS_NAME);

    InputStream is= FileIO.readFileToStream(aFileName);
    if (is==null) {
      clog.error(method, "Cannot find " + aFileName);
      return null;
    }

    ConfigFile r= null;
    try {
      r=new ConfigFile(is);
    } catch (IOException e) {
      String msg= "Cannot read " + aFileName;
      clog.error(method, msg, e);
    } finally {
      IOUtils.closeQuietly(is);
    }
    return r;
  }  // create

/**
 * Process tags and included files, tags are replace by their property values, while 
 * includeFile properties are replaced by the content of the included file.
 * @param isProcessTags Tags are defined as <code>${<i>tag</i>}</code>.
 * @param isProcessIncludeFile if true, the value of the includeFile property is treated
 * as a file name, which will be read and included at the current position, the 
 * includeFile line will be removed.<br>
 * You can include multiple files by using includeFile.1, includeFile.2, ... for property
 * names. 
 * @param logger Used when attempting to include a file which does not exist, a warning
 * will be logged, but the operation will continue.
 * @return true if all included files where found, otherwise, return false.
 */
  public boolean process(final boolean isProcessTags, 
                         final boolean isProcessIncludeFile, 
                         final ILogger logger) {
    this.isProcessTags= isProcessTags;
    this.isProcessIncludeFile= isProcessIncludeFile;
    return process(0, logger);
  }
  
  private boolean process(final int startIndex,
                          final ILogger logger) {
    boolean r = true;
    for (int i = startIndex; i < size(); i++) {
      if (isProcessTags) processLineTags(i, this);
      if (isProcessIncludeFile) {
        r = r & processIncludeFile(i, logger, this); // do not short-circuit the AND
      }
    }
    return r;
  }
  
  static boolean processIncludeFile(int index, final ILogger logger, ConfigFile configFile) {
    if (configFile.isComment(index)) return true;
    if (! configFile.isIncludeFile(index)) return true;
    
    String fn= configFile.pData.getValueAtIndex(index);
    fn= configFile.removeComment(fn);
    if (StringUtility.isBlank(fn)) return true;
    
    ListOfString included= readIncludedFile(fn, logger);
    if (included==null) return false;
    if (included.size()==0) return true;
    configFile.pData.remove(index);
    configFile.pData.addAll(index, included);
    configFile.process(index, logger);
    return true;
  }
  
  private static ListOfString readIncludedFile(String fileName, final ILogger logger) {
    ListOfString included= new ListOfString();
    try {
      included.readFromTextFile(fileName);
    } catch (IOException e) {
      String msg= "Could not read included property file: " + fileName + ". " + e.getMessage();
      logger.log(ILogger.WARNING, msg);
      return null;
    }
    return included;
  }
  
  private boolean isIncludeFile(int index) {
    String name= pData.getNameAtIndex(index);
    if (StringUtils.isBlank(name)) return false;
    
    if (StringUtils.equals(includeFilePropertyName, name)) return true;
    if (name.startsWith(includeFilePropertyName + ".")) return true;
    return false;
  }
  
  static void processLineTags(int index, ConfigFile sourceConfig) {
    String line= sourceConfig.pData.getItem(index);
    line= processLineTags(line, sourceConfig);
    sourceConfig.pData.set(index, line);
  }

  private static String processLineTags(String line, ConfigFile sourceConfig) {
    String orgLine= line;
    String commentChars= sourceConfig.pCommentChars;
    if (isComment(line, commentChars)) return orgLine;
    line = removeComment(line, commentChars);
    if (StringUtility.isBlank(line)) return orgLine;
    String[] tags= StringUtils.substringsBetween(line, TAG_START, TAG_END);
    if (ArrayUtils.isEmpty(tags)) return orgLine;
    
    for (int i=0, n= tags.length; i<n; i++) {
      String v= getProperyValue(tags[i], commentChars, sourceConfig.pData);
      if (v==null) continue;
      String tag= TAG_START + tags[i] + TAG_END;
      line= StringUtils.replace(line, tag, v);
    }
    return line;
  }

  
  private String removeComment(String line) {
    return removeComment(line, pCommentChars);
  }
  
/** remove comments from a line an return it */
  private static String removeComment(String line, String commentChars) {
    int i= StringUtility.indexOfAny(line, commentChars);
    if (i<0) return line;
    return StringUtility.left(line, i).trim();
  }

  /**
 * Get the value of a property either from this config or from system properties
 * @param name name of property
 * @return value of property, null if not found
 */  
  private static String getProperyValue(String name, String commentChars, ListOfString propertiesSource) {
    int i= propertiesSource.indexOfName(name);
    if (i>=0) {
      String value= propertiesSource.getValueAtIndex(i);
      value= removeComment(value, commentChars);
      return value;
    }
    return System.getProperty(name);
  }

/** Get the property name for including a file, the default is <code>includeFile</code> */
  public String getIncludeFilePropertyName() {
    return includeFilePropertyName;
  }

/** Set the property name for including a file, the default is <code>includeFile</code> */
  public void setIncludeFilePropertyName(String includeFilePropertyName) {
    this.includeFilePropertyName = includeFilePropertyName;
  }


/** for testing */
  public static void main(String[] args) throws Exception {
    ConfigFile cf= new ConfigFile("tokenizer.test.properties");
    ListOfString names= cf.getAllNames();
    System.out.println(names.toString());
  }  // main

}  // ConfigFile
