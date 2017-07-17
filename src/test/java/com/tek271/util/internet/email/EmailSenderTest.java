package com.tek271.util.internet.email;

import com.tek271.util.time.DateUt;

import junit.framework.*;

public class EmailSenderTest extends TestCase {

  public void testSendTek271() throws Exception {
    EmailSender es= new EmailSender();
    es.host= "tek271.com";
    es.user= "validEmailAccount@tek271.com";
    es.password= "PASSWORD";
    es.isAuthentication= true;
    es.isSSL= false;
    
    es.from= "validEmailAccount@tek271.com";
    es.to= "somebody@yahoo.com";
    es.subject= "testSendTek271() - " + DateUt.currentDateTime();
    es.text= "This is a test mail";
    es.attachedFileNames= new String[] {"c:/temp/junk.txt"};
//    es.send();
//    assertTrue(true);
  }

  public void testSendGoogle() throws Exception {
    EmailSender es= new EmailSender();
    es.host= "smtp.gmail.com";
    es.user= "abdul";   // without @gmail.com
    es.password= "PASSWORD";   
    es.isAuthentication= true;
    es.isSSL= true;

    es.from= "abdul@gmail.com";
    es.to= "ahabra@yahoo.com";
    es.subject= "testSendGoogle() - " + DateUt.currentDateTime();
    es.text= "This is a test mail";
    es.attachedFileNames= new String[] {"c:/temp/test.pdf"};
    try {
      es.send();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertTrue(true);
  }

}
