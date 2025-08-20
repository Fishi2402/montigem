package test_no_error;

import umlp.guiutil.Router;

import java.util.function.Consumer;

public class BuilderAndRouterNavigation extends BuilderAndRouterNavigationTOP {

  public Consumer<Void> navigateToAnotherPage() {
    return $ -> Router.navigate("test_update_after_build/Persons");
  }
}
