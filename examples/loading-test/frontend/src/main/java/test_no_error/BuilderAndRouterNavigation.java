package test_no_error;

import loadingtests.LoadingTestsManager;
import umlp.backendrte.common.NotificationScope;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class BuilderAndRouterNavigation extends BuilderAndRouterNavigationTOP {

  public Consumer<Void> navigateToAnotherPage() {
    LoadingTestsManager.deleteAnimal(this.getA().getGemId(), NotificationScope.LOCAL);
    return $ -> Router.navigate("test_update_after_build/Persons");
  }
}
