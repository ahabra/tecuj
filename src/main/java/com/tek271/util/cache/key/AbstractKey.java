package com.tek271.util.cache.key;

import java.io.Serializable;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.Printf;
import com.tek271.util.string.StringUtility;


/**
 * Implements th IKey interface, key classes should extend this class and implement the
 * methods: getPart(int), setPart(int, String), compare(IKey, IKey).
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public abstract class AbstractKey implements IKey, Serializable {
  protected static final int pNOT_COMPARED= 3;

  protected String pSeparator= DEFAULT_SEPARATOR;
  protected int pPartCount=1;
  protected String pSinglePart= StringUtility.EMPTY;  // Single part key
  private int pHashCode;

/** A key must have a known part count */
  public AbstractKey(final int aPartCount) {
    pPartCount= aPartCount;
  }

/** Check if aKey equals this object */
  public boolean equals(final Object aKey) {
    return compare(this, aKey)==0;
  }

  /** Check if aKey equals this object */
  public boolean equals(final IKey aKey) {
    return compare(this, aKey)==0;
  }

/**
 * Compare this object with the given aKey.
 * @param aKey Object to compare against.
 * @return int -ve if this object &lt; aKey, +ve if this object > aKey,
 *   0 if this object = aKey.
 */
  public int compareTo(final Object aKey) {
    return compare(this, aKey);
  }

/**
 * Compare this object with the given aKey.
 * @param aKey IKey to compare against.
 * @return int -ve if this object &lt; aKey, +ve if this object > aKey,
 *   0 if this object = aKey.
 */
  public int compareTo(final IKey aKey) {
    return compare(this, aKey);
  }

/**
 * Check if aKey1 or aKey2 are null.
 * @param aKey1 Object
 * @param aKey2 Object
 * @return int -1 if aKey1 &lt; aKey2, +1 if aKey1 > aKey2, 0 if aKey1 = aKey2,
 *    pNOT_COMPARED otherwise.
 */
  protected int compareForNull(final Object aKey1, final Object aKey2) {
    if (aKey1==null && aKey2==null) return 0;
    if (aKey1==null) return -1;
    if (aKey2==null) return 1;
    return pNOT_COMPARED;
  }

/**
 * Check the part count for two keys
 * @param aKey1 IKey
 * @param aKey2 IKey
 * @return int -1 if aKey1.partCount &lt; aKey2.partCount,
 *             +1 if aKey1.partCount > aKey2.partCount,
 *             pNOT_COMPARED  if aKey1.partCount = aKey2.partCount
 */
  protected int compareForSize(final IKey aKey1, final IKey aKey2) {
    int n1= aKey1.getPartCount();
    int n2= aKey2.getPartCount();
    if (n1 < n2) return -1;
    if (n1 > n2) return 1;
    return pNOT_COMPARED;
  }  // compareForSize


/**
 * Compare two key objects: aKey1 with aKey2. This method calls the abstract method
 * compare(IKey, IKey).
 * @param aKey1 Object
 * @param aKey2 Object
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2. Note that
 * null is considered less than any other value.
 */
  public int compare(final Object aKey1, final Object aKey2) {
    int r= compareForNull(aKey1, aKey2);
    if (r != pNOT_COMPARED)  return r;

    IKey k1= (IKey) aKey1;
    IKey k2= (IKey) aKey2;
    r= compareForSize(k1, k2);
    if (r != pNOT_COMPARED)  return r;

    return compare(k1, k2);
  } // compare

  public String toString() {
    return getString();
  }

/**
 * Set the separator between parts when converting the key to/from a string. The
 * default is the | char.
 */
  public void setSeparator(final String aSeparator) {
    pSeparator= aSeparator;
  }

/**
 * Get the separator between parts when converting the key to/from a string. The
 * default is the | char.
 */
  public String getSeparator() {
    return pSeparator;
  }

  private static final String pERR_BAD_KEY =
      "Key string has ? parts, while it should be ?. The bad key string is: ?";

/**
 * Set the value of the key from a string, if the key has multiple parts, the parts
 * will be separated by the value of the separator peoperty. This method calls the
 * abstract method setPart(int, String).
 * @param aStringKey String The key as a string.
 * @throws IllegalArgumentException if the given aStringKey has more parts than
 * this key object.
 */
  public void setString(final String aStringKey) {
    pHashCode=0;
    if (pPartCount==1) {
      pSinglePart= aStringKey;
      return;
    }

    ListOfString list= new ListOfString(aStringKey, pSeparator);
    int n= list.size();
    if (n>pPartCount) {
      String msg=Printf.p(pERR_BAD_KEY, String.valueOf(n),
                          String.valueOf(pPartCount), aStringKey);
      throw new IllegalArgumentException(msg);
    }
    for (int i=0; i<n; i++) {
      setPart(i, list.getItem(i));
    }
  }  // setString

/**
 * Get the value of the key as a string, if the key has multiple parts, the parts
 * will be separated by the value of the separator peoperty. This method calls the
 * abstract method getPart(int).
 * @return String The key as a string.
 */
  public String getString() {
    if (pPartCount==1) return pSinglePart;

    StringBuffer b= new StringBuffer(32);
    int n= pPartCount-1;
    for (int i=0; i<n; i++) {
      b.append(getPart(i)).append(pSeparator);
    }
    b.append(getPart(n));
    return b.toString();
  }

/** Number of parts in the key */
  public int getPartCount() {
    return pPartCount;
  }

  private final static String pERR_BAD_INDEX =
    "Key parts can be in the range of 0 to ?. Index passed=?.";

/**
 * Check if the given part index is in range.
 * @throws IndexOutOfBoundsException if the index is &lt; 0 or >= partCount
 */
  protected void checkIndex(final int aPartIndex) {
    if (aPartIndex >= 0 || aPartIndex < pPartCount) return;

    String msg= Printf.p(pERR_BAD_INDEX, pPartCount-1, aPartIndex);
    throw new IndexOutOfBoundsException(msg);
  }

/** Calculate the hashcode of this object */
  private void calcHashCode() {
    if (pPartCount==1) {
      pHashCode= pSinglePart.hashCode();
      return;
    }
    int r=0;
    for (int i=0; i<pPartCount; i++) {
      r= r ^ getPart(i).hashCode();
    }
    pHashCode= r;
  }  // calcHashCode();

/** Hash code of this object */
  public int hashCode() {
    if (pHashCode == 0) calcHashCode();
    return pHashCode;
  }  // hashCode()

/** This method should be called from the subclasses setPart(int, String) methods */
  protected void setPart(final int aIndex) {
    pHashCode=0;
    checkIndex(aIndex);
  }


}  // AbstractKey
