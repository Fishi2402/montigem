package gui.kitchen;

import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class ExtraUserPanel extends ExtraUserPanelTOP {
  @Override
  public Consumer<Void> navigateTo(User val_$_$0) {
    return $ -> {
      Router.navigate("gui", "management", "OfferAdministrationPage", String.valueOf(val_$_$0.getGemId()));
    };
  }
}
