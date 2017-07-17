/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2005  Abdul Habra
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

package com.tek271.util.log;

import java.util.*;
import com.tek271.util.*;
import com.tek271.util.string.*;
import com.tek271.util.thread.*;

/**
 * Buffer logging messages.
 * Remember to call <b>flush</b> when you are done with objects of this class.
 * Implementing classes must provide logNow() implementation.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public abstract class AbstractBufferedLogger implements ILogger {
  private final static int pDEFAULT_WAIT_PERIOD= 60 * 5;  // in seconds
  private final static int pDEFAULT_BUFFER_SIZE= 8;

  private int pBufferedLogLevel;  // what to buffer
  private boolean pIsLogAllStackTraces;
  private boolean pIsListThreadName;
  private LogBuffer pLogBuffer;
  private int pWaitPeriod;
  private String pWaitPeriodAsString;

/**
 * Create a buffered logger.
 * @param aWaitPeriod Seconds to wait before a buffered log entry is eligible to be
 * logged on next logging request.
 * @param aBufferSize int # of distinct log messages to be buffered.
 * @param aBufferedLogLevel int Error levels as defined in ILogger.
 * @param aIsLogAllStackTraces boolean If true, all stacktraces will be logged for
 * each buffered error. If false, only the first stack trace for an error will be
 * logged.
 * @param aIsListThreadName boolean If true, the thread name is listed before the
 * log message inside [square brakets].
 */
  public AbstractBufferedLogger(final int aWaitPeriod,
                                final int aBufferSize,
                                final int aBufferedLogLevel,
                                final boolean aIsLogAllStackTraces,
                                final boolean aIsListThreadName) {
    pWaitPeriod= aWaitPeriod;
    pBufferedLogLevel= aBufferedLogLevel;
    pIsLogAllStackTraces= aIsLogAllStackTraces;
    pIsListThreadName= aIsListThreadName;
    pLogBuffer= new LogBuffer(aBufferSize);

    pWaitPeriodAsString= String.valueOf(aWaitPeriod);
  }  // AbstractBufferedLogger

/**
 * Create a buffered logger. With: <ol>
 * <li>Buffered Log Level= ILogger.ERROR
 * <li>IsLogAllStackTraces= true
 * <li>Wait period= 60 * 5 seconds
 * <li>Buffer size= 8
 * </ol>
 */
  public AbstractBufferedLogger() {
    this(pDEFAULT_WAIT_PERIOD, pDEFAULT_BUFFER_SIZE, ILogger.ERROR, true, true);
  }

/**
 * Classes that extend this class must implement this method, where they log the
 * messages that were buffered by AbstractBufferedLogger.
 * @param aLevel int Log level as defined in ILogger
 * @param aMsg String The logging message
 * @param aThrowable Throwable The error to be logged.
 */
  protected abstract void logNow(final int aLevel, final String aMsg, final Throwable aThrowable);

/**
 * Log the given message and the given list of Throwable objects. Will call the
 * abstract logNow() method.
 * @param aLevel int Log level.
 * @param aMsg String Log message.
 * @param aThrowableList List of Throwable objects. Can be null.
 */
  private void logList(final int aLevel, final String aMsg, final List aThrowableList) {
    if (aThrowableList==null || aThrowableList.size()==0) {
      logNow(aLevel, aMsg, null);
      return;
    }
    if (aThrowableList.size()==1) {
      logNow(aLevel, aMsg, (Throwable) aThrowableList.get(0) );
      return;
    }

    MultiThrowable mt= new MultiThrowable(aMsg, aThrowableList);
    logNow(aLevel, aMsg, mt);
  }  // logList

/** Log the given msg. Implements the ILogger interface. */
  public synchronized void log(final int aLevel, String aMsg, final Throwable aThrowable) {
    if (aLevel<pBufferedLogLevel) {
      if (pIsListThreadName)
        aMsg= "[thread="+ThreadUtility.getCurrentThreadName()+"] " + aMsg;

      logNow(aLevel, aMsg, aThrowable);
      return;
    }
    pLogBuffer.add(aLevel, aMsg, aThrowable);
  }  // log

/** Log the given msg. Implements the ILogger interface. */
  public synchronized void log(final int aLevel, final String aMsg) {
    log(aLevel, aMsg, null);
  }  // log

/** You must call <b>flush()</b> when you are done with this logger object */
  public synchronized void flush() {
    pLogBuffer.flush();
  }

/** A safty net, just in case user forgets to call flush() */
  protected void finalize() {
    flush();
  }


  private static final String pMSG_FIRST = "[First time in ? seconds?] ";
  private static final String pMSG_COUNT = "[? times in the last ? seconds?] ";

