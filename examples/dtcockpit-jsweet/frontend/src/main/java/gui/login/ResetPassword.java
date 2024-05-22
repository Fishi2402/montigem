/* (c) https://github.com/MontiCore/monticore */
package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

@Component
public class ResetPassword extends ResetPasswordTOP {
  public ResetPassword(GemList users, User user) {
    super(users, user);
  }

  @Override
  public void init() {
    this.users = DtcockpitManager.getUserList();

    if (this.user == null) {
      this.user = new User();
    }
  }


  private String error = "";

  @Override
  public void passwordReset(Object event) {
    if (this.user == null || this.user.getPassword() == null || this.user.getEmail() == null) {
      return;
    }
    for (User u : this.users) {
      if (u != null && this.user.getEmail() != null && this.user.getEmail().equals(u.getEmail())) {
        u.setPassword(this.user.getPassword());
        Router.navigate("gui", "login", "LoginPage");
      }
    }
    this.error = "There is no user with the E-mail \"" + this.user.getEmail() +"\" !";
    this.user.setValidityEmail(this.error);
    this.user.setStatusEmail("Error");
  }

  @Override
  public String getErrorValue() {
    return this.error;
  }

  @Override
  public void navigateBack(Object event) {
    Router.navigate("gui", "login", "LoginPage");
  }
}
