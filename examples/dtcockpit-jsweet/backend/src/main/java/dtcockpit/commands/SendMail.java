/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import security.MailService;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.util.LogUtil;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public class SendMail extends SendMailTOP {

  public SendMail(long emailGemId, String subject, String message, List<String> forwards) {
    super(emailGemId, subject, message, forwards);
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public StatusResult doAction() {
    Optional<User> user = DtcockpitManager.userOpt(emailGemId);
    if (!user.isPresent()) {
      return StatusResult.error(emailGemId);
    }

    Log.trace("Sending Email to " + user.get().getEmail(), LogUtil.getLoggerName());
    try {
      MailService.getInstance().sendMail(message, user.get().getEmail(), subject);
    }
    catch (MessagingException error) {
      Log.error(error.toString());
      return StatusResult.error(getId());
    }

    return StatusResult.ok(this.getId());
  }

}


