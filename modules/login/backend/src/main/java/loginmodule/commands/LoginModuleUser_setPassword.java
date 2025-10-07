package loginmodule.commands;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class LoginModuleUser_setPassword extends LoginModuleUser_setPasswordTOP {
  public LoginModuleUser_setPassword(long gemId, java.lang.String password) {
    super(gemId, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
  }

}
