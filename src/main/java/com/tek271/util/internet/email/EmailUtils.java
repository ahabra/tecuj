package com.tek271.util.internet.email;

import java.io.*;
import javax.mail.*;

import com.tek271.util.string.StringUtility;

/**
 * Email helper static methods.
 * @author Abdul Habra
 * <p>Copyright (c) 2007 Technology Exponent</p>
 */
public class EmailUtils {

  public static void send(final String aHost,
                          final String aFrom,
                          final String aTo,
                          final String aSubject,
                          final String aText)
                throws IOException, MessagingException {
    EmailSender es= new EmailSender();
    es.host= aHost;
    es.from= aFrom;
    es.to= aTo;
    es.subject= aSubject;
    es.text= aText;

    es.send();
  }  // send

  private final static String pINVALID_EMAIL_CHARS= "()<>,;:\\\"[] \t\r\n";

/**
 * Validate that the given address is a syntactically valid email address.
 * It does not check if the address actually exists.
 * @param aAddress String
 * @return String null/empty if the address is valid, else returns the text of the
 * error message.
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
    if (domain.endsWith(".")) return "Email address domain cannot end with a . (Dot).";

    return null;
  }
  
}
