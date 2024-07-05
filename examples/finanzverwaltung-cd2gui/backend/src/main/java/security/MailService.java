/* (c) https://github.com/MontiCore/monticore */
package security;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.function.Function;

import static umlp.backendrte.service.websocket.WebSocketConfig.UMLP_DOMAINNAME_ENV;

public class MailService {

  public static final String SENDER = System.getenv().getOrDefault("mailAbsender", "noreply@finanzverwaltung.se.rwth-aachen.de");
  public static final String IDENTIFIER = "uidslpaper";
  protected static final String MAIL_PASSWORD = System.getenv().getOrDefault("mailPassword", "");
  public static final String DEFAULT_URL = "https://finanzverwaltung.demos.se.rwth-aachen.de/";

  public static final String TEXT_REGISTRATION = " You've been successfully registered on the Finanzverwaltung website :-).<br>" +
      "Please log in on " + getLink().apply(getUrl());


  public static final String SUBJECT_REGISTRATION = "New registration";

  protected final Properties properties = new Properties();

  public MailService() {
    addToProperties("mail.smtp.host", "smarthost-tls.rwth-aachen.de");
    addToProperties("mail.smtp.port", 25);
    addToProperties("mail.smtp.auto", "false");
    addToProperties("mail.transport.protocol", "smtp");
  }

  public boolean sendRegistrationMail(String email, String userName) throws MessagingException {
    return sendMail(getAppeal().apply(userName) + TEXT_REGISTRATION, email, SUBJECT_REGISTRATION);
  }

  public boolean sendMail(String mailText, String email, String subject) throws MessagingException {
    Session session = Session.getInstance(properties, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(SENDER, MAIL_PASSWORD);
      }
    });

    // create mail
    MimeMessage mail = new MimeMessage(session);
    mail.setFrom(new InternetAddress(IDENTIFIER + "<" + SENDER + ">"));
    InternetAddress[] address = {new InternetAddress(email)};
    mail.setRecipients(MimeMessage.RecipientType.TO, address);
    mail.setSubject(subject);
    mail.setSentDate(new Date());
    mail.setText(mailText, "UTF-8", "html");

    Transport t = session.getTransport();
    t.connect();
    t.sendMessage(mail, mail.getAllRecipients());
    return true;
  }

  public static String getUrl() {
    return System.getenv().containsKey(UMLP_DOMAINNAME_ENV) ? System.getenv().get(UMLP_DOMAINNAME_ENV) : DEFAULT_URL;
  }

  protected void addToProperties(String key, Object defaultValue) {
    properties.put(key, (System.getenv(key) != null ? System.getenv(key) : defaultValue));
  }

  protected static Function<String, String> getLink() {
    return s -> "<a href='" + s + "'>" + s + "</a>";
  }

  protected static Function<String, String> getAppeal() {
    return s -> "Hello " + s + "!";
  }
}
