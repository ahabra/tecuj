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

/**
 * A buffered logger that uses log4j.
 * Remember to call <b>flush</b> when you are done with objects of this class.
 * Note that this class extends AbstractBufferedLogger which implements the
 * ILogger interface, so you can use this class whereever ILogger is expected.
 * <p>Copyright (c) 2005 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class BufferedLog4jAdapter extends AbstractBufferedLogger {
  private Log4jAdapter pLogger;

/** Create a default logger.  */
  public BufferedLog4jAdapter() {
    super();
    pLogger= new Log4jAdapter();
  }

/**
 * Create a buffered logger. With: <ol>
 * <li>Buffered Log Level= ILogger.ERROR
 * <li>IsLogAllStackTraces= true
 * <li>Wait period= 60 * 5 seconds
 * <li>Buffer size= 8
 * </ol>
 * @param aPropertiesFileName String Log4j configuration file name.
 */
  public BufferedLog4jAdapter(final String aPropertiesFileName) {
    super();
    pLogger= new Log4jAdapter(aPropertiesFileName);
  }

/**
 * @param aPropertiesFileName String Log4j configuration file name.
 * @param aWaitPeriod Seconds to wait before a buffered log entry is eligible to be
 *        logged on next logging request.
 * @param aBufferSize int # of distinct log messages to be buffered.
 * @param aBufferedLogLevel int Error levels as defined in ILogger.
 * @param aIsLogAllStackTraces boolean If true, all stacktraces will be logged for
 *        each buffered error. If false, only the first stack trace for an error will be
 *        logged.
 * @param aIsListThreadName Should the thread name be listed at the begining of the log
 *        message.
 */
  public BufferedLog4jAdapter(final String aPropertiesFileName,
                              final int aWaitPeriod,
                              final int aBufferSize,
                              final int aBufferedLogLevel,
                              final boolean aIsLogAllStackTraces,
                              final boolean aIsListThreadName) {
    super(aWaitPeriod, aBufferSize, aBufferedLogLevel, aIsLogAllStackTraces, aIsListThreadName);
    pLogger= new Log4jAdapter(aPropertiesFileName);
  }

/** implement abstract method of AbstractBufferedLogger class */
  protected void logNow(final int aLevel, final String aMsg, final Throwable aThrowable) {
    if (aThrowable==null)  pLogger.log(aLevel, aMsg);
    else pLogger.log(aLevel, aMsg, aThrowable);
  }

}  // BufferedLog4jAdapter
