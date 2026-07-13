package gui.admin;

import kitchencompanion.Offer;
import kitchencompanion.Tally;
import kitchencompanion.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class UserOfferAdmin extends UserOfferAdminTOP {
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
