/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2005  Abdul Habra
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

package com.tek271.util.thread;

/**
 * <p>Create an in-memory mutual-exclusion-lock</p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class Mutex extends AbstractMutex {
  private boolean pLock;

/** Create a mutual-exclusion-lock with a lowered initial value  */
  public Mutex() {
    pLock= false;
  }

/**
 * Create a mutual-exclusion-lock
 * @param aInitialValue boolean raised or lowered.
 */
  public Mutex(final boolean aInitialValue) {
    pLock= aInitialValue;
  }

/**
 * getValue of the mutual-exclusion-lock
 * @return boolean true means raised, false means lowered.
 */
  public synchronized boolean getValue() {
    return pLock;
  }

/**
 * set the Value of the mutual-exclusion-lock
 * @param aIsRaised boolean true means raised, false means lowered.
 * @return boolean always true.
 */
  public synchronized boolean setValue(boolean aIsRaised) {
    pLock= aIsRaised;
    return true;
  }
}  // Mutex
