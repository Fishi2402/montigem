package gui.admin;


import app.AppRouter;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class UserEdit extends UserEditTOP {
  @Override
  public void init() {
    super.init();
    getUser().endPurchase();
    if (!LoginServiceFront.getLoggedIn()) {
      Router.navigate("gui", "admin", "LoginPage");
    }
  }

  @Override
  public Consumer<Void> confirm(User u) {
    return $ -> {
      if (u.isValid()) {
        AppRouter.navigateToGuiAdminAdmin();
      }
    };
  }

  @Override
  public Consumer<Void> deleteAndNavigate(User user) {
    return $ -> {
      KitchenCompanionManager.deleteUser(user.getGemId());
      Router.navigate("gui", "admin", "UserPage");
    };
  }

}
