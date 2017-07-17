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

import java.util.*;
import com.tek271.util.string.StringUtility;

/**
 * When running in a case insensitive mode, we need to maintain the original key in order
 * to return it to the caller if needed. This class represent a value in a map with
 * its original key. Used only in case insensitive mode.
 * <p>Note that the class has a default package scope, and is not accessiable outside its
 * package.</p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
class MapValue implements Map.Entry {
  boolean isCaseSensetive;
  String orgKey;  // original key
  String value;

  MapValue(){}

  MapValue(final boolean aIsCaseSensetive, final String aOrgKey, final String aValue) {
    isCaseSensetive= aIsCaseSensetive;
    orgKey= aOrgKey;
    value= aValue;
  }

  public boolean equals(Object aMapValue) {
    if (aMapValue==null) return false;
    if (!(aMapValue instanceof MapValue)) return false;
    return isEquals((MapValue) aMapValue);
  }

  public boolean equals(MapValue aMapValue) {
    return isEquals(aMapValue);
  }

  public boolean isEquals(MapValue aMapValue) {
    if (! isEqualKey(aMapValue.orgKey)) return false;
    return isEqualValue(aMapValue.getValueAsString());
  }
  
  public boolean isEqualValue(final String aValue) {
    return StringUtility.equals((String) getValue(), aValue, isCaseSensetive);
  }
  
  public boolean isEqualKey(final String aKey) {
    return StringUtility.equals(orgKey, aKey, isCaseSensetive);
  }

  public Object getKey() {
    return orgKey;
  }

  public Object getValue() {
    return value;
  }

  public String getValueAsString() {
    return value;
  }

  public int hashCode() {
    int k= orgKey==null? 0 : orgKey.hashCode();
    int v= getValue()==null? 0 : getValue().hashCode();
    return k ^ v;
  }

  public Object setValue(Object aValue) {
    String oldStr= value;
    value= (String) aValue;
    return oldStr;
  }

  public String toString() {
    return "(" + orgKey + "=" + getValue() + ")";
  }

}  // MapValue
