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

package com.tek271.util.time;

import java.util.*;
import org.apache.commons.lang3.time.*;

/**
 * Date/Time functions.
 * <p>Copyright (c) 2004, 2005 Technology Exponent</p>
 * @author Abdul Habra, Doug Estep
 * @version 1.0
 */
public class DateUt extends DateUtils {

/**
 * Truncates two dates, leaving the field specified as the most significant
 * field, and compares them.
 *
 * @param aDt1 The date to compare to aDt2.
 * @param aDt2 The date to be compared.
 * @param aField  The field from Calendar or SEMI_MONTH.
 * @return The value <code>0</code> if the <code>aDt1</code> is equal to
 *         <code>aDt2</code>; a value less than <code>0</code> if this
 *         <code>aDt1</code> is before the <code>aDt2</code>; and a
 *         value greater than <code>0</code> if this <code>aDt1</code>
 *         is after the <code>aDt2</code>.
 */
  public static int compareTo(final Date aDt1, final Date aDt2, final int aField) {
      Date dt1 = DateUtils.truncate(aDt1, aField);
      Date dt2 = DateUtils.truncate(aDt2, aField);
      return dt1.compareTo(dt2);
  }

/**
 * Truncates two dates, assuming that the field specified as the most significant
 * field is days, and compares them.
 *
 * @param aDt1 The date to compare to aDt2.
 * @param aDt2 The date to be compared.
 * @return The value <code>0</code> if the <code>aDt1</code> is equal to
 *         <code>aDt2</code>; a value less than <code>0</code> if this
 *         <code>aDt1</code> is before the <code>aDt2</code>; and a
 *         value greater than <code>0</code> if this <code>aDt1</code>
 *         is after the <code>aDt2</code>.
 */
  public static int compareTo(final Date aDt1, final Date aDt2) {
      return compareTo(aDt1, aDt2, Calendar.DATE);
  }

/** Return current time in given format */
  public static String currentDateTime(final String aFormat) {
    return DateFormatUt.formatDate(new Date(), aFormat);
  } // currentDateTime

/** Return current time in yyyy.MM.dd:HH.mm.ss:SSS format */
  public static String currentDateTime() {
    return DateFormatUt.formatDate(new Date(), DateFormatUt.FORMAT_DEFAULT);
  } // currentDateTime

  public static int currentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  public static void main(String[] args) {
    System.out.println(currentYear() );
  }

}
