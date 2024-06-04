package gui.login;

import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Component(
    // TODO Pfade
//    styleUrls = {"\"./Login.scss\"" }
)
public class UserCreate extends UserCreateTOP {
  private String error = "";
  protected GemList<User> users;

  // TODO
//  public static Pattern email = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

  @Override
  public Consumer<Void> createUser() {
    return $ -> {
      this.error = "";
      if (this.getNewUser().getUsername() == null || this.getNewUser().getPassword() == null || this.getNewUser().getEmail() == null) {
        return;
      }
      String errorMessage = this.validateUser();
      if (errorMessage == null || errorMessage.isBlank()) {
        //this.userBuilder.activated(ActivationStatus.AKTIVIERT);
        this.getNewUser().setRegistrationDate(LocalDateTime.now());
        this.getNewUser().setRole(DtcockpitRole.USER);
        this.getNewUser().build();
        Router.navigate("gui", "login");
      }
      else {
        this.error = errorMessage;
      }
    };
  }

  public String getErrorValue() {
    return this.error;
  }

  private String validateUser() {
    String res = "";
    // Check Email format TODO
//    if (!UserCreate.email.matcher(this.getNewUser().getEmail()).matches()) {
//      return "Wrong format for Email address!";
//    }
//    // Check if user is unique
//    for (User user : users) {
//      if (this.getNewUser().getUsername().equals(user.getUsername())) {
//        res += " Account \"" + this.getNewUser().getUsername() + "\"";
//      }
//      if (this.getNewUser().getEmail().equals(user.getEmail())) {
//        res += (res.length() == 0 ? "" : " und ") + " E-mail \"" + this.getNewUser().getEmail() + "\"";
//      }
//    }
    return res + (res.length() == 0 ? "" : " already being used!");
  }

  public Consumer<Void> navigateBack() {
    return $ -> Router.navigate("gui", "login");
  }

}
