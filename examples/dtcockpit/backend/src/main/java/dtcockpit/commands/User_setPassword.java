/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

// TODO: Module System: User_setPassword and LoginModuleUser_setPassword have no relation at all
public class User_setPassword extends User_setPasswordTOP {
  public  User_setPassword(long gemId,java.lang.String password){
    super(gemId, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
  }
}
