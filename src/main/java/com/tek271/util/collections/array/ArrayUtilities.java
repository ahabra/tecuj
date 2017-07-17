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

package com.tek271.util.collections.array;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.string.StringUtility;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * Generic array methods including different overloaded
 * <code>indexOf()</code> and <code>concat()</code> methods.
* @author Abdul Habra
* @version 0.1
*/
public class ArrayUtilities extends ArrayUtils {

  public static final byte[] ZERO_LEN_BYTE_ARRAY= {};
  public static final char[] ZERO_LEN_CHAR_ARRAY= {};
  public static final int[] ZERO_LEN_INT_ARRAY= {};
  public static final long[] ZERO_LEN_LONG_ARRAY= {};
  public static final float[] ZERO_LEN_FLOAT_ARRAY= {};
  public static final double[] ZERO_LEN_DOUBLE_ARRAY= {};
  public static final String[] ZERO_LEN_STRING_ARRAY= {};
  public static final Object[] ZERO_LEN_OBJECT_ARRAY= {};


/** Do not call this constructor. Allows extending the class. */
  public ArrayUtilities() {}

/** For testing */
  public static void main(String[] args) {
    String[] a= { "0", "1", "2", "3", "4", "5aa", "6dd"};
    String[] t= (String[]) subarray(a, 4, 20);

    System.out.println( toString(t) );

  } // main


/** Find the index of aChar in the given array, return -1 if not found */
  public static int indexOf(char[] aArray, char aChar, int aStartIndex) {
    for (int i=aStartIndex, n=aArray.length; i<n; i++) {
      if (aArray[i]==aChar) return i;
    }
    return -1;
  }

/**
* Search the contents of a character array for another character array.
* @param aArray The array to search.
* @param aArrayToFind The array to search for.
* @param aStartIndex The index of starting character in the source array.
* @return The index of the begining of the found target, -1 if not found.
*/
  public static int indexOf(char[] aArray, char[] aArrayToFind, int aStartIndex) {
    int sourceLen = aArray.length;
    if (aStartIndex >= sourceLen) return -1;

    int targetLen = aArrayToFind.length;
    int n= sourceLen-targetLen;
    int i, j;

    for(i=aStartIndex; i<=n ; i++) {
      int m= i+targetLen;
      for(j=i; j<m; j++) {
        if (aArray[j] != aArrayToFind[j-i]) break;
      } // j
      if (j == i+targetLen) return i;
    } // i

    return -1;
  } // indexOf

/**
* Search the contents of a character array for a string.
* @param aArray The array to search.
* @param aStringToFind The string to search for.
* @param aStartIndex The index of starting character in the source array.
* @return The index of the begining of the found target, -1 if not found.
*/
  public static int indexOf(char[] aArray, String aStringToFind, int aStartIndex) {
    return indexOf(aArray, aStringToFind.toCharArray(), aStartIndex);
  } // indexOf

/**
* Search the contents of a character array for the first element in a list of strings.
* @param aArray The array to search.
* @param aListToFind The list of strings to search for any of its items.
* @param aStartIndex The index of starting character in the source array.
* @param aListIndex Index in the list of the found element, -1 if not found. This is
*        an array of size 1, to enable modifying a method parameter.
* @return The index of the begining of the found target, -1 if not found.
*/
  public static int indexOf(final char[] aArray,
                            final ListOfString aListToFind,
                            final int aStartIndex,
                            final int[] aListIndex) {
    for (int i=0, n= aListToFind.size(); i<n; i++) {
      int si= indexOf(aArray, aListToFind.getItem(i), aStartIndex);
      if (si>=0) {
        aListIndex[0]= i;
        return si;
      }
    }
    aListIndex[0] = -1;
    return -1;
  } // indexOf

/**
 * Search aArray for aStringToFind
 * @param aArray The array to search.
 * @param aStringToFind The string to search for
 * @param aStartIndex int index of first element in the array to search.
 * @param aCaseSensetive should the search be case sensetive
 * @return index of the first found element in the array, -1 if not found.
 */
  public static int indexOf(final String[] aArray, final String aStringToFind,
                            final int aStartIndex, final boolean aCaseSensetive) {
    for (int i=aStartIndex, n=aArray.length; i<n; i++) {
      if (StringUtility.equals(aArray[i], aStringToFind, aCaseSensetive)) return i;
    }
    return -1;
  } // indexOf

/**
 * Search aArray for aStringToFind starting from the begining.
 * @param aArray The array to search.
 * @param aStringToFind The string to search for
 * @param aCaseSensetive should the search be case sensetive
 * @return index of the first found element in the array, -1 if not found.
 */
  public static int indexOf(final String[] aArray, final String aStringToFind,
                            final boolean aCaseSensetive) {
    return indexOf(aArray, aStringToFind, 0, aCaseSensetive);
  }

/** Check if the given array is null or zero length */
  public static boolean isEmpty(final String[] aArray) {
    if (aArray==null) return true;
    if (aArray.length==0) return true;
    return false;
  } // isEmpty

/** Check if the given array is null, zero length, or has null values */
  public static boolean isNull(final Object[] aArray) {
    if (aArray==null) return true;
    int n= aArray.length;
    if (n==0) return true;
    for (int i=0; i<n; i++) {
      if (aArray[i] != null) return false;
    }
    return true;
  }  // isNull()

/** Shallow clone the given array */
  public static Object clone(final Object aArray) {
    Class c= aArray.getClass();
    if (!c.isArray()) return null;
    int n= Array.getLength(aArray);
    Object r= Array.newInstance(c.getComponentType() , n);
    System.arraycopy(aArray, 0, r, 0, n);
    return r;
  } // clone

/** Clone the given string array */  
  public static String[] clone(final String[] aArray) {
    if (aArray==null) return null;
    int n= aArray.length;
    
    String[] result= new String[n];
    for (int i=0; i<n; i++) {
      result[i]= aArray[i];
    }
    return result;
  }
  
  
/**
 * Concat two arrays into a new array, the new array will have the content of aArray1
 * followed by the content of aArray2. The elements of aArray1 and aArray2 must be of
 * the same type.
 * @param aArray1 Object An array.
 * @param aArray2 Object An array.
 * @return Object An array that is aArray1 followed by aArray2.
 */
  public static Object concat(final Object aArray1, final Object aArray2) {
    int n1= Array.getLength(aArray1);
    int n2= Array.getLength(aArray2);
    if (n1==0 && n2==0) return null;
    if (n1==0) return clone(aArray2);
    if (n2==0) return clone(aArray1);

    Class c= aArray1.getClass().getComponentType();

    Object r= Array.newInstance(c, n1 + n2);
    System.arraycopy(aArray1, 0, r, 0, n1);
    System.arraycopy(aArray2, 0, r, n1, n2);
    return r;
  }  // concat

/** Concat two char arrays */
  public static char[] concatChar(final char[] aArray1, final char[] aArray2) {
    return (char[]) concat(aArray1, aArray2);
  } // concatChar

/** Concat two int arrays */
  public static int[] concatInt(final int[] aArray1, final int[] aArray2) {
    return (int[]) concat(aArray1, aArray2);
  } // concatInt

/** Concat two float arrays */
  public static float[] concatFloat(final float[] aArray1, final float[] aArray2) {
    return (float[]) concat(aArray1, aArray2);
  } // concatFloat

/** Concat two String arrays */
  public static String[] concatString(final String[] aArray1, final String[] aArray2) {
    return (String[]) concat(aArray1, aArray2);
  } // concatString

/** Convert the array into a string with items separated by aSeparator */
  public static String toString(final String[] aArray, final String aSeparator) {
    if (aArray==null) return null;
    int n= aArray.length;
    if (n==0) return StringUtility.EMPTY;

    StringBuffer b= new StringBuffer(n*aArray[0].length());
    n--;
    for (int i=0; i<n; i++) {
      b.append(aArray[i]).append(aSeparator);
    }
    b.append(aArray[n]);
    return b.toString();
  } // toString()

/** Get the length of the given array, return -1 if array is null */
  public static int length(final Object[] aArray) {
    if (aArray==null) return -1;
    return aArray.length;
  }

/**
* Search the contents of a byte array for another byte array.
* @param aArray The array to search.
* @param aArrayToFind The array to search for.
* @param aStartIndex The index of starting byte in the source array.
* @return The index of the begining of the found target, -1 if not found.
*/
  public static int indexOf(final byte[] aArray,
                            final byte[] aArrayToFind,
                            final int aStartIndex) {
    int sourceLen = aArray.length;
    if (aStartIndex >= sourceLen) return -1;

    int targetLen = aArrayToFind.length;
    int n= sourceLen-targetLen;
    int i, j;

    for(i=aStartIndex; i<=n ; i++) {
      int m= i+targetLen;
      for(j=i; j<m; j++) {
        if (aArray[j] != aArrayToFind[j-i]) break;
      } // j
      if (j == i+targetLen) return i;
    } // i

    return -1;
  } // indexOf

/**
 * Find the index of an array within another array. This can be used to search arrays
 * of any type; primitive or otherwise. It uses reflection to access elements of the
 * array, so be aware of the possible performance implications.
 * @param aArray Object Array to search, this can be an array of any type.
 * @param aArrayToFind Object Array to search for, this can be an array of any type.
 * @param aStartIndex int starting index in aArray
 * @return int The index of the begining of the found target, -1 if not found.
 */
  public static int indexOfArray(final Object aArray,
                                 final Object aArrayToFind,
                                 final int aStartIndex) {
    int sourceLen = Array.getLength(aArray);
    if (aStartIndex >= sourceLen) return -1;

    int targetLen = Array.getLength(aArrayToFind);
    int n= sourceLen-targetLen;
    int i, j;

    for(i=aStartIndex; i<=n ; i++) {
      int m= i+targetLen;
      for(j=i; j<m; j++) {
        Object src= Array.get(aArray, j);
        Object trg= Array.get(aArrayToFind, j-i);
        if (! ObjectUtils.equals(src, trg)) break;
      } // j
      if (j == i+targetLen) return i;
    } // i

    return -1;
  } // indexOf

/** Create an array of type similar to the elements to the given array */
  public static Object newArrayLike(final Object aArray, final int aLength) {
    Class type= aArray.getClass().getComponentType();
    return Array.newInstance(type, aLength);
  }

/** Create a zero-length array of the given element type */
  public static Object zeroLenArray(final Class aElementType) {
    return Array.newInstance(aElementType, 0);
  }

/** Create a zero-length array of type similar to the elements to the given array */
  public static Object zeroLenArrayLike(final Object aArray) {
    return newArrayLike(aArray, 0);
  }

