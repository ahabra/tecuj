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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.apache.commons.lang.time.*;
import com.tek271.util.string.StringUtility;

/**
 * Date/Time formatting functions.
 * <p>Copyright (c) 2004, 2005 Technology Exponent</p>
 * @author Abdul Habra, Doug Estep
 * @version 1.0
 * <p><b>History</b><ol>
 * <li>2005.10.28: Added formatDate(long) </li>
 * </ol></p>
 */
public class DateFormatUt extends DateFormatUtils {

  public final static String FORMAT_DEFAULT = "yyyy.MM.dd:HH.mm.ss:SSS";
//  public final static String FORMAT_DATE = "MM/dd/yyyy";
  public final static String FORMAT_DATE_TIME = "MM/dd/yyyy HH:mm:ss:SSS";

  /** Pattern for a date in the yyyy-MM-dd format.  */
  public static final String YEAR_DASH_MON_DASH_DAY = "yyyy-MM-dd";

  /**  Pattern for a date in the MM-dd-yyyy format. */
  public static final String MON_DASH_DAY_DASH_YEAR = "MM-dd-yyyy";

  /**  Pattern for a date in the MM/dd/yyyy format.  */
  public static final String MON_SLASH_DAY_SLASH_YEAR = "MM/dd/yyyy";

/**
 * Format aDate according to the given format.
 * @param aDate a Date object.
 * @param aFormat Must follow the rules of java.text.SimpleDateFormat class.
 * @return String The date formatted with the given format.
 */
  public static String formatDate(final Date aDate, final String aFormat) {
    if (aDate==null) return StringUtility.EMPTY;
    SimpleDateFormat formatter = new SimpleDateFormat(aFormat);
    return formatter.format( aDate );
  } // formatDate

/** Format aDate according to yyyy.MM.dd:HH.mm.ss:SSS */
  public static String formatDate(final Date aDate) {
    return formatDate(aDate, FORMAT_DEFAULT);
  } // formatDate

/** Format aDate according to the given format */
  public static String formatDate(final long aDate, final String aFormat) {
    Date d= new Date(aDate);
    return formatDate(d, aFormat);
  }

/** Format aDate according to yyyy.MM.dd:HH.mm.ss:SSS */
  public static String formatDate(final long aDate) {
    return formatDate(aDate, FORMAT_DEFAULT);
  }

  public static String reformat(final String aDateTime, final String aFormat) {
    Date dt= string2Date(aDateTime, aFormat);
    if (dt==null) return null;
    return formatDate(dt, aFormat);
  }

  public static String reformat(final String aDateTime) {
    return reformat(aDateTime, FORMAT_DEFAULT);
  }

/**
 * Convert a string into a Date object using the given format.
 * @param aDateTime The date/time as a string.
 * @param aFormat Format of the given date/time following the rules of
 *    java.text.SimpleDateFormat claqss.
 * @return Date return a populated Date object. Null if aTime is not well formatted.
 */
  public static Date string2Date(final String aDateTime, final String aFormat) {
    SimpleDateFormat formatter = new SimpleDateFormat(aFormat);
    try {
      return formatter.parse(aDateTime);
    }
    catch (ParseException e) {
      return null;
    }
  } // string2Date

/**
 * Convert a string into a Date object using FORMAT_DEFAULT.
 * @param aDateTime The date/time as a string.
 * @return Date return a populated Date object. Null if aTime is not well formatted.
 */
  public static Date string2Date(final String aDateTime) {
    return string2Date(aDateTime, FORMAT_DEFAULT);
  } // string2Date


}
