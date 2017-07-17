/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2006  Abdul Habra
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
package com.tek271.util.collections.map;

import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**n * Encapsulate a multi-value value for the MapOfStringMV class.
 * <p>Note that the class has a default package scope, and is not accessiable outside its
 * package.</p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
class MapValueMV extends MapValue {
  private ListOfString pValueList;

  MapValueMV(final boolean aIsCaseSensetive,
             final String aOrgKey,
             final String aValueList,
             final String aValueSeparator) {
    super(aIsCaseSensetive, aOrgKey, null);
    pValueList= new ListOfString();
    pValueList.isCaseSensitive= aIsCaseSensetive;
    pValueList.lineSeparator= aValueSeparator;
    pValueList.setText(aValueList);
  }

  MapValueMV(final boolean aIsCaseSensetive,
             final String aOrgKey,
             final String aValueList) {
    this(aIsCaseSensetive, aOrgKey, aValueList, StringUtility.COMMA);
  }

  MapValueMV(final boolean aIsCaseSensetive,
             final String aOrgKey,
             final ListOfString aValueList,
             final String aValueSeparator) {
    super(aIsCaseSensetive, aOrgKey, null);
    pValueList= aValueList.copy();
    pValueList.isCaseSensitive= aIsCaseSensetive;
    pValueList.lineSeparator= aValueSeparator;
  }

  public int hashCode() {
    return pValueList.hashCode();
  }
  
  public boolean equals(Object aMapValueMV) {
    if (aMapValueMV==null) return false;
    if (!(aMapValueMV instanceof MapValueMV)) return false;
    return equals((MapValueMV) aMapValueMV);
  }

  public boolean equals(MapValueMV aMapValueMV) {
    if (!StringUtility.equals(orgKey, aMapValueMV.orgKey, isCaseSensetive)) return false;
    return isEqualValue(aMapValueMV.getValueList());
  }


  public boolean isEqualValue(final ListOfString aValues) {
    return pValueList.isEqual(aValues);
  }

  public ListOfString getValueList() {
    return pValueList;
  }

  public void setValueList(final ListOfString aValueList) {
    pValueList= aValueList;
  }

  public String getValueAsString() {
    return pValueList.getText();
  }

  public Object getValue() {
    return getValueAsString();
  }

  public String setValue(final String aValueList) {
    String oldStr= getValueAsString();
    pValueList.clear();
    pValueList.setText(aValueList);
    return oldStr;
  }

  public Object setValue(final Object aValueList) {
    return setValue((String) aValueList);
  }

  public void appendValue(final String aValueList) {
    pValueList.setText(aValueList);
  }

  public void appendValue(final ListOfString aValueList) {
    pValueList.addAll(aValueList);
  }

  public String getValue(final int aIndex) {
    return pValueList.getItem(aIndex);
  }

}  // MapValueMV
