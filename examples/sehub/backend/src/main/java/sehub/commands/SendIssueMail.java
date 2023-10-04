/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import de.se_rwth.commons.logging.Log;
import security.MailService;
import sehub.Issue;
import sehub.User;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.util.LogUtil;

import javax.mail.MessagingException;

public class SendIssueMail extends SendIssueMailTOP {

  public SendIssueMail(User email, Issue subject, String message) {
    super(email, subject, message);
  }

  public StatusResult doAction() {
    Log.trace("Sending Email to " + email, LogUtil.getLoggerName());
    MailService mailsender = new MailService();
    try {
      mailsender.sendMail(message, email.getEmail(), subject.getTitle());
    }
    catch (MessagingException error) {
      Log.error(error.toString());
      return StatusResult.error(getId());
    }
    return StatusResult.ok(this.getId());
  }

}


