/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2005  Abdul Habra
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

package com.tek271.util.cache;

/**
 * An interface to represent a cached item.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 * @deprecated This interface is not used anymore. It will be removed later. This should
 * not impact any client code, because this interface always had a package scope.
 */
interface ICacheItem {

/** Set the value to be cached */
  public void setValue(final Object aValue);

/** Get the cached value */
  public Object getValue();

/**
 * Get the time stamp when this object was created
 * @deprecated
 */
  public long getCreateTime();

/**
 * Time-To-Live in seconds
 * @deprecated
 */
  public int getTtlSeconds();

/**
 * Get age of the object in seconds
 * @deprecated
 */
  public int getAge();

/**
 * Has this cache item expired ie age > ttl
 * @deprecated
 */
  public boolean isExpired();


}  // ICacheItem
