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
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import javax.mail.MessagingException;

// TODO: Module System: User_create and LoginModuleUser_create have no relation at all
public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public StatusResult doAction() {
    // Replace cleartext password with hashed password
    user.setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(user.getPassword()), NotificationScope.LOCAL);

    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkCreatePermission(this)) {
      DtcockpitManager.addWithScopeUser(user, NotificationScope.LOCAL);
      return sendMail();
    } else {
      return StatusResult.error(getId());
    }

  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
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


