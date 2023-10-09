/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import de.se_rwth.commons.logging.Log;
import security.MailService;
import sehub.Issue;
import sehub.SehubManager;
import sehub.User;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.util.LogUtil;

import javax.mail.MessagingException;
import java.util.Optional;

public class SendIssueMail extends SendIssueMailTOP {

  public SendIssueMail(long gemId, long emailGemId, long subjectGemId, String message) {
    super(gemId, emailGemId, subjectGemId, message);
  }

  public StatusResult doAction() {
    Optional<User> user = SehubManager.userOpt(emailGemId);
    if (!user.isPresent()) {
      return StatusResult.error(emailGemId);
    }

    Optional<Issue> subject = SehubManager.issueOpt(subjectGemId);
    if (!user.isPresent()) {
      return StatusResult.error(subjectGemId);
    }

    Log.trace("Sending Email to " + user.get().getEmail(), LogUtil.getLoggerName());
    MailService mailsender = new MailService();
    try {
      mailsender.sendMail(message, user.get().getEmail(), subject.get().getTitle());
    }
    catch (MessagingException error) {
      Log.error(error.toString());
      return StatusResult.error(getId());
    }

    return StatusResult.ok(this.getId());
  }

}


