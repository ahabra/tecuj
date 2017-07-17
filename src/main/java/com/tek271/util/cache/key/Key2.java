package com.tek271.util.cache.key;

import com.tek271.util.string.StringUtility;

/**
 * A key that consists of two parts.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public class Key2 extends AbstractKey {
  private String pKeyPart0;
  private String pKeyPart1;

/** Create a key that consists of two parts */
  public Key2(final String aKeyPart0, final String aKeyPart1) {
    super(2);
    pKeyPart0= aKeyPart0;
    pKeyPart1= aKeyPart1;
  }

/**
 * Get the part at the given index.
 * @param aIndex int The only valid values are 0 or 1.
 * @return String The key part at the given index
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public String getPart(final int aIndex) {
    checkIndex(aIndex);
    if (aIndex==0) return pKeyPart0;
    return pKeyPart1;
  }

/**
 * Set the part at the given index.
 * @param aIndex int The only valid values are 0 or 1.
 * @param aKeyPart the value of the part.
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public void setPart(final int aIndex, final String aKeyPart) {
    super.setPart(aIndex);
    if (aIndex==0) pKeyPart0= aKeyPart;
    else pKeyPart1= aKeyPart;
  }

/**
 * Compare two IKey objects: aKey1 with aKey2.
 * @param aKey1 IKey
 * @param aKey2 IKey
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2.
 * Note that null is considered less than any other value.
 */
  public int compare(final IKey aKey1, final IKey aKey2) {
    Key2 k1= (Key2) aKey1;
    Key2 k2= (Key2) aKey2;
    return compare(k1, k2);
  }

  /**
   * Compare two Key2 objects: aKey1 with aKey2.
   * @param aKey1 Key2
   * @param aKey2 Key2
   * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2.
   * Note that null is considered less than any other value.
   */
  public int compare(final Key2 aKey1, final Key2 aKey2) {
    int r= compareForNull(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    r= compareForSize(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    int c=StringUtility.compare(aKey1.pKeyPart0, aKey2.pKeyPart0);
    if (c!=0) return c;

    return StringUtility.compare(aKey1.pKeyPart1, aKey2.pKeyPart1);
  }  // compare


}  // Key2
