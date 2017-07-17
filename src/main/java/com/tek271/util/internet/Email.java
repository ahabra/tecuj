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

package com.tek271.util.internet;

import java.util.*;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import com.tek271.util.log.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.string.StringUtility;

/**
 * Simplifies sending SMTP email. Its static methods <code>send()</code> and
 * <code>sendAsynchronous()</code> are very easy to use and send email.
 * <p>Requires javax.mail. Can be downloaded at:
 * <code>http://java.sun.com/products/javamail/downloads/index.html</code>.
 * However, this is included with the tecuj.jar.
 * </p>
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 * @deprecated as of 9/2007. Use the class 
 *    <code>com.tek271.util.internet.email.EmailSender</code> instead.
 */
public class Email {
  /** name of protocol to use: SMTP */
  private static final String pPROTOCOL = "mail.smtp.host";
  private static final String pERR_FROM_ADDRESS = "Invalid 'From' address:";
  private static final String pERR_TO_ADDRESS = "Invalid 'To' address:";

  private String pHost;
  private InternetAddress pFrom;
  private List pTo; // of String
  private String pSubject;
  private String pText;

  private InternetAddress[] mToAddresses;

  /** For testing */
  public static void main(String[] args) throws Exception {
    System.out.println("Attempting to send ...");
    Email m = new Email();
    List to = new ArrayList();
    to.add("abdul.habra@tek271.com");

    m.setFrom("x@yahoo.com");
    m.setHost("mail.tek271.com");
    m.setSubject("testing");
    m.setText("Life is a continuos battle.\nTesting SMTP.\n");
    m.setTo(to);
    m.send();
    System.out.println("Send Complete.");
  } // main

  /** The name of the mail server host. E.g. mail.tek271.com */
  public void setHost(String aHost) {
    pHost = aHost;
  } // setHost

  public String getHost() {
    return pHost;
  } // getHost

/**
* Email address of the sender.
* @param aFrom The email address of the sender.
* @exception AddressException If aFrom address is not valid.
*/
  public void setFrom(String aFrom) throws AddressException {
    try {
      pFrom = new InternetAddress( aFrom );
    } catch (AddressException e) {
      throw new AddressException(pERR_FROM_ADDRESS + aFrom + ", " + e.getMessage() );
    }
  } // setFrom

  public String getFrom() {
    return pFrom.toString() ;
  } // getFrom

  /** Convert a List of addresses into an array of InternetAddress. */
  private InternetAddress[] convertAddress(List aAddresses) throws AddressException {
    InternetAddress[] add = new InternetAddress[aAddresses.size() ];
    Iterator it = aAddresses.iterator();
    int i=0;
    String st= StringUtility.EMPTY;

    try {
      while (it.hasNext()) {
        st = (String)it.next();
        add[i++] = new InternetAddress(st);
      } // while
      return add;
    }
    catch (AddressException e) {
      throw new AddressException(pERR_TO_ADDRESS + st + ", " + e.getMessage() );
    }
  } // convertAddress

  /**
   * A list of email addresses of the recipients.
   * @param aTo a list of String objects, each item is an address for a recipient.
   * @exception AddressException If an address is not valid.
   */
  public void setTo(List aTo) throws AddressException {
    if (aTo == null)
      throw new AddressException("The recipients aTo in setTo(List aTo) is null.");
    if (aTo.size()==0)
      throw new AddressException("The recipients aTo in setTo(List aTo) have Zero count.");

    mToAddresses = convertAddress(aTo);
    pTo = aTo;
  } // setTo

  /**
  * Email addresses of the recipients.
  * @param aTo A comma separated list of recipients.
  * @exception AddressException If an address is not valid.
  */
  public void setTo(String aTo) throws AddressException {
    ListOfString list = new ListOfString();
    list.lineSeparator= ",";
    list.setText(aTo);
    setTo(list);
  } // setTo

  public List getTo() {
    return pTo;
  } // getTo

/** Subject of the email. */
  public void setSubject(String aSubject) {
    pSubject = aSubject;
  } // setSubject

  public String getSubject() {
    return pSubject;
  } // getSubject

/** The text of the email. */
  public void setText(String aText) {
    pText = aText;
  } // setText

