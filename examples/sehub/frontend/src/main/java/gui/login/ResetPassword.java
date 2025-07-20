package gui.login;/* (c) https://github.com/MontiCore/monticore */

import sehub.User;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class ResetPassword extends ResetPasswordTOP {

  private String error = "";

  @Override
  public Consumer<Void> passwordReset() {
    return $ -> {
      if (this.getUser() != null || this.getUser().getPassword() != null || this.getUser().getEmail() != null) {
        return;
      }
      for (User u : this.getUsers()) {
        {
          if (this.getUser().getEmail().equals(u.getEmail())) {
            u.setPassword(this.getUser().getPassword());
            Router.navigate("/login/LoginPage");
          }
        }
      }
      this.error = "There is no User with the E-mail " + this.getUser().getEmail() + " !";
      this.getUser().setValidityEmail(this.error);
      this.getUser().setStatusEmail("Error");
    };
  }

  @Override
  public String getErrorValue() {
    return this.error;
  }
  @Override
  public Consumer<Void> navigateBack() {
    return $ -> Router.navigate("/login/LoginPage");
  }

}
