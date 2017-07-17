package com.tek271.util.cache.key;

import java.util.Comparator;

/**
 * Encapsulate a key to a cache store. The key can consist of 1 or more string parts.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 0.1
 */
public interface IKey extends Comparable, Comparator {
/** | is the default separator between key parts when converted to string */
  public static final String DEFAULT_SEPARATOR= "|";

/** Check if this IKey object equals the given key */
  public boolean equals(final Object aKey);

/** Check if this IKey object equals the given key */
  public boolean equals(final IKey aKey);

/**
 * Compare this object with the given aKey.
 * @param aKey Object to compare against.
 * @return int -ve if this object &lt; aKey, +ve if this object > aKey,
 *   0 if this object = aKey.
 */
  public int compareTo(final Object aKey);

/**
 * Compare this object with the given aKey.
 * @param aKey Object to compare against.
 * @return int -ve if this object &lt; aKey, +ve if this object > aKey,
 *   0 if this object = aKey.
 */
  public int compareTo(final IKey aKey);

/**
 * Compare two key objects: aKey1 with aKey2.
 * @param aKey1 Object
 * @param aKey2 Object
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2. Note that
 * null is considered less than any other value.
 */
  public int compare(final Object aKey1, final Object aKey2);

/**
 * Compare two key objects: aKey1 with aKey2.
 * @param aKey1 IKey
 * @param aKey2 IKey
 * @return int -ve if aKey1 &lt; aKey2, +ve if aKey1 > aKey2,  0 if aKey1 = aKey2. Note that
 * null is considered less than any other value.
 */
  public int compare(final IKey aKey1, final IKey aKey2);

/** Get a string representing this key */
  public String toString();  // calls getString()

/** Hashcode of this key */
  public int hashCode();

/** Set the separator for multi-part keys */
  public void setSeparator(final String aSeparator);

/** Get the separator for multi-part keys */
  public String getSeparator();

/** Set the key value from a String that can be multi-part */
  public void setString(final String aStringKey);
/** Get the key as a string that can be multi-part */
  public String getString();

/** How many parts are in the key */
  public int getPartCount();

/** Get the key part at the given index */
  public String getPart(final int aIndex);

/** Set the key part at the given index */
  public void setPart(final int aIndex, final String aKeyPart);

}  // IKey
