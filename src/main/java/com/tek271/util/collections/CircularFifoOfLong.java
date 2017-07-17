/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2006  Abdul Habra
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
package com.tek271.util.collections;

import java.util.*;
import com.tek271.util.collections.iterator.ILongIterator;

/**
 * Code adapted from org.apache.commons.collections.buffer.CircularFifoBuffer.
 *
 * CircularFifoBuffer is a first in first out buffer with a fixed size that
 * replaces its oldest element if full.
 * <p>
 * The removal order of a <code>CircularFifoBuffer</code> is based on the
 * insertion order; elements are removed in the same order in which they
 * were added.  The iteration order is the same as the removal order.
 * <p>
 * The add(Object), remove() and get() operations
 * all perform in constant time.  All other operations perform in linear
 * time or worse.
 * <p>
 * Note that this implementation is not synchronized.  The following can be
 * used to provide synchronized access to your <code>CircularFifoBuffer</code>:
 * <pre>
 *   Buffer fifo = BufferUtils.synchronizedBuffer(new CircularFifoBuffer());
 * </pre>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/06/03 22:02:13 $
 *
 * @author Stefano Fornari
 * @author Stephen Colebourne
 * @author Abdul Habra Adapted to long type
 */
public class CircularFifoOfLong {
  private final int pMAX_ELEMENTS;

  private long[] pElements;
  private int pStart=0;
  private int pEnd=0;
  private boolean pIsFull=false;

  /**
   * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold 32 elements.
   */
  public CircularFifoOfLong() {
    this(32);
  }

  /**
   * Constructs a new <code>BoundedFifoBuffer</code> big enough to hold
   * the specified number of elements.
   * @param aSize  the maximum number of elements for this fifo
   * @throws IllegalArgumentException  if the size is less than 1
   */
  public CircularFifoOfLong(int aSize) {
    if (aSize<=0) throw new IllegalArgumentException("The size must be greater than 0");
    pElements=new long[aSize];
    pMAX_ELEMENTS=pElements.length;
    Arrays.fill(pElements, Long.MIN_VALUE);
  }

  /**
   * Returns the number of elements stored in the buffer.
   * @return this buffer's size
   */
  public int size() {
    if (pEnd<pStart) return pMAX_ELEMENTS-pStart+pEnd;
    if (pEnd==pStart) return pIsFull ? pMAX_ELEMENTS : 0;
    return pEnd-pStart;
  }

  /**
   * Returns true if this buffer is empty; false otherwise.
   * @return true if this buffer is empty
   */
  public boolean isEmpty() {
    return size()==0;
  }

  /**
   * Returns true if this collection is full and no new elements can be added.
   * @return <code>true</code> if the collection is full
   */
  public boolean isFull() {
    return size()==pMAX_ELEMENTS;
  }

  /**
   * Gets the maximum size of the collection (the bound).
   * @return the maximum number of elements the collection can hold
   */
  public int maxSize() {
    return pMAX_ELEMENTS;
  }

  /**  Clears this buffer. */
  public void clear() {
    pIsFull=false;
    pStart=0;
    pEnd=0;
    Arrays.fill(pElements, Long.MIN_VALUE);
  }

  /**
   * Adds the given element to this buffer.
   * @param aElement  the element to add
   * @return true, always
   * @throws NullPointerException  if the given element is null
   */
  public boolean add(long aElement) {
    if (pIsFull) remove();

    pElements[pEnd++]=aElement;
    if (pEnd>=pMAX_ELEMENTS) pEnd=0;
    if (pEnd==pStart) pIsFull=true;
    return true;
  }

  /**
   * Returns the least recently inserted element in this buffer.
   * @return the least recently inserted element
   */
  public long get() {
    if (isEmpty()) { // ?? BufferUnderflowException
      throw new RuntimeException("The buffer is already empty");
    }

    return pElements[pStart];
  }

  /**
   * Removes the least recently inserted element from this buffer.
   * @return the least recently inserted element
   */
  public long remove() {
    if (isEmpty()) { // BufferUnderflowException
      throw new RuntimeException("The buffer is already empty");
    }

    long element=pElements[pStart];

    if (element != Long.MIN_VALUE) {
      pElements[pStart++]=Long.MIN_VALUE;

      if (pStart>=pMAX_ELEMENTS) pStart=0;
      pIsFull=false;
    }
    return element;
  }

  /**
   * Increments the internal index.
   * @param aIndex  the index to increment
   * @return the updated index
   */
  private int increment(int aIndex) {
    aIndex++;
    if (aIndex>=pMAX_ELEMENTS)    aIndex=0;
    return aIndex;
  }

  /**
   * Decrements the internal index.
   * @param aIndex  the index to decrement
   * @return the updated index
   */
  private int decrement(int aIndex) {
    aIndex--;
    if (aIndex<0)    aIndex=pMAX_ELEMENTS-1;
    return aIndex;
  }

  /** Returns an iterator over this buffer's elements.  */
  public ILongIterator iterator() {
    return new ILongIterator() {

      private int index=pStart;
      private int lastReturnedIndex=-1;
      private boolean isFirst=pIsFull;

      public boolean hasNext() {
        return isFirst|| (index!=pEnd);
      }

      public long nextLong() {
        if (!hasNext())    throw new NoSuchElementException();

        isFirst=false;
        lastReturnedIndex=index;
        index=increment(index);
        return pElements[lastReturnedIndex];
      }

      public Object next() {
        return new Long(nextLong());
      }

      public void remove() {
        if (lastReturnedIndex==-1)   throw new IllegalStateException();

        // First element can be removed quickly
        if (lastReturnedIndex==pStart) {
          CircularFifoOfLong.this.remove();
          lastReturnedIndex=-1;
          return;
        }

        // Other elements require us to shift the subsequent elements
        int i=lastReturnedIndex+1;
        while (i!=pEnd) {
          if (i>=pMAX_ELEMENTS) {
            pElements[i-1]=pElements[0];
            i=0;
          } else {
            pElements[i-1]=pElements[i];
            i++;
          }
        }

        lastReturnedIndex=-1;
        pEnd=decrement(pEnd);
        pElements[pEnd]=Long.MIN_VALUE;
        pIsFull=false;
        index=decrement(index);
      }  // remove

    };
  }  // iterator

  public long[] toArray() {
    long[] r= new long[size()];
    int i=0;
    for (ILongIterator it= iterator(); it.hasNext(); ) {
      r[i++]= it.nextLong();
    }
    return r;
  }

} // CircularFifoOfLong