  private static int adjustLength(final int aArrayLength,
                                  final int aStartIndex,
                                  final int aProposedLength) {
    int maxSize= aArrayLength - aStartIndex;
    if (aProposedLength > maxSize) return maxSize;
    return aProposedLength;
  }

/**
 * Extract a sub-array from the given source array.
 * @param aArray Object Source array to extract from. if null, null is returned.
 * @param aStart int start index to extract at, if aStart is less than zero, then it will
 *        start at zero. If aStart is greater than size of array, then an empty array is
 *        returned.
 * @param aSize int size of the returned array. If -ve an empty array is returned. If
 *        aSize specifies more data than is available, then only available data is
 *        returned.
 * @return Object array of a size of upto aSize. Elements of the returned array will have
 *        the same type as the elements of the given array.
 */
  public static Object subarray(final Object aArray,
                                int aStart,
                                int aSize) {
    if (aArray==null) return null;
    if (aSize<=0) return zeroLenArrayLike(aArray);

    if (aStart<0) aStart=0;

    int len= Array.getLength(aArray);
    if (aStart>=len) return zeroLenArrayLike(aArray);

    aSize= adjustLength(len, aStart, aSize);

    Object r= newArrayLike(aArray, aSize);
    System.arraycopy(aArray, aStart, r, 0, aSize);
    return r;
  }  // subarray

/**
 * Safe version of System.arraycopy() that adjust indexes to fit within the arrays
 * boundries.
 * @param src Object
 * @param srcPos int if &less; 0 then start at zero.
 * @param dest Object
 * @param destPos int
 * @param length int
 * @return int number of array items which were copied.
 */
  public static int arraycopy(Object src,
                              int srcPos,
                              Object dest,
                              int destPos,
                              int length) {
    if (src==null || dest==null || length<=0) return 0;
    int srcLen= Array.getLength(src);
    if (srcPos<0) srcPos=0;
    else if (srcPos >= srcLen) return 0;

    int destLen= Array.getLength(dest);
    if (destPos<0) destPos=0;
    else if (destPos>= destLen) return 0;

    length= adjustLength(srcLen, srcPos, length);
    length= adjustLength(destLen, destPos, length);

    System.arraycopy(src, srcPos, dest, destPos, length);
    return length;
  }

/**
 * Map a name to a value.
 * @param aNames String[] List of names.
 * @param aValues int[] List of corresponding values.
 * @param aName String Name whose value to find.
 * @param aDefault int Value to return if name not found.
 * @param aCaseSensetive boolean should the search be case sensetive
 * @return int The value of the given name, or aDefault.
 */
  public static int map(final String[] aNames,
                        final int[] aValues,
                        final String aName,
                        final int aDefault,
                        final boolean aCaseSensetive) {
    int i= indexOf(aNames, aName, aCaseSensetive);
    if (i < 0) return aDefault;
    if (i >= aValues.length) return aDefault;
    return aValues[i];
  }

/**
 * Convert an array of Objects to an array of Strings
 * @param array of Objects to convert.
 * @return an array of Strings
 */  
  public static String[] toArrayOfString(final Object[] array) {
    if (array==null) return null;
    int n= array.length;
    String[] r= new String[n];
    for (int i=0; i<n; i++) {
      Object obj= array[i];
      r[i]= obj==null? null : obj.toString();
    }
    return r;
  }
  
/** Convert a collection of Objects into an array of Strings. */  
  public static String[] toArrayOfString(final Collection collection) {
    if (collection==null) return null;
    int n= collection.size();
    String[] r= new String[n];
    int i=0;
    for (Iterator it= collection.iterator(); it.hasNext();) {
      Object obj= it.next();
      r[i++]= obj==null? null : obj.toString();
    }
    return r;
  }
  
} // ArrayUtilities
