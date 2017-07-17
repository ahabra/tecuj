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
 * <p>Define the interface of a mutual-exclusion-lock </p>
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public interface IMutex {

/**
 * Raise the mutual-exclusion-lock
 * @return true if raise is successful
 */
  boolean raise();

/**
 * Lower the mutual-exclusion-lock
 * @return true if lowering is successful
 */
  boolean lower();

/** @return boolean true if mutual-exclusion-lock is raised. */
  boolean isRaised();

/** @return boolean true if mutual-exclusion-lock is lowered. */
  boolean isLowered();

/** @return true if raised, false if not  */
  boolean getValue();

/**
 * Set value of mutual-exclusion-lock
 * @param aIsRaised boolean true if mutual-exclusion-lock is to be raised, false if to be lowered.
 * @return boolean true if operation is successful
 */
  boolean setValue(boolean aIsRaised);

}  // IMutex
