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

package com.tek271.util.internet.html;

import com.tek271.util.string.*;

/**
 * Given two colors and a range, get the gradient color for each step, in other words,
 * get the interpolation of color as we transition in the range.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class ColorGradient {
  private int pRangeStart;
  private int pRangeEnd;

  private Color pColorStart;
  private Color pColorEnd;

  private int pRangeDelta;

  private static class Color {
    int red, green, blue;

    Color(final int aRed, final int aGreen, final int aBlue) {
      red= aRed;
      green= aGreen;
      blue= aBlue;
    }

/** Create a Color object from a 6 hex digits string RRGGBB */
    Color(final String aColor) {
      if (StringUtility.isBlank(aColor)) return;
      if (aColor.length() != 6) return;

      String r= aColor.substring(0, 2);
      String g= aColor.substring(2, 4);
      String b= aColor.substring(4);

      red= colorToInt(r);
      green= colorToInt(g);
      blue= colorToInt(b);
    }

/** Convert a 2-digit hex to a number, return 0 if failed */
    private int colorToInt(final String aColor) {
      long c= StringUtility.fromHex(aColor);
      if (c<0) return 0;
      return (int) c;
    }

/** Convert a single color to a 2-digit hex */
    private String colorToString(final int aColor) {
      String c= StringUtility.toHex( (char) aColor);
      return c.substring(2);
    }

    public String toString() {
      return colorToString(red) + colorToString(green) + colorToString(blue);
    }
  }  // class Color


  public ColorGradient(final String aColorStart,
                       final String aColorEnd,
                       final int aRangeStart,
                       final int aRangeEnd) {
    pColorStart= new Color(aColorStart);
    pColorEnd= new Color(aColorEnd);
    pRangeStart= Math.min(aRangeStart, aRangeEnd);
    pRangeEnd= Math.max(aRangeStart, aRangeEnd);

    pRangeDelta= pRangeEnd - pRangeStart;
  }

/**
 * Get a string reprsenting the interpolation of color at the given position.
 * @param aPosition position in the range
 * @return String 6-char hex number representing the three color components RRGGBB.
 *         each color component is in one byte (2-hex digits)
 */
  public String getColorAt(final int aPosition) {
    if (aPosition <= pRangeStart) return pColorStart.toString();
    if (aPosition >= pRangeEnd) return pColorEnd.toString();

    int r= getScaledColor(aPosition, pColorStart.red, pColorEnd.red);
    int g= getScaledColor(aPosition, pColorStart.green, pColorEnd.green);
    int b= getScaledColor(aPosition, pColorStart.blue, pColorEnd.blue);

    Color c= new Color(r, g, b);

    return c.toString();
  }

  private int getScaledColor(final int aPosition,
                             final int aColorStart,
                             final int aColorEnd) {
    float dp= aPosition-pRangeStart;
    dp = dp / pRangeDelta;

    int dc= aColorEnd - aColorStart;
    return Math.abs( (int)(dp*dc) + aColorStart);
  }

}  // ColorGradient
