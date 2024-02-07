/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import dtcockpit.UserAccessPolicy;
import security.MailService;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;
import umlp.backendrte.util.LogUtil;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import javax.mail.MessagingException;

public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  @Override
  public StatusResult doAction() {
    // Replace cleartext password with hashed password
//    user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()), NotificationScope.LOCAL);

    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkCreatePermission(this)) {
      DtcockpitManager.addUser(user, NotificationScope.LOCAL);
      return sendMail();
    } else {
      return StatusResult.error(getId());
    }

  }

  @BackendOnly(BackendOnlyModes.STATUS_OK)
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


