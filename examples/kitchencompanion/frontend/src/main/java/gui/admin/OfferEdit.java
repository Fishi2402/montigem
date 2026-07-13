package gui.admin;

import umlp.guiutil.Router;

import java.util.function.Consumer;

public class OfferEdit extends OfferEditTOP{
  @Override
  public Consumer<Void> navigateBack(){
    return $ -> {
      Router.navigate("gui","admin","OfferAdmin");
    };
  }
  @Override
  public void init() {
    super.init();
    if(!LoginServiceFront.getLoggedIn()){
      Router.navigate("gui", "admin", "LoginPage");
    }
  }
}