  public String getText() {
    return pText;
  } // getText

/** Create the msg. body */
  private Message createMessage() throws MessagingException {
    Properties props = new Properties();
    props.put(pPROTOCOL ,pHost);
    Session session = Session.getDefaultInstance(props, null);

    Message msg = new MimeMessage(session);

    msg.setFrom(pFrom);
    msg.setRecipients(Message.RecipientType.TO, mToAddresses );
    msg.setSubject(pSubject);
    msg.setText(pText);
    return msg;
  }

/**
 * Create the message body, logging errors
 * @param aLogger ILogger
 * @return the message, null if error.
 */
  private Message createMessage(final ILogger aLogger) {
    try {
      return createMessage();
    } catch (MessagingException e) {
      aLogger.log(ILogger.ERROR, "Cannot create email message. " + e.getMessage() , e);
      return null;
    }
  } // createMessage

/** Send the message, logging the errors. return false if error. */
  private boolean send(final Message aMessage, final ILogger aLogger) {
    try {
      Transport.send(aMessage);
      return true;
    } catch (MessagingException e) {
      aLogger.log(ILogger.ERROR, "Cannot send email. " + e.getMessage(), e);
      return false;
    }
  }  // send

/** Encapsulate sending in a Runnable to enable threading */
  private class Sender implements Runnable {
    private ILogger pLogger;
    private Message pMessage;

    Sender(final Message aMessage, final ILogger aLogger) {
      pLogger= aLogger;
      pMessage= aMessage;
    }

    public void run() {
      send(pMessage, pLogger);
    }
  } // Sender

/** Send the email, returns right away without waiting for the completion of the send */
  public synchronized void sendAsynchronous(final ILogger aLogger) {
    final Message msg= createMessage(aLogger);
    if (msg==null) return;

    Sender s= new Sender(msg, aLogger);
    Thread t= new Thread(s);
    t.start();
  } // sendAsynchronous

/**
* Send the email.
* All properties must have been initialized before calling this method.
*/
  public void send() throws MessagingException {
    Message msg = createMessage();
    Transport.send(msg);
  } // send

  private static Email create(final String aHost,
                              final String aFrom,
                              final String aTo,
                              final String aSubject,
                              final String aText)
             throws AddressException, MessagingException {
    Email m=new Email();
    m.setHost(aHost);
    m.setFrom(aFrom);
    m.setTo(aTo);
    m.setSubject(aSubject);
    m.setText(aText);
    return m;
  }  // create

/**
 * A utility static method to send an email text message.
 * @param aHost String The smtp host server.
 * @param aFrom String The address of the sender.
 * @param aTo String A comma separated string of receipients.
 * @param aSubject String The subject of the email.
 * @param aText String The boody of the mail
 * @throws AddressException If aFrom or aTo are not valid
 * @throws MessagingException If sending fails
 */
  public static void send(final String aHost,
                          final String aFrom,
                          final String aTo,
                          final String aSubject,
                          final String aText)
                throws AddressException, MessagingException {
    Email m = create(aHost, aFrom, aTo, aSubject, aText);
    m.send();
  }  // send

/**
 * A utility static method to send (asynchronously) an email text message.
 * @param aHost String The smtp host server.
 * @param aFrom String The address of the sender.
 * @param aTo String A comma separated string of receipients.
 * @param aSubject String The subject of the email.
 * @param aText String The boody of the mail
 * @param aLogger Logger used for errors
 */
  public static void sendAsynchronous(final String aHost,
                                      final String aFrom,
                                      final String aTo,
                                      final String aSubject,
                                      final String aText,
                                      final ILogger aLogger)
                throws AddressException, MessagingException {
    Email m = create(aHost, aFrom, aTo, aSubject, aText);
    m.sendAsynchronous(aLogger);
  }  // sendAsynchronous

  private final static String pINVALID_EMAIL_CHARS= "()<>,;:\\\"[] \t\r\n";

/**
 * Validate that the given address is a valid (syntactically) email address.
 * It does not check if the address actually exists.
 * @param aAddress String
 * @return String null/empty if the address is valid, else returns the text of the
 * arror message.
 */
  public static String validateEmailAddress(String aAddress) {
    if (StringUtility.isBlank(aAddress)) return "Email address has no value.";
    aAddress= aAddress.trim();
    //a@b.c
    if (aAddress.length()<5) return "Email address must be at least 5 characters.";
    if (StringUtility.indexOfAny(aAddress, pINVALID_EMAIL_CHARS) >= 0) {
      return "Email address contains invalid characters.";
    }

    int at= aAddress.indexOf('@');
    if (at<0) return "Email address must contain the @ sign.";
    if (aAddress.indexOf('.') <0) return "Email address must contain . (Dot).";

    String domain= StringUtility.substring(aAddress, at+1);
    if (domain.length()==0) return "Email address has no domain.";

    if (domain.charAt(0)=='.') return "Invalid domain (Cannot start with a dot).";
    if (domain.indexOf('.') <0) return "Email address domain must contain a . (Dot).";
    if (domain.indexOf('@') >=0) return "Domain connot contain the @ sign.";

    return null;
  }

} // Email
