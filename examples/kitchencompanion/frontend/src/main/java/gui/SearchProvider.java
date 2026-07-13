package gui;

import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Search;

public abstract class SearchProvider {
  public static Search search;

  public static Search getSearch() {
    if (search == null){
      search = KitchenCompanionManager.searchBuilder()
          .searched(false)
          .searchStr("")
          .build().get();
    }
    return search;
  }
}
