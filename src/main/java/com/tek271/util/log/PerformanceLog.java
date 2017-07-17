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

import java.text.*;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.collections.MapIterator;

import com.tek271.util.string.*;
import com.tek271.util.time.DateFormatUt;
import com.tek271.util.io.FileIO;
import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.collections.CircularFifoOfLong;

/**
 * Log timing for different events in the JVM. The caller logs the
 * time of an event by name.
 * Accessing this class is throu the singlton PerformanceLog.LOG. The method that will
 * be called the most is PerformanceLog.LOG.putNow(key, startTime)
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class PerformanceLog {
  /** max number of entries in the log */
  public static final int MAX_SIZE= 512;

  public static final int MAX_ENTRY_BUFFER_SIZE= 10;

/**
 * Create a performance logger with a max size of MAX_SIZE.
 * This is the only way to get an object of PerformanceLog. This object
 * is static and is shared in the JVM.
 */
  public static final PerformanceLog LOG= new PerformanceLog(MAX_SIZE);

  private static final String pNL= StringUtility.NEW_LINE;
  private static final DecimalFormat pFORMATTER= new DecimalFormat("###,###,###");
  private final static String pDATE_HTML = "yyyy.MM.dd:'<br>'HH.mm.ss:SSS";

  private LRUMap pMap;

/** Store info about a single operation logging times */
  public static class LogEntry {
    private long pCreateTime;  // when this was created
    private long pModifyTime;  // last time we updated this entry
    private long pTotalDelay;  // total delay of all calls on this operation
    private int pCounter;      // # of times this operations was invoked
    private long pMinDelay;    // minimum delay on this operation
    private long pMaxDelay;    // maximum delay on this operation
    private CircularFifoOfLong pQueue;  // queue of last 10 delays of this event

    private LogEntry(final long aDelay) {
      pCreateTime= System.currentTimeMillis();
      pModifyTime= pCreateTime;
      pTotalDelay= aDelay;
      pCounter=1;
      pMinDelay= aDelay;
      pMaxDelay= aDelay;
      pQueue= new CircularFifoOfLong(MAX_ENTRY_BUFFER_SIZE);
      pQueue.add(aDelay);
    }

    private void updateDelay(final long aDelay) {
      pModifyTime= System.currentTimeMillis();
      pTotalDelay+= aDelay;
      pCounter++;
      if (aDelay<pMinDelay) pMinDelay= aDelay;
      if (aDelay>pMaxDelay) pMaxDelay= aDelay;
      pQueue.add(aDelay);
    }

/** # of times this operations was invoked */
    public int getCounter() { return pCounter; }

/** when this LogEntry was created */
    public String getCreateTime() {  return DateFormatUt.formatDate(pCreateTime);  }

/** last time we updated this entry */
    public String getModifyTime() {  return DateFormatUt.formatDate(pModifyTime);  }

/** when this LogEntry was created, formatted for html */
    public String getCreateTimeHtml() { return DateFormatUt.formatDate(pCreateTime, pDATE_HTML); }

/** last time we updated this entry, formatted for html */
    public String getModifyTimeHtml() { return DateFormatUt.formatDate(pModifyTime, pDATE_HTML); }

/** total delay of all calls on this operation */
    public long getTotalDelay() { return pTotalDelay; }

/** Average delay for this operation */
    public long getAverageDelay() { return pTotalDelay / pCounter;  }

/** min delay encountered on this event */
    public long getMinDelay() { return pMinDelay; }

/** max delay encountered on this event */
    public long getMaxDelay() { return pMaxDelay; }

/** Last 10 delays */
    public long[] getHistory() { return pQueue.toArray(); }


    private static final String pCOUNTER= "Counter=";
    private static final String pAVERAGE = "Average Delay=";
    private static final String pTOTAL = "Total Delay=";
    private static final String pMIN = "Min Delay=";
    private static final String pMAX = "Max Delay=";
    private static final String pHISTORY= "History=";
    private static final String pCREATED = "Created=";
    private static final String pMODIFIED = "Modified=";
    private static final String pCOMMA = ", ";
    private static final String pMS = " ms, ";

    StringBuffer toStringBuffer(final StringBuffer aBuf) {
      item(aBuf, pCOUNTER, pFORMATTER.format(pCounter), pCOMMA);
      item(aBuf, pAVERAGE, pFORMATTER.format(getAverageDelay()), pMS);
      item(aBuf, pTOTAL, pFORMATTER.format(getTotalDelay()), pMS);
      item(aBuf, pMIN, pFORMATTER.format(getMinDelay()), pMS);
      item(aBuf, pMAX, pFORMATTER.format(getMaxDelay()), pMS);
      item(aBuf, pHISTORY, ArrayUtilities.toString(getHistory()) , pMS);

      item(aBuf, pCREATED, getCreateTime(), pCOMMA);
      item(aBuf, pMODIFIED, getModifyTime(), pNL);
      return aBuf;
    }

    private static final String pTABLE_START=
      "<table cellspacing='0' border='1' width='100%' style='font-size: x-small'> <tr align='right'>";
    private static final String pTABLE_END= "</tr></table>";
    private static final String pTR= "</tr> <tr align='right'>";

    private String historyToHtml() {
      StringBuffer b= new StringBuffer(64);
      b.append(pTABLE_START);
      long[] h= getHistory();
      boolean isNewLine= false;
      for (int i=0, n= h.length; i<n; i++) {
        td(b, pFORMATTER.format(h[i]));
        if (i<n-1)  {
          if (isNewLine) b.append(pTR);
          isNewLine= ! isNewLine;
        }
      }
      b.append(pTABLE_END);
      return b.toString();
    }

    StringBuffer toStringBufferHtml(final StringBuffer aBuf) {
      td(aBuf, pFORMATTER.format(pCounter));
      td(aBuf, pFORMATTER.format(getAverageDelay()));
      td(aBuf, pFORMATTER.format(getTotalDelay()));
      td(aBuf, pFORMATTER.format(getMinDelay()));
      td(aBuf, pFORMATTER.format(getMaxDelay()));
      td(aBuf, historyToHtml() );

      td(aBuf, getCreateTimeHtml());
      if (pModifyTime != pCreateTime)  td(aBuf, getModifyTimeHtml());
      else td(aBuf, StringUtility.EQUAL);
      return aBuf;
    }

  }  // LogEntry


  private PerformanceLog() {
    this(MAX_SIZE);
  }

  private PerformanceLog(final int aMaxSize) {
    pMap= new LRUMap(aMaxSize);
  }

