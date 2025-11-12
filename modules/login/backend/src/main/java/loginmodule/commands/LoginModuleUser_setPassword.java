package loginmodule.commands;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class LoginModuleUser_setPassword extends LoginModuleUser_setPasswordTOP {
  public LoginModuleUser_setPassword(long gemId, java.lang.String password) {
    super(gemId, password);
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  @Override
  public StatusResult doAction() {
    PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    return super.doAction();
  }

}
