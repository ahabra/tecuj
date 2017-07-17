package com.tek271.util.cache.key;

/**
 * A key that consists of one part.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public class Key1 extends AbstractKey {

/** Create a key that consists of one part */
  public Key1(final String aKey) {
    super(1);
    pSinglePart= aKey;
  }

/**
 * Get the part at the given index.
 * @param aIndex int The only valid value is 0.
 * @return String The key part at the given index
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public String getPart(final int aIndex) {
    checkIndex(aIndex);
    return pSinglePart;
  }

/**
 * Set the part at the given index.
 * @param aIndex int The only valid value is 0.
 * @param aKeyPart the value of the part.
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  public void setPart(final int aIndex, final String aKeyPart) {
    super.setPart(aIndex);
    pSinglePart= aKeyPart;
  }

/**
 * Compare two IKey objects: aKey1 with aKey2.
 * @param aKey1 IKey
 * @param aKey2 IKey
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2.
 * Note that null is considered less than any other value.
 */
  public int compare(final IKey aKey1, final IKey aKey2) {
    int r= compareForNull(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    r= compareForSize(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    return pSinglePart.compareTo(aKey2.getPart(0));
  }  // compare

}  // Key1
