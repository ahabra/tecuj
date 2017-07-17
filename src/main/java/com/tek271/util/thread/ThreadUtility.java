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

package com.tek271.util.thread;

import com.tek271.util.*;

/**
 * Several static thread related methods including <code>sleep(seconds)</code>.
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 * <p><b>History</b><ol>
 * <li>2005.10.28: Added getCurrentThreadName() </li>
 * </ol></p>
 *
 */
public class ThreadUtility {
  private ThreadUtility() {}

  public static Thread run(final IExecutable aExecutable, final String aThreadName) {
    class T implements Runnable {
      public void run() {
        aExecutable.execute();
      }
    }  // class T

    Thread t= new Thread(new T(), aThreadName);
    t.start();
    return t;
  }  // run

  public static Thread run(final IExecutable aExecutable) {
    class T implements Runnable {
      public void run() {
        aExecutable.execute();
      }
    }  // class T

    Thread t= new Thread(new T());
    t.start();
    return t;
  }  // run

/**
 * Wait for the given period of milli seconds, ignore exceptions
 * @param aWaitPeriod long wait period in milli seconds
 * @return boolean true if intrrupted and thread is to terminate.
 */
  public static boolean sleepMillis(long aWaitPeriod) {
    if (Thread.interrupted()) return true;
    try {
      Thread.sleep(aWaitPeriod);
      return false;
    } catch (InterruptedException e) {
      return true;
    }
  }  // sleepMillis


/**
 * Wait for the given period of seconds, ignore exceptions
 * @param aWaitPeriod long wait period in seconds
 * @return boolean true if intrrupted and thread is to terminate.
 */
  public static boolean sleep(long aWaitPeriod) {
    return sleepMillis(aWaitPeriod*1000);
  }  // sleep

/** Get the name of the current thread */
  public static String getCurrentThreadName() {
    return Thread.currentThread().getName();
  }

}  // ThreadUtility
