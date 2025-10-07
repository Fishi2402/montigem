package loginmodule.commands;

import loginmodule.LoginModuleUser;
import loginmodule.LoginModuleUserAccessPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class LoginModuleUser_create extends LoginModuleUser_createTOP {
  public LoginModuleUser_create(LoginModuleUser loginModuleUser) {
    super(loginModuleUser);
  }

  @Override
  public StatusResult doAction() {
    // Replace cleartext password with hashed password
    getLoginModuleUser().setPassword(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(getLoginModuleUser().getPassword()), NotificationScope.LOCAL);


    LoginModuleUserAccessPolicy accessPolicy = new LoginModuleUserAccessPolicy();
    if (accessPolicy.checkCreatePermission(this)) {
      super.doAction();
      // TODO: Move sendMail to a listener
      return sendMail();
    } else {
      return StatusResult.error(getId());
    }

  }

  public StatusResult sendMail() {
    // TODO: Send a welcome email?
//    Log.trace("Sending Email to " + user.getEmail(), LogUtil.getLoggerName());
//    MailService mailsender = new MailService();
//    try {
//      mailsender.sendRegistrationMail(user.getEmail(), user.getUsername());
//    }
//    catch (MessagingException error) {
//      Log.error(error.toString());
//      return StatusResult.error(getId());
//    }
    return StatusResult.ok(this.getId());
  }
}
