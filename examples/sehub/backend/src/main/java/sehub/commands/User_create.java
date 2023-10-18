/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import de.se_rwth.commons.logging.Log;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import security.MailService;
import sehub.SehubManager;
import sehub.User;
import sehub.UserAccessPolicy;
import sehub.UserValidator;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;
import umlp.backendrte.common.ValidationMessage;
import umlp.backendrte.common.ValidationStatus;
import umlp.backendrte.util.LogUtil;

import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;

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
      UserValidator validator = new UserValidator();
      List<ValidationMessage> messages = validator.validate(user);
      if (messages.stream().anyMatch(m -> m.getStatus() == ValidationStatus.ERROR)) {
        Log.error("Validation failed for user:\n" + messages.stream().filter(m -> m.getStatus() == ValidationStatus.ERROR).map(ValidationMessage::getMessage).collect(Collectors.joining("\n")));
        return StatusResult.error(this.getId());
      }
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


