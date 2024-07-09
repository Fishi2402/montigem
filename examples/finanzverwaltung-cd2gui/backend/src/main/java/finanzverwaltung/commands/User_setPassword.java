/* (c) https://github.com/MontiCore/monticore */
package finanzverwaltung.commands;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class User_setPassword extends User_setPasswordTOP {
  public  User_setPassword(long gemId,java.lang.String password){
    super(gemId, PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password));
  }
}
