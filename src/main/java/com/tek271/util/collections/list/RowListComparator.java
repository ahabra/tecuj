/*
 Technology Exponent Common Utilities For Java (TECUJ)
 Copyright (C) 2003,2004  Abdul Habra, Tom Comer
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

import java.io.Serializable;
import java.util.*;

/**
 * RowListComparator implements the <code>Comparator</code> interface
 * and provides the compare method to compare the rows in a RowList based on the column
 * and direction flag arrays passed into it.
 * <p>Note that the class has a default package scope, and is not accessiable outside its
 * package.</p>
 * @author Tom Comer, Abdul Habra
 * @version 1.0
 * @since 2.0
 */
class RowListComparator implements Comparator, Serializable {
  private static final String pERR_DATA_SIZE=
    "The number of columns to sort by, and the flags for sort order have to be equal.";
  private static final String pERR_SORT_SIZE=
    "Number of sort columns must equal number of sort direction flags.";

  private int[] pColumns;
  private int[] pFlags;
  private int pSortColumnsCount=0;

  /**
   * @param aColumnList int[] indexes of the columns to sort by.
   * @param aFlagList   int[] sort direction: +ve means ascending, -ve mean descening.
   */
  public RowListComparator(final int[] aColumnList, final int[] aFlagList) {
    if (aColumnList.length != aFlagList.length) {
      throw new IllegalArgumentException(pERR_SORT_SIZE);
    }
    pColumns=aColumnList;
    pFlags=aFlagList;
    pSortColumnsCount= pColumns.length; // cache this for performance
  }

  /**
   * Compare two rows from the RowList, each row is an array of objects.
   * @param aRow1 Object An array of objects that represent a row. Items of this array
   * must implement the Comparable interface.
   * @param aRow2 Object An array of objects that represent a row. Items of this array
   * must implement the Comparable interface.
   * @return int 0 if equal, +ve if aRow1>aRow2, -ve if aRow1 &lt; aRow2
   */
  public int compare(final Object aRow1, final Object aRow2) {
    Object[] r1= (Object[]) aRow1;
    Object[] r2= (Object[]) aRow2;

    if (r1.length!=r2.length) throw new IllegalArgumentException(pERR_DATA_SIZE);

    for (int i=0; i<pSortColumnsCount; i++) {
      int colIndex= pColumns[i];
      Object v1= r1[colIndex];
      Object v2= r2[colIndex];
      if (v1==null  && v2==null) continue;
      if (v2==null) return 1;
      if (v1==null) return -1;

      Comparable c1= (Comparable) v1;
      Comparable c2= (Comparable) v2;
      int comp= c1.compareTo(c2);
      if (comp != 0) return comp * pFlags[i];
    }

    return 0; // means the rows are equal
  }

}  // RowListComparator
