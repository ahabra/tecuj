package com.tek271.util.cache.key;

import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.string.StringUtility;

/**
 * A key that consists of N parts, where N is determined when you create an instance of
 * KeyN.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public class KeyN extends AbstractKey {
  private String[] pKey;

/** Create a KeyN whose parts are given in the array aKey */
  public KeyN(final String[] aKey) {
    super(aKey.length);
    pKey= ArrayUtilities.clone(aKey);
  }

/** Create a KeyN whose parts are given in the ListOfString aKey */
  public KeyN(final ListOfString aKey) {
    super(aKey.size());
    pKey= aKey.getArray();
  }

/**
 * Create a KeyN whose parts are given in the String aKey and whose parts are
 * separated by the given aPartSeparator.
 * @param aKey String Multipart key separated by aPartSeparator.
 * @param aPartSeparator String
 */
  public KeyN(final String aKey, final String aPartSeparator) {
    super(0);
    pKey= StringUtility.split(aKey, aPartSeparator);
    pPartCount= pKey.length;
  }

/**
 * Create a KeyN whose parts are given in the String aKey and whose parts are
 * separated by the IKey.DEFAULT_SEPARATOR |
 * @param aKey String Multipart key separated by DEFAULT_SEPARATOR.
 */
  public KeyN(final String aKey) {
    this(aKey, IKey.DEFAULT_SEPARATOR);
  }

/**
 * Get the part at the given index.
 * @param aIndex int The part's index.
 * @return String The key part at the given index
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public String getPart(final int aIndex) {
    checkIndex(aIndex);
    return pKey[aIndex];
  }

/**
 * Set the part at the given index.
 * @param aIndex int The part's index.
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public void setPart(final int aIndex, final String aKeyPart) {
    super.setPart(aIndex);
    pKey[aIndex]= aKeyPart;
  }

/**
 * Compare two IKey objects: aKey1 with aKey2.
 * @param aKey1 IKey
 * @param aKey2 IKey
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2.
 * Note that null is considered less than any other value.
 */
  public int compare(final IKey aKey1, final IKey aKey2) {
    KeyN k1= (KeyN) aKey1;
    KeyN k2= (KeyN) aKey2;

    return compare(k1, k2);
  }

/**
 * Compare two KeyN objects: aKey1 with aKey2.
 * @param aKey1 KeyN
 * @param aKey2 KeyN
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2.
 * Note that null is considered less than any other value. Also if two keys have
 * different part count then the one with the lower part count is considered less than
 * the other.
 */
  public int compare(final KeyN aKey1, final KeyN aKey2) {
    int r= compareForNull(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    r= compareForSize(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    // same size, compare contents
    for (int i=0, n=aKey1.getPartCount(); i<n; i++) {
      r= StringUtility.compare(aKey1.pKey[i], aKey2.pKey[i]);
      if (r!=0) return r;
    }
    return 0;
  }  // compare

} // KeyN
