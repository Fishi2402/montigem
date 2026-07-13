package gui.management;

import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Offer;
import kitchencompanion.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AlphabetSearch extends AlphabetSearchTOP {
  public final String[] ALPHABET = {
      "A", "B", "C", "D", "E", "F", "G",
      "H", "I", "J", "K", "L", "M", "N",
      "O", "P", "Q", "R", "S", "T", "U",
      "V", "W", "X", "Y", "Z"
  };

  @Override
  public List<String> getAlphabet() {
    List<String> list = new ArrayList<>();
    for (String alphabet : ALPHABET) {
      list.add(alphabet);
    }
    return list;
  }

  @Override
  public Consumer<Void> setLetterSelection(String str) {
    return $ -> {
      getSearchState().setSearched(true);
      getSearchState().setSearchStr(str);
    };
  }

  @Override
  public Boolean getDisabled(String str) {
    if (isEmptySearch(str)) {
      return true;
    }
    if (getSearchState() != null) {
      return getSearchState().isSearched() && str.compareTo(getSearchState().getSearchStr()) == 0;
    }
    return false;
  }

  private Boolean isEmptySearch(String str) {
    if (isUserOrOffer()) {
      return searchInUser(str);
    } else {
      return searchInOffer(str);
    }
  }

  private boolean searchInOffer(String str) {
    List<Offer> offers = KitchenCompanionManager.getOfferList();
    for (Offer offer : offers) {
      if (offer.getName().startsWith(str) && offer.isInStock()) {
        return false;
      }
    }
    return true;
  }

  private boolean searchInUser(String str) {
    List<User> users = KitchenCompanionManager.getUserList();
    for (User user : users) {
      if (user.getName().startsWith(str) || user.getLastName().startsWith(str)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Consumer<Void> clearLetterSelection() {
    return $ -> {
      getSearchState().setSearched(false);
      getSearchState().setSearchStr("");
    };
  }

  @Override
  public Boolean isDisabled() {
    try {
      return !getSearchState().isSearched();
    }catch(NullPointerException npe) {
      return true;
    }
  }
}
