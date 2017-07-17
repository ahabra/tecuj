/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2007  Abdul Habra
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
package com.tek271.util.internet.email;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.tek271.util.collections.array.ArrayUtilities;
import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.string.StringUtility;

/**
 * Send email using the SMTP/SMTPS prtocol.
 * I used the smtpsend.java which comes with Java Mail API demo as a 
 * starting point for creating this class. The authors of smtpsend.java are
 * Max Spivak and Bill Shannon.
 * @author Abdul Habra
 * Copyright (C) Abdul Habra 2007
 */
public class EmailSender {
  private static final String pMAILER= "tek271-EmailSender";

/** The smtp server host name, eg. smtp.yourdomain.com  Required. */  
  public String host;
  
/** 
 * The sender's email address. Optional. If you omit it, the system's property 
 * "mail.user" is used. If this property also is absent, the system property 
 * "user.name" is used.   
**/  
  public String from;

/** Comma separated list of recipients. */
  public String to, cc, bcc;
  
/** Subject of the email. */  
  public String subject;
  
/** Text of the email. */  
  public String text;
  
/** An array of attached file names (including paths) */  
  public String[] attachedFileNames;
  
/** Does the smtp server require SSL */  
  public boolean isSSL;

/** Does the smtp server require authentication */  
  public boolean isAuthentication;
  
/** The user name and password. Usually, non-local smtp servers require this. */  
  public String user, password;

/** Send email. Call after you initialize all required properties. */  
  public void send() throws MessagingException, IOException {
    Session session = createSession();
    Message message = createMessage(session);
    sendWithTransport(session, message);
  } // send

/** Create an SMTP session */  
  private Session createSession() {
    Properties props = System.getProperties();
    props.put("mail.smtp.host", host);
    if (isAuthentication) {
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtps.auth", "true");
    }
    return Session.getInstance(props, null);
  }

/** Create the message body including the text and any attachments. */
  private Message createMessage(Session aSession) throws MessagingException, IOException {
    Message message = new MimeMessage(aSession);
    message.setHeader("X-Mailer", pMAILER);
    message.setSentDate(new Date());
    message.setSubject(subject);

    if (StringUtility.isBlank(from))  message.setFrom();
    else message.setFrom(new InternetAddress(from));

    setRecipients(message);
    setContents(message);
    return message;
  }

/** Set the recipients of this email */  
  private void setRecipients(Message message) throws MessagingException {
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
    if (StringUtility.isNotBlank(cc)) {
      message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
    }
    if (StringUtility.isNotBlank(bcc)) {
      message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
    }
  }  // setRecipients

/** Build the contents of the email inculding text and attachments */  
  private void setContents(Message message) throws MessagingException, IOException {
    if (ArrayUtilities.isEmpty(attachedFileNames)) {
      message.setText(text);
      return;
    }

    MimeMultipart content= new MimeMultipart();
    MimeBodyPart mbp= new MimeBodyPart();
    mbp.setText(text);
    content.addBodyPart(mbp);

    for (int i=0, n=attachedFileNames.length; i<n; i++) {
      mbp= new MimeBodyPart();
      mbp.attachFile( attachedFileNames[i] );
      content.addBodyPart(mbp);
    }
    message.setContent(content);
  }  // setContents

/** Actually send the message */  
  private void sendWithTransport(Session aSession, Message aMessage) throws MessagingException {
    Transport transport= aSession.getTransport(isSSL? "smtps" : "smtp");
    if (isAuthentication) transport.connect(host, user, password);
    else                  transport.connect();

    try {
      transport.sendMessage(aMessage, aMessage.getAllRecipients());
    } catch (SendFailedException e) {
      handleSendFailedException(e);
    } finally {
      transport.close();
    }
  }  // sendWithTransport

/** Create a nice readable error list */  
  private void handleSendFailedException(MessagingException exception) throws MessagingException {
    ListOfString list= new ListOfString();
    dumpSmtpException(exception, list);

    Exception ne;
    while ((ne=exception.getNextException()) != null && ne instanceof MessagingException) {
      exception = (MessagingException) ne;
      dumpSmtpException(exception, list);
    }
    throw new MessagingException(list.toString());
  }  // handleSendFailedException
  
/** Dump the given smtp exception into the given list */  
  private static void dumpSmtpException(MessagingException exception, ListOfString list) {
    if (exception instanceof SMTPSendFailedException) {
      SMTPSendFailedException ssfe = (SMTPSendFailedException) exception;
      list.addItem("SMTP Send failed:");
      list.addItem("  Command: " + ssfe.getCommand());
      list.addItem("  RetCode: " + ssfe.getReturnCode());
      list.addItem("  Response: " + ssfe.getMessage());
      list.addItem("    " + ssfe.toString());
    } else
    if (exception instanceof SMTPAddressFailedException) {
      SMTPAddressFailedException ssfe = (SMTPAddressFailedException) exception;
      list.addItem("Address Failed:");
      list.addItem("  Address: " + ssfe.getAddress());
      list.addItem("  Command: " + ssfe.getCommand());
      list.addItem("  RetCode: " + ssfe.getReturnCode());
      list.addItem("  Response: " + ssfe.getMessage());
      list.addItem("    " + ssfe.toString());
    } else 
    if (exception instanceof SMTPAddressSucceededException) {
      SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) exception;
      list.addItem("Address Succeeded:");
      list.addItem("  Address: " + ssfe.getAddress());
      list.addItem("  Command: " + ssfe.getCommand());
      list.addItem("  RetCode: " + ssfe.getReturnCode());
      list.addItem("  Response: " + ssfe.getMessage());
      list.addItem("    " + ssfe.toString());
    }else {
      list.addItem("Send failed: " + exception.toString());
    }
  }  // dumpSmtpException
  
}
