package gui.login;

import security.MailService;

import java.util.ArrayList;
import java.util.List;

public class MailServiceMock extends MailService {
  protected List<MailMock> mails = new ArrayList<>();

  public static void init(){
    instance = new MailServiceMock();
  }

  public static MailServiceMock getInstance(){
    return (MailServiceMock) instance;
  }

  public List<MailMock> getMails() {
    return mails;
  }

  @Override
  public boolean sendMail(String mailText, String email, String subject) {
    mails.add(new MailMock(mailText, email, subject));
    return true;
  }

  public static class MailMock {
    final String mailText;
    final String email;
    final String subject;

    public MailMock(String mailText, String email, String subject) {
      this.mailText = mailText;
      this.email = email;
      this.subject = subject;
    }
  }
}