/**
 * Put an operation log time into this logger
 * @param aKey String The operation's key, e.g. the text of a sql query, remember not to
 * put the values of the query parameters into the string.
 * @param aDelay long Time in milliseconds the given operation took.
 */
  public synchronized void put(final String aKey, final long aDelay) {
    LogEntry e= get(aKey);
    if (e == null) {
      e= new LogEntry(aDelay);
      pMap.put(aKey, e);
    } else {
      e.updateDelay(aDelay);
    }
  }  // put

/**
 * Put an operation log time into this logger, the end time of the event is now.
 * @param aKey String The operation's key, e.g. the text of a sql query, remember not to
 * put the values of the query parameters into the string.
 * @param aStartTime long Time in milliseconds when the operation started.
 */
  public synchronized void putNow(final String aKey, final long aStartTime) {
    long d= System.currentTimeMillis() - aStartTime;
    put(aKey, d);
  }  // putNow


/** Get info about the given operation, null if not found */
  public synchronized LogEntry get(final String aKey) {
    return (LogEntry) pMap.get(aKey);
  }

/** An iterator over the performancew log entries */
  public synchronized MapIterator mapIterator() {
    return pMap.mapIterator();
  }

/** # of entries in the log */
  public synchronized int size() {
    return pMap.size();
  }

