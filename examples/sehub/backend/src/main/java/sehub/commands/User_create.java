/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import de.se_rwth.commons.logging.Log;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import security.MailService;
import sehub.SehubManager;
import sehub.User;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;
import umlp.backendrte.util.LogUtil;
import sehub.UserAccessPolicy;

import javax.mail.MessagingException;

public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  public StatusResult doRun() {
:wq
    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkCreatePermission(this)) {
      SehubManager.addUser(user, NotificationScope.LOCAL);
      return sendMail();
    } else {
      return StatusResult.error(getId());
    }

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


