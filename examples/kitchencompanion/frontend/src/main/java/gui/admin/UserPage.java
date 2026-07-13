package gui.admin;

import gui.SearchProvider;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Search;

import java.util.List;
import java.util.function.Consumer;

public class UserPage extends UserPageTOP {

  @Override
  public void init() {
    super.init();
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