/** Does the log have any items */
  public synchronized boolean isEmpty() {
    return pMap.isEmpty();
  }

  private static final String pTH_START= "    <th>";
  private static final String pTH_END= "</th>";

  private static StringBuffer th(final StringBuffer aBuf, final String aValue) {
    aBuf.append(pTH_START).append(aValue).append(pTH_END).append(pNL);
    return aBuf;
  }

  private static StringBuffer th(final StringBuffer aBuf, final long aValue) {
    return th(aBuf, String.valueOf(aValue));
  }

  private static final String pTD_START= "    <td>";
  private static final String pTD_END= "</td>";

  private static StringBuffer td(final StringBuffer aBuf, final String aValue) {
    aBuf.append(pTD_START).append(aValue).append(pTD_END).append(pNL);
    return aBuf;
  }

//  private static StringBuffer td(final StringBuffer aBuf, final long aValue) {
//    aBuf.append(pTD_START).append(aValue).append(pTD_END).append(pNL);
//    return aBuf;
//  }

  private static final String pTD_START_ALIGN= "    <td align='";
  private static final String pQUOTE_GT= "'>";

  private static StringBuffer td(final StringBuffer aBuf, final String aValue,
                                 final String aAlign) {
    aBuf.append(pTD_START_ALIGN).append(aAlign).append(pQUOTE_GT);
    aBuf.append(aValue).append(pTD_END).append(pNL);
    return aBuf;
  }

  private static final String pFONT_START= "<font size='-1'>";
  private static final String pFONT_END= "</font>";

  private static String smallFont(final String aValue) {
    return pFONT_START + aValue + pFONT_END;
  }

  private static StringBuffer item(final StringBuffer aBuf,
                                   final String aPrefix,
                                   final String aValue,
                                   final String aPosttfix) {
    aBuf.append(aPrefix);
    aBuf.append(aValue);
    aBuf.append(aPosttfix);
    return aBuf;
  }

  private static final String pCOLON= ": ";

/** Convert the log's data to a string */
  public synchronized String toString() {
    StringBuffer b= new StringBuffer(128);
    int i=0;
    for (MapIterator it= mapIterator(); it.hasNext(); ) {
      i++;
      String k= (String) it.next();
      LogEntry e= (LogEntry) it.getValue();
      b.append(i).append(pCOLON);
      b.append(k).append(pCOLON);
      e.toStringBuffer(b);
    }
    return b.toString();
  }

  private static final String pTR_START= "  <tr align='right' valign='top'>";
  private static final String pTR_END= "  </tr>";
  private static final String pLEFT= "left";

/** Convert the log's data to an html string */
  public synchronized String toHtml() {
    StringBuffer b= new StringBuffer(128);
    b.append("<table cellspacing='0' cellpadding='0' border='1'>").append(pNL);
    b.append("  <tr>").append(pNL);
    th(b, "#");
    th(b, "Operation");
    th(b, smallFont("Counter"));
    th(b, smallFont("Average Delay<br>Milliseconds"));
    th(b, smallFont("Total Delay<br>Milliseconds"));
    th(b, smallFont("Min Delay<br>Milliseconds"));
    th(b, smallFont("Max Delay<br>Milliseconds"));
    th(b, smallFont("Recent History<br>Milliseconds"));

    th(b, "Created");
    th(b, "Modified");
    b.append("  </tr>").append(pNL);

    int i=0;
    for (MapIterator it= mapIterator(); it.hasNext(); ) {
      b.append(pTR_START).append(pNL);
      i++;
      String k= (String) it.next();
      LogEntry e= (LogEntry) it.getValue();
      th(b, i);
      td(b, smallFont(k), pLEFT);
      e.toStringBufferHtml(b);
      b.append(pTR_END).append(pNL);
    }
    b.append("</table>").append(pNL);
    return b.toString();
  }

/** for testing */
  public static void main(String[] args) throws Exception {
    LOG.put("select * from schema.table where c in (v1, v2, v3)", 10000);
    LOG.put("q1", 20000);
    LOG.put("q2", 10);
    LOG.put("q2", 30000);
    LOG.put("q2", 500);
    LOG.put("q2", 600);
    LOG.put("q2", 700);
    LOG.put("q2", 800);
    LOG.put("select * from schema.table where c in (v1, v2, v3)", 12000);

    System.out.println(LOG.toString());
    FileIO.write("c:/temp/t1.html", LOG.toHtml());
  }

}  // PerformanceLog
