package gui.kitchen;

import de.se_rwth.commons.logging.Log;
import gui.SearchProvider;
import gui.admin.LoginPage;
import gui.admin.LoginServiceFront;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Search;
import kitchencompanion.User;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;
import java.util.function.Consumer;


@Component
public class KitchenPage extends KitchenPageTOP {

  @Override
  public void init() {
    super.init();
    LoginServiceFront.setLoggedIn(false);
  }

  @Override
  protected void initSearchState() {
    super.initSearchState();
    setSearchState(SearchProvider.getSearch());
  }

  @Override
  public void destroy() {
    super.destroy();
    KitchenCompanionManager.deleteSearch(getSearchState().getGemId());
    SearchProvider.search = null;
  }
}

