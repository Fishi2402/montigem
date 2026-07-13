package gui.management;

import kitchencompanion.Offer;
import kitchencompanion.Tally;

import java.util.function.Consumer;

public class OfferCard extends OfferCardTOP {
  @Override
  public Consumer<Void> addTally(Offer offer) {
    return $ -> {
      getUser().addTally(offer);
      setTallyCount(offer);
    };
  }

  @Override
  public Consumer<Void> removeTally(Offer offer) {
    return $ -> {
      getUser().removeTally(isAdminRights(), offer);
      setTallyCount(offer);
    };
  }

  public void setTallyCount(Offer offer) {
    int tallyCount = 0;
    for (Tally t : getUser().getTalliesList()) {
      if (t.getOffer().getName().equals(offer.getName())) {
        tallyCount = t.getCount();
        break;
      }
    }
    String prevTalliesCount = getPrevTallies(offer);
    if (prevTalliesCount.compareTo("0") == 0 && tallyCount == 0) {
      setCount("" + 0);
    } else if (prevTalliesCount.compareTo("0") == 0 && tallyCount > 0) {
      setCount("" + tallyCount);
    } else if (prevTalliesCount.compareTo("0") != 0 && tallyCount == 0) {
      setCount(prevTalliesCount);
    } else {
      setCount(prevTalliesCount + " " + "+" + "" + tallyCount);
    }
  }

  private String getPrevTallies(Offer offer) {
    int tallyCount = 0;
    for (Tally t : getUser().getPrevTalliesList()) {
      if (t.getOffer().getName().equals(offer.getName())) {
        tallyCount = t.getCount();
        break;
      }
    }
    return "" + tallyCount;
  }
}
