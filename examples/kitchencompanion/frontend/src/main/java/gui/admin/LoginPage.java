package gui.admin;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.guiutil.Router;

import java.util.function.Consumer;
public class LoginPage extends LoginPageTOP {
  private GemTextInputValidity validity = new GemTextInputValidity("Success","validity hasnt been checked yet");
  @Override
  public Consumer<Void> login(String pw) {
    return $ -> {
      System.out.println("Only temporary login page for admin");
      if(LoginServiceFront.canLogin(pw)){
        validity = new GemTextInputValidity("Success", "");
      } else{
        validity = new GemTextInputValidity("Error", "Wrong password");
      }
      getPasswordValidity();
      LoginServiceFront.login(pw);
      if(LoginServiceFront.getLoggedIn()){
        System.out.println(true);
        Router.navigate("gui", "admin", "Admin");
      }
    };
  }

  @Override
  public GemTextInputValidity getPasswordValidity() {
    return validity;
  }

  @Override
  public void init() {
    super.init();
    if(LoginServiceFront.getLoggedIn()){
      Router.navigate("gui", "admin", "Admin");
    }
  }
}
