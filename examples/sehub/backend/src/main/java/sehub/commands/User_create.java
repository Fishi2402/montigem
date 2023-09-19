/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import de.se_rwth.commons.logging.Log;
import security.MailService;
import sehub.User;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.util.LogUtil;

import javax.mail.MessagingException;

public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  public StatusResult doRun() {
    StatusResult createUserStatus = super.doRun();
    if (!createUserStatus.wasSuccessful()) {
      return createUserStatus;
    }
    return sendMail();
  }

  public StatusResult sendMail() {
    Log.trace("Sending Email to " + user.getEmail(), LogUtil.getLoggerName());
    MailService mailsender = new MailService();
    try {
      mailsender.sendRegistrationMail(user.getEmail(), user.getUsername());
    }
    catch (MessagingException error) {
      Log.error(error.toString());
      return StatusResult.error(getId());
    }
    return StatusResult.ok(this.getId());
  }

}


