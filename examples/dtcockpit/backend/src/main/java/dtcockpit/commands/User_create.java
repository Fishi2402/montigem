/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import de.se_rwth.commons.logging.Log;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import security.MailService;
import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import dtcockpit.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;
import umlp.backendrte.util.LogUtil;

import javax.mail.MessagingException;

public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  @Override
  public StatusResult doAction() {
    // Replace cleartext password with hashed password
    user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()), NotificationScope.LOCAL);

    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkCreatePermission(this)) {
      DtcockpitManager.addUserWithScope(user, NotificationScope.LOCAL);
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


