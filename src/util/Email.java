package util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;


/**
 *  Generic class to send out email
 */
public final class Email {
  private int msgId = -1;
  private String fromAddr=null;
  private String fromName=null;
  private String toAddr=null;
  private String toName=null;
  private String subject=null;
  private String body=null;
  private ArrayList recipients=null;

  public Email(){}

  /**
   * Constructs a new Email message
   * @param fromName
   * @param fromAddr
   * @param toName
   * @param toAddr
   * @param subj
   * @param body
   */
  public Email(String fromName, String fromAddr, String toName, String toAddr, String subj, String body) {
    this.fromName=fromName;
    this.fromAddr=fromAddr;
    this.addRecipient(toAddr, toName);
    this.subject=subj;
    this.body=body;
  }


  /**
   * The email address of the sender of this message
   */
  public final void setFromAddr(String fromAddr){
    this.fromAddr=fromAddr;
  }

  /**
   * The symbolic name to be placed in the from field. eg. 'Error Reporter'
   */
  public final void setFromName(String fromName){
    this.fromName=fromName;
  }

  /**
   * Set both from name and from address
   */
  public final void setFrom(String name, String address){
    this.fromName=name;
    this.fromAddr=address;
  }

  /**
   * The recipient's email address
   */
  public final void setToAddr(String toAddr){
    this.toAddr=toAddr;
  }

  /**
   * The recipient's name
   */
  public final void setToName(String toName){
    this.toName=toName;
  }

  /**
   * The message subject
   */
  public final void setSubject(String subject){
    this.subject=subject;
  }

  /**
   * Get the message id. This is an optional field that stores a reference to the
   * msgmaster tables key that was used to generate this message.
   */
  public final int getMsgId(){
    return msgId;
  }

  /**
   * Set the message id. This is an optional field that stores a reference to the
   * msgmaster tables key that was used to generate this message.
   */
  public final void setMsgId(int msgId){
    this.msgId=msgId;
  }

  /**
   * Get the message subject
   */
  public final String getSubject(){
    return subject;
  }


  /**
   * This methods add individuals to the recipient list
   * @return true if the add was successful
   */
  public final boolean addRecipient(String toAddr, String toName){
    try {
      InternetAddress temp = new InternetAddress(toAddr,toName);
      if (recipients==null)
        recipients = new ArrayList();
      recipients.add(temp);
    }
    catch (java.io.UnsupportedEncodingException e){
      return false;
    }
    return true;
  }

  /**
   * The message body
   */
  public final void setBody(String body){
    this.body=body;
  }

  /**
   * Get the message body
   */
  public final String getBody(){
    return body;
  }

  public final void send() {
    Properties props = new Properties();
    //SWD Hardcoded to test. Fix with Servlet property
    props.put("mail.smtp.host","localhost");
    Session mailSession = Session.getDefaultInstance(props);
    try {
      InternetAddress from = new InternetAddress(fromAddr,fromName);
      InternetAddress to = null;

      Message msg = new MimeMessage(mailSession);
      // A recipient may have been specified using the setToAddr and setToName
      // methods. If so, create the recipient
      if (toAddr!=null){
        to = new InternetAddress(toAddr,toName);
        msg.addRecipient(Message.RecipientType.TO,to);
      }

      msg.setFrom(from);

      // If this mail is going to more than one recipient the recipients list
      // will have been initialized and contain InternetAddresses
      if (recipients!=null){
        for (int i=0;i<recipients.size();i++){
          InternetAddress temp = (InternetAddress)recipients.get(i);
          msg.addRecipient(Message.RecipientType.TO,temp);
        }
      }

      msg.setSubject(subject);
      msg.setContent(body, "text/plain");

      // Send the message
      Transport.send(msg);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public final String toString(){
    StringBuffer toReturn = new StringBuffer();
    toReturn.append("From: \"").append(fromName).append("\"").append(" <").append(fromAddr).append(">\n");
    if (toName!=null || toAddr!=null)
      toReturn.append("To: \"").append(toName).append("\"").append(" <").append(toAddr).append(">\n");
    if (recipients!=null){
      for (int i=0;i<recipients.size();i++){
        InternetAddress temp = (InternetAddress)recipients.get(i);
        toReturn.append(temp.getAddress());
        if (i!=recipients.size()-1)
          toReturn.append(", ");
      }
    }
    toReturn.append("Subject: ").append(subject);
    toReturn.append("\nBody: ").append(body);
    return toReturn.toString();
  }
}

