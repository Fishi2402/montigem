package gui.admin;

import kitchencompanion.Offer;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class OfferAdmin extends OfferAdminTOP{
  @Override
  public Consumer<Void> navigateToOffer(Offer offer){
    return $ ->{
      Router.navigate("gui", "admin", "OfferEdit",String.valueOf(offer.getGemId()));
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
