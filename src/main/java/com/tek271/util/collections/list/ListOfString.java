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

package com.tek271.util.collections.list;

import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.codec.language.*;
import org.apache.commons.io.IOUtils;

import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.string.StringComparator;
import com.tek271.util.string.StringUtility;

/**
 * Encapsulates a list whose items are String objects. This class
 *   extends the ArrayList class. Some interesting features are:
 * <ol>
 * <li>Can read/write to a text file where each line is an item in the list.
 * <li>Can populate from a page at a given URL where each line is an item in the list.
 * <li>Items can be in the form: <code>name=value</code>. With methods to access by name.
 * <li>Many different indexOf() methods.
 * <li>Intersection/Difference/Merge two lists.
 * <li>Sort items of the list.
 * </ol>
 * @author Abdul Habra
 * @version 1.0
 */
public class ListOfString extends ArrayList {

  /** Case sensitivity for searches. Default=true */
  public boolean isCaseSensitive = true;

  /** The symbol used for name/value pairs. The default is = */
  public String valueEquator = "=";

  /** The character to separate lines of text. The default is \n */
  public String lineSeparator = "\n";

  /** The string that indicates the start of a single line comment */
  public String commentStartSingleLine = ";";

  private int pIndex; // use with setText

public ListOfString() {}

public ListOfString(int aInitialCapacity) {
  super(aInitialCapacity);
}

/** Create a list from the elements of the given array */
public ListOfString(final String[] aInitialElements) {
  setArray(aInitialElements, true);
}

/** Create a list from the given text */
public ListOfString(final String aText, final String aLineSeparator) {
  lineSeparator= aLineSeparator;
  setText(aText);
}

/** Create a ListOfString object and assign aText to its text property */
public static ListOfString createFromString(final String aText) {
  ListOfString r= new ListOfString();
  r.setText(aText);
  return r;
} // createFromString

/** Create a ListOfString object and assign aText to its text property */
public static ListOfString createFromString(final String aText,
                                            final String aLineSeparator) {
  ListOfString r= new ListOfString();
  r.lineSeparator= aLineSeparator;
  r.setText(aText);
  return r;
} // createFromString

/**
* Create a ListOfString object, splits aText into separate strings with
* aFixedWidth size, and adds each String to the ListOfString.
* <p>
* If aText is not divisible by aFixedWidth, the remainder of the aText will
* be added as the last element in the list without an exception.
* <br>author: Doug Estep, Abdul Habra.
* @param aText The string.
* @param aFixedWidth the fixed width to divide up the String.  If it is less than 1,
 * null is returned.
* @return ListOfString returns a ListOfString containing an element for each
* piece of the String.
*/
public static ListOfString createFromString(final String aText, final int aFixedWidth) {
  if (aFixedWidth<1) return null;
  ListOfString r= new ListOfString();
  if (StringUtility.isEmpty(aText)) return r;

  int i=0;
  while (true) {
    String item= StringUtility.substringBlock(aText, i++, aFixedWidth);
    if (item==null) break;
    r.add(item);
  }

  return r;
} // createFromString

/** Check if this list is equal to aList, respecting isCaseSensitive flag */
public boolean equals(final Object aList) {
  if (aList==null) return false;
  if (!(aList instanceof ListOfString)) return false;
  return isEqual( (ListOfString) aList );
}  // equals

/** Check if this list is equal to aList, respecting isCaseSensitive flag */
public boolean isEqual(final ListOfString aList) {
  if (aList==null) return false;
  int n= size();
  if (aList.size() != n) return false;
  for (int i=0; i<n; i++) {
    if (!StringUtility.equals(getItem(i), aList.getItem(i), isCaseSensitive)) return false;
  }
  return true;
} // isEqual

/**
 * Find the index of aItem in the list taking case sensetivity into consideration
 * @param aItem to find
 * @return index of found item, -1 if not found.
 */
public int indexOf(String aItem) {
  if (isCaseSensitive) return super.indexOf(aItem);

  for(int i=0,n=size(); i<n; i++) {
    if ( StringUtility.equalsIgnoreCase(getItem(i), aItem)) return i;
  } // for
  return -1;
} // indexOf

/**
 * Find the index of any item in aTarget that exist in this object
 * @param aTarget ListOfString List of items, any of which could be found
 * @return int index of found item, -1 if not found.
 */
public int indexOfAny(final ListOfString aTarget) {
  for (int i=0, n= aTarget.size(); i<n; i++) {
    int d= indexOf(aTarget.getItem(i));
    if (d>=0) return d;
  }
  return -1;
} // indexOfAny

/**
 * Find the index of last occurance of aItem in the list taking case
 * sensetivity into consideration.
 * @param aItem to find
 * @return index of found item, -1 if not found.
 */
public int lastIndexOf(final String aItem) {
  if (isCaseSensitive) return super.lastIndexOf(aItem);

  for(int i=size()-1; i>=0; i--) {
    if ( StringUtility.equalsIgnoreCase(getItem(i), aItem)) return i;
  } // for
  return -1;
} // lastIndexOf

/**
 * Find the index of the item that starts with aItemPrefix
 * @param aItemPrefix The prefix of the item to find.
 * @return Index of found item, -1 if not found.
 */
public int indexOfStart(final String aItemPrefix) {
  for(int i=0, n=size(); i<n; i++) {
    if (StringUtility.startsWith(getItem(i), aItemPrefix, isCaseSensitive ))
      return i;
  }
  return -1;
}  // indexOfStart

/** Find the index of the item that IS a prefix of the given string */
public int indexOfPrefixItem(final String aString) {
  for(int i=0, n=size(); i<n; i++) {
    if (StringUtility.startsWith(aString, getItem(i), isCaseSensitive ))
      return i;
  }
  return -1;
}  // indexOfPrefixItem

/** Find the index of the item that IS a suffix of the given string */
public int indexOfSuffixItem(final String aString) {
  for(int i=0, n=size(); i<n; i++) {
    if (StringUtility.endsWith(aString, getItem(i), isCaseSensitive ))
      return i;
  }
  return -1;
}  // indexOfSuffixItem()

/** Find the index of the item that is a substring of the given aString */
public int indexOfSubstringItem(final String aString) {
  for(int i=0, n=size(); i<n; i++) {
    if (StringUtility.indexOf(aString, getItem(i), isCaseSensitive ) >=0 )
      return i;
  }
  return -1;
}  // indexOfSubstringItem

/**
 * Populate this list from a text string, each line will become an element in
 * the list.
 * Uses the value of <b>lineSeparator</b> to separate lines.
 * @param aText The string to convert into a list.
 */
public void setText(String aText) {
  setText(aText, false);
} // setText

/**
 * Populate this list from a text string, each line will become an element in
 * the list.
 * Uses the value of <b>lineSeparator</b> to separate lines.
 * @param aText The string to convert into a list.
 */
public void setText(final String aText, final boolean aTrimLines) {
  if (aText==null) return;
  pIndex=0;
  String line;
  while (true) {
    line = getNextLine(aText);
    if (line==null) break;
    if (aTrimLines) line= line.trim();
    add(line);
  }
} // setText

/**
* get the next line from aText starting at pIndex.
* advance pIndex to the beginning of the next line.
* If pIndex > length of aText, return null
*/
private String getNextLine(String aText) {
  if (pIndex>=aText.length()) return null;
  int start= pIndex;
  int eolIndex = aText.indexOf(lineSeparator, start);
  if (eolIndex == -1) {
    pIndex = aText.length()+1;
    return aText.substring(start);
  }
  // lineSeparator found at eolIndex
  pIndex = eolIndex + lineSeparator.length();
  return aText.substring(start, eolIndex);
} // getNextLine()

/**
 * Convert this list's item into a string of lines separated by
 * <b>lineSeparator</b>.
 * @return The list as text.
 */
public String getText(final String aLineSeparator) {
  int n=size();
  if (n<1) return StringUtility.EMPTY;
  StringBuffer buf= new StringBuffer(n*16);
  for (int i=0; i<n-1; i++) {
    buf.append(getItem(i)).append(aLineSeparator);
  }
  buf.append(getItem(n-1));
  return buf.toString();
}   // getText

public String getText() {
  return getText(lineSeparator);
}

/**
* Convert the list into a string.
* Elements are separated by lineSeparator.
* @return The list as a string.
*/
public String toString() {
  return getText();
} // toString()

public static String toString(final ListOfString aList) {
  if (aList==null) return StringUtility.EMPTY;
  return aList.getText();
}

/**
 * Treating the list items as name/value pairs, find the first index of the
 * given name. Uses <b>valueEquator</b> to separate name and value.
 * @param aName Name to find.
 * @return Index of found item, -1 if not found.
 */
public int indexOfName(final String aName) {
  return indexOfName(aName, 0);
}  // indexOfName

/**
 * Treating the list items as name/value pairs, find the first index of the
 * given name. Uses <b>valueEquator</b> to separate name and value.
 * @param aName String Name to find.
 * @param aStartIndex int Starting index to start the search from
 * @return int Index of found item, -1 if not found.
 */
public int indexOfName(String aName, final int aStartIndex) {
  if (aName==null) return -1;
  aName= aName.trim();
  if (aName.length()==0) return -1;
  aName += valueEquator;

  for (int i=aStartIndex, n=size(); i<n; i++) {
    if (StringUtility.startsWith(getItem(i), aName, isCaseSensitive))
      return i;
  }
  return -1;
}  // indexOfName

/**
 * Treating the list items as name/value pairs, get the name at the given index.
 * @param aIndex The index in the list to get its name.
 * @return The name at the given index
 */
public String getNameAtIndex(int aIndex) {
  String item = getItem(aIndex);
  if (item.length() == 0) return StringUtility.EMPTY;

  int equalIndex = item.indexOf(valueEquator);
  if (equalIndex<0) {return item; }
  else if (equalIndex == 0) {return StringUtility.EMPTY; }
  else return item.substring(0, equalIndex);
} // getNameAtIndex

/**
 * Treating the list items as name/value pairs, set the name at the given index.
 * @param aIndex The index in the list to set its name.
 * @param aName The name to set.
 */
public void setNameAtIndex(int aIndex, String aName) {
  String value = getValueAtIndex(aIndex);
  set(aIndex, aName + valueEquator + value);
} // setNameAtIndex

/**
 * Treating the list items as name/value pairs, get the value at the given index.
 * @param aIndex The index in the list to get its value.
 * @return The value at the given index
 */
public String getValueAtIndex(int aIndex) {
  String item = getItem(aIndex);
  if (item.length() == 0) return StringUtility.EMPTY;

  int equalIndex = item.indexOf(valueEquator);
  if (equalIndex<0) return item;
  
  return item.substring(equalIndex + valueEquator.length());
} // getValueAtIndex

/**
 * Treating the list items as name/value pairs, set the value at the given index.
 * @param aIndex The index in the list to set its value.
 * @param aValue The value to set.
 */
public void setValueAtIndex(int aIndex, String aValue) {
  String name= getNameAtIndex(aIndex);
  set(aIndex, name + valueEquator + aValue );
} // setValueAtIndex

/**
 * Treating the list items as name/value pairs, get the value for the given name.
 * @param aName The name to search for.
 * @return The value for the given name, empty string if name not found.
 */
public String getValueOfName(String aName) {
  int index = indexOfName(aName);
  if (index<0) return StringUtility.EMPTY;
  return getValueAtIndex(index);
} // valueOfNameGet


/**
 * Treating the list items as name/value pairs, set the value for the given name.
 * If the name does not already exists, add a new item to the list.
 * @param aName The name to search for.
 * @param aValue The value to set.
 */
public void setValueOfName(String aName, String aValue) {
  String item = aName + valueEquator + aValue;
  int index = indexOfName(aName);
  if (index<0)  add(item);
  else  set(index, item);
} // valueOfNameSet

/** Check if the line at the given index contains a name=value pair */
public boolean isNameValuePair(final int aIndex) {
  String s= StringUtility.trimLeft( getItem(aIndex) );
  if (s.length() == 0) return false;
  if (s.startsWith(commentStartSingleLine)) return false;
  int i= s.indexOf(valueEquator);
  if (i<1) return false;
  return true;
}

/**
* Write the list to a text file.
* items are separated by lineSeparator
* @param aFileName The name of the file to save into.
* @throws IOException If the attempt to write failed.
*/
public void writeToTextFile(final String aFileName) throws IOException {
  BufferedWriter out = new BufferedWriter(new FileWriter(aFileName));
  out.write(getText());
  out.close();
} // writeToTextFile

/**
* Read a text file into the list, each line is an item.
* @param aFileName The name of the file to read from.
* @throws IOException If the attempt to read the file failed.
*/
public void readFromTextFile(final String aFileName) throws IOException {
  BufferedReader in = new BufferedReader( new FileReader(aFileName) );
  readFromBufferedReader(in);
} // readFromTextFile

/**
* Read from a given input stream into this object.
* Will close the stream when done.
* @param aStream The stream to read from.
* @throws IOException If the attempt to read the stream failed.
*/
public void readFromStream(InputStream aStream) throws IOException {
  InputStreamReader inpr= new InputStreamReader(aStream);
  BufferedReader rdr= new BufferedReader(inpr);
  readFromBufferedReader(rdr);
}  // readFromStream();

/**
 * Read from a given reader into this object.
 * Will close the reader when done.
 * @param aReader Reader The reader to read from.
 * @throws IOException If the attempt to read failed.
 */
public void readFromReader(final Reader aReader) throws IOException {
  BufferedReader rdr= new BufferedReader(aReader);
  readFromBufferedReader(rdr);
}  // readFromStream();

/**
* Read from a given buffered reader into this object.
* Will close the reader when done.
* @param aReader The buffered reader to read from.
* @throws IOException If the attempt to read the buffered reader failed.
*/
public void readFromBufferedReader(BufferedReader aReader) throws IOException {
  String line = aReader.readLine();
  while (line != null) {
    add(line);
    line = aReader.readLine();
  } // while
  aReader.close();
}  // readFromStream();

/**
* Read the page at the given url into this object
* @param aUrl The url of the page to read.
* @throws IOException
*/
public void readFromUrl(String aUrl) throws IOException {
  URL url = new URL(aUrl);

  InputStream is = url.openStream();
  readFromStream(is);
  is.close();
}  // readFromUrl

/**
 * Read from the given file which resides in the current context.
 * @param fileName  The name of the file to read from.
 * @throws IOException If the attempt to read the file failed.
 */
public void readFromTextFileInContext(final String fileName) throws IOException {
  ClassLoader classLoader= Thread.currentThread().getContextClassLoader();
//  ClassLoader classLoader= ClassLoader.getSystemClassLoader();
  readFromTextFileInContext(fileName, classLoader);
}

/**
 * Read from the given file which resides in the context of the given class loader
 * @param fileName  The name of the file to read from.
 * @param classLoader The class loader whose context should contain the given file
 * @throws IOException If the attempt to read the file failed.
 */
public void readFromTextFileInContext(final String fileName, final ClassLoader classLoader) throws IOException {
  InputStream is= classLoader.getResourceAsStream(fileName);
  if (is==null)  {
    throw new IOException("Cannot find " + fileName + " in context");
  }
  
  try {
    readFromStream(is);
  } finally {
    IOUtils.closeQuietly(is);
  }
}

/**
 * Add a name/value pair separated by <b>valueEquator</b>.
 * @param aName The name of the new item.
 * @param aValue The value of the new item.
 */
public void add(String aName, String aValue) {
  add(aName + valueEquator + aValue);
} // add

/**
 * Add a name/value pair separated by <b>valueEquator</b>.
 * @param aName The name of the new item.
 * @param aValue The integer value of the new item.
 */
public void add(String aName, int aValue) {
  add(aName + valueEquator + aValue);
} // add

/**
 * Add a new name/value pair to the list, supporting multivalue items.
 * @param aName The name of the new item.
 * @param aValue The value of the new item.
 * @param aAppend When the list already has an item with the same aName, if
 * aAppend is true, then append aValue to the existing value (separated by aSeparator),
 * if false, then just add the item to the end of list.
 * @param aSeparator The separator between multiple values.
 */
public void add(String aName, String aValue, boolean aAppend, String aSeparator) {
  if (! aAppend) {
    add(aName, aValue);
    return;
  }

  int i = indexOfName(aName);
  if (i==-1)
    add(aName, aValue);
  else {
    setValueAtIndex(i, getValueAtIndex(i) + aSeparator + aValue);
  }
} // add

/**
 * Add a new name/value pair to the list, supporting multivalue items separated
 * by space character.
 * @param aName The name of the new item.
 * @param aValue The value of the new item.
 * @param aAppend When the list already has an item with the same aName, if
 * aAppend is true, then append aValue to the existing value (separated by space),
 * if false, then just add the item to the end of list.
 */
public void add(String aName, String aValue, boolean aAppend) {
  add(aName, aValue, aAppend, StringUtility.BLANK);
} // add


public void addGroup(final String aName, final String[] aValues, final boolean aAppend) {
  String v= ArrayUtilities.toString(aValues, StringUtility.COMMA);
  add(aName, v, aAppend, StringUtility.COMMA);
}

/**
 * Add aItem to the list if it is not null or zero length.
 * @param aItem Item to add
 */
public void addNoNull(final String aItem) {
  if (StringUtility.isEmpty(aItem)) return;
  add(aItem);
}

/**
 * Add an item to the list if it does not already exists.
 * @param aItem Item to add.
 */
public void addUnique(final String aItem) {
  if (this.indexOf(aItem) >= 0) return;
  add(aItem);
}

/**
 * Add an item to the list if is not null or zero length and if it does
 * not already exists.
 * @param aItem Item to add.
 */
public void addUniqueNoNull(final String aItem) {
  if (StringUtility.isEmpty(aItem)) return;
  if (this.indexOf(aItem) >= 0) return;
  add(aItem);
}

/** Add an integer item */
public void add(final int aItem) {
  add(String.valueOf(aItem));
}

public void addItem(final String aItem) {
  add(aItem);
}


/**
* Set the values of an array into the list.
* @param aValues A string array to populate the list.
* @param aIsSameOrder if true, add items in the same order as in the array, if false,
*        add items in reverse order.
*/
public void setArray(final String[] aValues, final boolean aIsSameOrder) {
  if (ArrayUtilities.isEmpty(aValues)) return;
  if (aIsSameOrder) {
    for (int i=0, n=aValues.length; i<n; i++) {
      add(aValues[i]);
    }
  } else {
    for (int i=aValues.length-1; i>=0; i--) {
      add(aValues[i]);
    }
  }
 } // setArray();

/**
* Set the values of an array into the list.
* @param aValues A string array to populate the list.
*/
public void setArray(final String[] aValues) {
  setArray(aValues, true);
}

/**
* Set the values of an array into the list of name=value pairs.
* @param aValues A 2-dimentional string array to populate the list,
* aValues[i][0] is a name, aValues[i][1] is the value
* @param aIsSameOrder if true, add items in the same order as in the array, if false,
*        add items in reverse order.
*/
public void setArray(final String[][] aValues, final boolean aIsSameOrder) {
  if (aIsSameOrder) {
    for (int i=0, n=aValues.length; i<n; i++) {
      add(aValues[i][0], aValues[i][1]);
    }
  } else {
    for (int i=aValues.length-1; i>=0; i--) {
      add(aValues[i][0], aValues[i][1]);
    }
  }
 } // setArray();

/**
* Set the values of an array into the list of name=value pairs.
* @param aValues A string array to populate the list.
*/
public void setArray(final String[][] aValues) {
 setArray(aValues, true);
}

/**
* Get the contents of this list as an array of strings.
* @return The list as an array of String.
*/
public String[] getArray() {
  int n= size();
  String[] r = new String[n];
  for (int i=0; i<n; i++) {
    r[i]= getItem(i);
  }
  return r;
} // getArray()

/**
 * Get the item at the given index as a string.
 * @param aIndex index in the list
 * @param aIsTrim Trim the item before returning it?
 * @return The item in the list, if item is null, return "".
 */
public String getItem(final int aIndex, final boolean aIsTrim) {
  Object it= get(aIndex);
  if (it==null) return StringUtility.EMPTY;
  String s= (String) it;
  if (aIsTrim) return s.trim();
  return s;
}

public String getItem(final int aIndex) {
  return getItem(aIndex, false);
}

/** get the last item in the list */
public String getLastItem() {
  int s= size();
  if (s==0) return StringUtility.EMPTY;
  return getItem(s-1);
}  // getLastItem


/**
 * Set the value of the last item in the list
 * @param aValue String the new value
 * @param aAppendIfEmpty boolean if list is empty, add a new item.
 */
public void setLastItem(final String aValue, final boolean aAppendIfEmpty) {
  int s= size();
  if (s==0) {
    if (aAppendIfEmpty) add(aValue);
    return;
  }
  set(s-1, aValue);
}  // setLastItem

public void setLastItem(final String aValue) {
  setLastItem(aValue, true);
}

/**
 * Check if aItem exists in the list.
 * @param aItem The item to search for.
 * @return true if item exists, false if not.
 */
public boolean isExist(final String aItem) {
  return this.indexOf(aItem)>=0;
}

/**
 * Find the items that exist in this list and in the given list.
 * @param aList List to check against.
 * @return Common items.
 */
public ListOfString intersect(ListOfString aList) {
  ListOfString r= new ListOfString();
  for(int i=0, n= aList.size(); i<n; i++) {
    String itm= aList.getItem(i);
    if (isExist(itm)) r.add(itm);
  }
  return r;
}

/**
 * Find the items that are members of this list but are not members of aList.
 * @param aList List to check against.
 * @return Items that exist in this list but not in aList.
 */
public ListOfString notIntersect(ListOfString aList) {
  ListOfString r= new ListOfString();
  boolean sens= aList.isCaseSensitive;  // save original case sensetivity
  aList.isCaseSensitive= isCaseSensitive;
  for (int i=0, n=size(); i<n; i++) {
    String itm= getItem(i);
    if (!aList.isExist(itm)) r.add(itm);
  }
  aList.isCaseSensitive= sens;  // restore original case sensetivity
  return r;
}

/**
 * Merge two lists item by item. Lists MUST have the same size
 * @param aList1
 * @param aList2
 * @param aItemSeparator Separator between items of lists
 * @return a list where each item is of the following format:<br>
 * list1ItemN aItemSeparator list2ItemN. If aList1 and aList2 has different
 * sizes, return null.
 */
public static ListOfString merge(ListOfString aList1,
                                 ListOfString aList2,
                                 String aItemSeparator) {
  if (aList1.size() != aList2.size()) return null;
  ListOfString r= new ListOfString();
  for(int i=0, n=aList1.size(); i<n; i++) {
    r.add(aList1.getItem(i) + aItemSeparator + aList2.getItem(i));
  }
  return r;
}

/**
 * Add all the contents of the Map to the list, assuming the map's keys and values
 * are of string type.
 * @param aMap A map where the keys and values are Strings.
 */
public void addAll(Map aMap) {
  Set set= aMap.entrySet();
  Map.Entry ent;
  for (Iterator i= set.iterator(); i.hasNext(); ) {
    ent= (Map.Entry) i.next();
    add( (String)ent.getKey(), (String)ent.getValue());
  }
} // addAll

/** Sort this list string items into ascending order */
public void sort() {
  sort(true);
}  // sort

/**
 * Sort this list in either ascending or descending order.
 * <p>Bug Fix on 2007.12.11: take into consideration the <code>isCaseSensitive</code>
 * field.
 * </p>
 * @param aIsAscending boolean if true sort in ascending order, if false sort in
 * descending order
 */
public void sort(final boolean aIsAscending) {
  int sz= size();
  String[] ar= getArray();
  Arrays.sort(ar, new StringComparator(isCaseSensitive));
  clear();
  ensureCapacity(sz);

  setArray(ar, aIsAscending);
}  // sort

/**
 * Sort this list according to the given comparator
 * @param aComparator the comparator to determine the order of the array. A null value
 * indicates ascending order.
 */
public void sort(final Comparator aComparator) {
  int sz= size();
  String[] ar= getArray();
  Arrays.sort(ar, aComparator);
  clear();
  ensureCapacity(sz);
  setArray(ar, true);
}  // sort

/** Check if line at aIndex is a comment line */
public boolean isCommentLine(final int aIndex) {
  String s= getItem(aIndex, true);
  return (s.startsWith(commentStartSingleLine));
}

/** Remove comment and blank lines */
public void removeLines(final boolean aIsRemoveComments,
                        final boolean aIsRemoveBlankLines) {
  for (int i=size()-1; i>=0; i--) {
    if (aIsRemoveBlankLines && StringUtility.isBlank(getItem(i)) ) remove(i);
    else
    if (aIsRemoveComments && isCommentLine(i)) remove(i);
  }
}  // removeLines();

public static final int REMOVE_BLANKS=1;
public static final int REMOVE_COMMENTS=2;
public static final int REMOVE_BLANKS_AND_COMMENTS=4;

/** Remove comment and blank lines */
public void removeLines(final int aRemoveMask) {
  if (aRemoveMask==REMOVE_BLANKS_AND_COMMENTS) removeLines(true, true);
  else
  if (aRemoveMask==REMOVE_BLANKS) removeLines(false, true);
  else
  if (aRemoveMask==REMOVE_COMMENTS) removeLines(true, false);
}  // removeLines

/** Find the subset of items that start with aItemStart */
public ListOfString subsetItemStart(final String aItemStart) {
  ListOfString r= new ListOfString();
  String s;
  for(int i=0, n=size(); i<n; i++) {
    s= getItem(i);
    if (StringUtility.startsWith(s, aItemStart, isCaseSensitive))   r.add(s);
  }
  return r;
} // subsetItemStart

public ListOfString copy() {
  return (ListOfString) clone();
}

public Object clone() {
  ListOfString r= (ListOfString) super.clone();
// The following immutable fields are copied automatically by the Object.clone()
//  r.commentStartSingleLine= commentStartSingleLine;
//  r.isCaseSensitive= isCaseSensitive;
//  r.lineSeparator= lineSeparator;
//  r.valueEquator= valueEquator;
  return r;
}

/** Check if the list has one item only, and that it is equal to the given value */
public boolean isOneItem(final String aItem) {
  if (size() != 1) return false;
  return StringUtility.equals(getItem(0), aItem, isCaseSensitive);
}  // isOneItem

/** Check if list has only items of either aItem1 or aItem2 */
public boolean isAnyOfTwoItems(final String aItem1, final String aItem2) {
  int n= size();
  if (n==0 || n>2) return false;

  String t= getItem(0);
  if (!StringUtility.equals(t, aItem1, isCaseSensitive) &&
      !StringUtility.equals(t, aItem2, isCaseSensitive)) return false;

  if (n==1) return true;
  t= getItem(1);
  if (!StringUtility.equals(t, aItem1, isCaseSensitive) &&
      !StringUtility.equals(t, aItem2, isCaseSensitive)) return false;

  return true;
} // isAnyOfTwoItems

/** Find the index of the item with the maximum length */
public int indexOfLongestItem(final boolean aIsTrim) {
  int sz= size();
  if (sz==0) return -1;
  int j=0;
  int maxLen=getItem(0, aIsTrim).length();
  for (int i=1; i<sz; i++) {
    int len= getItem(i, aIsTrim).length();
    if (len>maxLen) {
      maxLen=len;
      j=i;
    }
  }
  return j;
}  // indexOfLongestItem

/** Get the item with the maximum length, empty string if no items */
public String getLongestItem(final boolean aIsTrim) {
  int i= indexOfLongestItem(aIsTrim);
  if (i<0) return StringUtility.EMPTY;
  return getItem(i);
}  // getLongestItem

/** trim all items of this list */
public void trim() {
  for (int i=0, n=size(); i<n; i++) {
    set(i, getItem(i, true) );
  }
}  // trim

/**
 * Insert aValue at the given index, shifting all following values by one.
 * @param aIndex int index to insert at
 * @param aValue String Value to insert
 */
public void insert(int aIndex, final String aValue) {
  if (aIndex<0) aIndex=0;
  if (aIndex>= size() ) {
    add(aValue);
    return;
  }

  add(getLastItem());
  for (int i=size()-2; i>aIndex; i--) {
    set(i, getItem(i-1));
  }
  set(aIndex, aValue);
}  // insert

/** Remove the first occurance of the given item */
public boolean remove(String aItem) {
  int i= this.indexOf(aItem);
  if (i<0) return false;
  super.remove(i);
  return true;
}

/** Remove the first occurance of the given item */
public boolean remove(Object aItem) {
  return this.remove( (String) aItem );
}


/** Remove the item with the given name */
public void removeNameValue(final String aName) {
  int i= indexOfName(aName);
  if (i<0) return;
  remove(i);
}

/** Check if name exist */
public boolean isNameExist(final String aName) {
  return indexOfName(aName) >= 0;
}

/** get the values at the given index */
public String[] getValues(final int aIndex) {
  String v= getValueAtIndex(aIndex);
  if (StringUtility.isEmpty(v)) return ArrayUtilities.EMPTY_STRING_ARRAY;
  return StringUtility.split(v, StringUtility.COMMA);
}

/** get the values for the given name */
public String[] getValues(final String aName) {
  int i= indexOfName(aName);
  if (i<0) return ArrayUtilities.EMPTY_STRING_ARRAY;
  return getValues(i);
}

/**
 * Create a new list whose items are the soundex of items in this list.
 * @param aIsUnique if true, then the returned list will have unique items only.
 *        if false, the new list will have as many items as there is in this list.
 * @return ListOfString a list of soundex elements.
 */
public ListOfString soundex(final boolean aIsUnique) {
  ListOfString r= new ListOfString();
  //RefinedSoundex sndx= RefinedSoundex.US_ENGLISH;
  //Soundex sndx= Soundex.US_ENGLISH;  // has a bug !!!
  Soundex sndx= new Soundex();
  for (int i=0, n=size(); i<n; i++) {
    String item= getItem(i);
    String s= sndx.soundex(item);
    if (StringUtility.isBlank(s)) s= item;  // if item is a number its soundex is itself.

    if (aIsUnique) {
      if (StringUtility.isNotBlank(s)) r.addUnique(s);
    } else {
      r.add(s);
    }
  }
  return r;
}  // soundex()

/**
 * Create a new list whose items are the soundex of items in this list, the new
 * list will NOT contain duplicate items, which means that the new list may have
 * fewer elements than this list.
 * @return ListOfString a list of unique soundex elements.
 */
public ListOfString soundex() {
  return soundex(true);
}  // soundex()

/**
 * convert the name=value pairs into an ordered map
 * @return a map<String, String> of the name=value pairs. The map is ordered 
 * in the same way is this list's element, so the first element in the list 
 * will be the first element in the map. 
 */
public Map toMap() {
  Map map= new LinkedHashMap();
  for (int i=0, n=size(); i<n; i++) {
    String k= getNameAtIndex(i);
    String v= getValueAtIndex(i);
    map.put(k, v);
  }
  return map;
}

} // class ListOfString