/** A data structure that store a log message and its stack trace(s) */
  private class BufferedLogEntry {
    private int pLevel;
    private String pLogMessage;
    private List pThrowableList= new ArrayList();  // Throwable objects list
    private long pLastAddTime;  // when the last message was added
    private long pCreateTime;   // when this object was created
    private int pCount;  // # of times this message was logged.
    private String pThreadName;

    BufferedLogEntry(final int aLevel, final String aMsg, final Throwable aThrowable) {
      pLevel= aLevel;
      pLogMessage= aMsg;
      pCount=1;
      if (aThrowable != null)  pThrowableList.add(aThrowable);
      pCreateTime= System.currentTimeMillis();
      pLastAddTime= pCreateTime;
      pThreadName= ThreadUtility.getCurrentThreadName();
      // 2005.03.21: log it first time it occures, following occurances will be buffered.
      logNow(aLevel, buildMsg(), aThrowable);
    }

/** Increment counter and add aThrowable to the list */
    void add(final Throwable aThrowable) {
      pLastAddTime= System.currentTimeMillis();
      pCount++;
      if (pIsLogAllStackTraces && aThrowable != null)  pThrowableList.add(aThrowable);
    }

    private String buildMsg() {
      String thr= pIsListThreadName? ", thread="+pThreadName : StringUtility.EMPTY;
      String msg;
      if (pCount==1) {
        msg= Printf.p(pMSG_FIRST, pWaitPeriodAsString, thr);
      } else {
        float d= (System.currentTimeMillis() - pCreateTime) / 1000.0f;
        String ds= String.valueOf(d);
        msg=Printf.p(pMSG_COUNT, String.valueOf(pCount), ds, thr);
      }
      return msg + pLogMessage;
    }

/** log this message */
    void log() {
      // 2005.03.21: If item occured only once then do not log it again.
      if (pCount==1) return;

      logList(pLevel, buildMsg(), pThrowableList);
    }

/** check if the given level, msg, and current thread name are equal to this message */
    public boolean equals(final int aLevel, final String aLogMessage) {
      if (pLevel != aLevel) return false;
      if (!StringUtility.equals(pLogMessage, aLogMessage)) return false;
      return StringUtility.equals(pThreadName, ThreadUtility.getCurrentThreadName() );
    }

  }  // BufferedLogEntry class


  private class LogBuffer {
    private BufferedLogEntry[] pBuffer;
    private int pSize;
    private int pBufferSize;


    public LogBuffer(final int aBufferSize) {
      pBufferSize= aBufferSize;
      pBuffer= new BufferedLogEntry[aBufferSize];
    }

/** index of entry not used the earliest */
    private int indexOfOldestUnused() {
      if (pSize==0) return -1;
      int r=0;
      for (int i=1; i<pSize; i++) {
        if (pBuffer[i].pLastAddTime < pBuffer[r].pLastAddTime) r=i;
      }
      return r;
    } // indexOfOldestUnused

/** find index of entry with the given level, msg and current thread name */
    private int indexOfLog(final int aLevel, final String aMsg) {
      for (int i=0; i<pSize; i++) {
        if (pBuffer[i].equals(aLevel, aMsg)) return i;
      }
      return -1;
    }  // indexOfLog

/** Delete item at the given index, shift following items back */
    private void delete(final int aIndex) {
      // check valid index
      if (aIndex<0 || aIndex>=pSize) return;

      for (int i=aIndex; i<pSize-1; i++) {
        pBuffer[i]= pBuffer[i+1];
      }
      pSize--;
      pBuffer[pSize]= null;
    }  // delete

    private void logAndDelete(final int aIndex) {
      pBuffer[aIndex].log();
      delete(aIndex);
    }

/** Clear logs that have been there for more than the holding period */
    private void clearTimedOut() {
      long now= System.currentTimeMillis();
      for (int i=pSize-1; i>=0; i--) {
        long d= (now - pBuffer[i].pCreateTime) / 1000;  // time diffesrence in seconds
        if (d >= pWaitPeriod) logAndDelete(i);
      }
    }  // clearTimedOut

/** Add a log entry to the buffer */
    public void add(final int aLevel, final String aMsg, final Throwable aThrowable) {
      clearTimedOut();   // remove logs that have been there for >= hold period
      // Check if a log with same level and msg exist
      int i= indexOfLog(aLevel, aMsg);
      if (i>=0) {
        pBuffer[i].add(aThrowable);
        return;
      }
      // now, log does not exist in buffer
      BufferedLogEntry be= new BufferedLogEntry(aLevel, aMsg, aThrowable);
      if (pSize<pBufferSize) { // buffer is not full
        pBuffer[pSize++]= be;
        return;
      }
      // full buffer
      i= indexOfOldestUnused();
      logAndDelete(i);
      pBuffer[pSize++]= be;
    }  // add

/** Flush all messages in the buffer */
    public void flush() {
      for (int i=0; i<pSize; i++) {
        pBuffer[i].log();
        pBuffer[i]= null;
      }
      pSize=0;
    }  // flush

  } // LogBuffer class


} // AbstractBufferedLogger
