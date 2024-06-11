/* (c) https://github.com/MontiCore/monticore */
package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ResetPassword extends ResetPasswordTOP {
  public ResetPassword(GemList<User> users, User user) {
    setUsers(users);
    setUser(user);
  }

  @Override
  public void init() {
    setUsers(DtcockpitManager.getUserList());

    if (this.getUser() == null) {
      this.setUser(new User());
    }
  }


  private String error = "";

  @Override
  public Consumer<Void> passwordReset() {
    return $ -> {
      if (this.getUser() == null || this.getUser().getPassword() == null || this.getUser().getEmail() == null) {
        return;
      }
      for (User u : this.getUsers()) {
        if (u != null && this.getUser().getEmail() != null && this.getUser().getEmail().equals(u.getEmail())) {
          u.setPassword(this.getUser().getPassword());
          Router.navigate("gui", "login", "LoginPage");
        }
      }
      this.error = "There is no getUser() with the E-mail \"" + this.getUser().getEmail() + "\" !";
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
    return $ -> Router.navigate("gui", "login", "LoginPage");
  }
}
