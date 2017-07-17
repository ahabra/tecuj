/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2008  Abdul Habra.
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

package com.tek271.util.collections.set;

import org.apache.commons.collections4.SetUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Abdul Habra. Copyright &copy; Abdul Habra 2008.
 */
public class SetUtility {

  /** Create a set from the given array */
  public static Set toSet(Object[] values) {
    if (values==null) return null;
    Set r= new HashSet();
    for (int i=0, n=values.length; i<n; i++) {
      r.add(values[i]);
    }
    return r;
  }

}
