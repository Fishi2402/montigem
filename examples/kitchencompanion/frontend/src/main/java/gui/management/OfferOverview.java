package gui.management;

import gui.SearchProvider;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Offer;
import kitchencompanion.Search;
import kitchencompanion.Tally;
import kitchencompanion.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static def.dom.Globals.setInterval;

public class OfferOverview extends OfferOverviewTOP {

  @Override
  public void init() {
    super.init();
    getUser().fillTallies();
    getUser().endPurchase();
    getUser().setSum();
    getUser().sortPrevTallies();
    SearchProvider.getSearch();
  }

  @Override
  public Boolean search(Offer offer) {
    return Search.offerSearch(SearchProvider.getSearch(), offer, isInStock(offer));
  }

  @Override
  public Search getSearchState() {
    return SearchProvider.getSearch();
  }

  @Override
  public List<Offer> getOfferList(User user) {
    ArrayList<Offer> offers = new ArrayList<>();
    List<Tally> list;
    if(user.hasOrderedTallies() || (user.getReceiptsList().isEmpty())){
      list = user.getPrevTalliesList();
    }
    else{
      list = user.getReceiptsList().get(0).getPastTalliesList();
    }
    for (Tally t : list) {
      offers.add(t.getOffer());
    }

    return offers;
  }

  @Override
  public String getTargetAddress(User user) {
    return "/gui/management/Subtotal/"+String.valueOf(user.getGemId());
  }

  public Boolean isInStock(Offer offer) {
    return offer.isInStock();
  }

  @Override
  public void destroy() {
    super.destroy();
    KitchenCompanionManager.deleteSearch(SearchProvider.getSearch().getGemId());
    SearchProvider.search = null;
  }

  @Override
  public String getName(User user){
    return user.getFormatedName();
  }

  @Override
  public String getTallyCount(Offer offer) {
    int tallyCount = 0;
    for(Tally t: getUser().getPrevTalliesList()){
      if(t.getOffer().getName().equals(offer.getName())) {
        tallyCount = t.getCount();
        break;
      }
    }
    return String.valueOf(tallyCount);
  }
}
