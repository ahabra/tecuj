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

package com.tek271.util.io;

import java.net.*;
import java.io.*;
import org.apache.commons.io.IOUtils;

import com.tek271.util.log.ILogger;
import com.tek271.util.exception.ExceptionUtil;
import com.tek271.util.string.*;

public class Net {
  private final static String pCLASS_NAME= "com.tek271.util.io.Net";

  public Net() {}

/** Log an error message */
  private static void error(final ILogger aLogger, final String aMethod,
                            final String aMessage, final Exception aException) {
    ExceptionUtil.error(aLogger, pCLASS_NAME, aMethod, aMessage, aException);
  }

/** Create a URL object from the given string, log errors if they occur */
  public static URL createUrl(final ILogger aLogger,
                              final String aUrl) {
    try {
      return new URL(aUrl);
    } catch (IOException ex) {
      error(aLogger, "createUrl", "Cannot create URL object", ex);
      return null;
    }
  }  // createUrl

  public static InputStream getInputStreamFromUrl(final ILogger aLogger,
                                                  final URL aUrl) {
    try {
      return aUrl.openStream();
    } catch (IOException ex) {
      error(aLogger, "getInputStream", "Cannot open given URL", ex);
      return null;
    }
  }

/** Read the given url to a byte array */
  public static byte[] urlToByteArray(final ILogger aLogger,
                                      final URL aUrl) {
    String method= "urlToByteArray";
    InputStream is= getInputStreamFromUrl(aLogger, aUrl);
    if (is==null) return null;

    byte[] r;
    try {
      r=IOUtils.toByteArray(is);
      return r;
    } catch (IOException ex) {
      error(aLogger, method, "Cannot read from given URL", ex);
      return null;
    } finally {
      IOUtils.closeQuietly(is);
    }
  }  // urlToByteArray


/** Read the given url to a byte array */
  public static byte[] urlToByteArray(final ILogger aLogger,
                                      final String aUrl) {
    URL url= createUrl(aLogger, aUrl);
    if (url==null) return null;
    return urlToByteArray(aLogger, url);
  }  // urlToByteArray

/** Read the given url to a String */
  public static String urlToString(final ILogger aLogger,
                                   final URL aUrl) {
    String method= "urlToString";
    InputStream is= getInputStreamFromUrl(aLogger, aUrl);
    if (is==null) return null;

    String r;
    try {
      r= IOUtils.toString(is);
      return r;
    } catch (IOException ex) {
      error(aLogger, method, "Cannot read from given URL", ex);
      return null;
    } finally {
      IOUtils.closeQuietly(is);
    }
  }  // urlToString


/** Read the given url to a String */
  public static String urlToString(final ILogger aLogger,
                                   final String aUrl) {
    URL url= createUrl(aLogger, aUrl);
    if (url==null) return null;
    return urlToString(aLogger, url);
  }  // urlToString

/** Url encode the given string with UTF-8, if error happen, log it */
  public static String urlEncodeUtf8(final ILogger aLogger,
                                     final String aString) {
    if (StringUtility.isEmpty(aString)) return StringUtility.EMPTY;

    try {
      return URLEncoder.encode(aString, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      error(aLogger, "urlEncodeUtf8", "Cannot encode string:"+aString, ex);
      return null;
    }
  }  // urlEncodeUtf8

/** Url encode the given string with UTF-8, if error happen, log it */
  public static String urlDecodeUtf8(final ILogger aLogger,
                                     final String aString) {
    if (StringUtility.isEmpty(aString)) return StringUtility.EMPTY;

    try {
      return URLDecoder.decode(aString, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      error(aLogger, "urlDecodeUtf8", "Cannot decode string:"+aString, ex);
      return null;
    }
  }  // urlDecodeUtf8


}  // Net
