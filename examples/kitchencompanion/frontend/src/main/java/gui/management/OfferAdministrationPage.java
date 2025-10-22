package gui.management;

import de.se_rwth.commons.logging.Log;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Offer;
import kitchencompanion.Tally;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static def.dom.Globals.clearInterval;
import static def.dom.Globals.setInterval;

public class OfferAdministrationPage extends OfferAdministrationPageTOP {

  // Timer
  private double timerInterval;
  private double passedSeconds;

  @Override
  public void init() {
    super.init();
    clearInterval(this.timerInterval);
    this.passedSeconds = 0.0;
    Consumer<Void> lambda = $ -> {
      this.passedSeconds = this.passedSeconds + 0.1;
      if (passedSeconds > 120) {
        clearInterval(this.timerInterval);
        Log.warn("User timed out go back to main page");
        Router.navigate("gui", "kitchen", "KitchenPage");
      }
    };
    this.timerInterval = setInterval(lambda, 100);
  }

  @Override
  public String getPriceInEuro(Double val_$_$0) {
    return String.valueOf(val_$_$0).replace(".", ",") + " €";
  }

  @Override
  public String getTally(User user, Offer offer) {
    int tallyCount = 0;
    Optional<Tally> tally = Optional.empty();
    for (Tally t : user.getTalliesList()) {
      if (t.getOffer().getName().equals(offer.getName())) {
        tally = Optional.of(t);
        break;
      }
    }
    if (tally.isPresent()) {
      tallyCount = tally.get().getCount();
    }
    return String.valueOf(tallyCount);
  }

  @Override
  public Consumer<Void> addTally(User user, Offer offer) {
    Optional<Tally> tally = findTally(user.getTalliesList(), offer.getName());
    return $ -> {
      if (tally.isPresent()) {
        int curTallyCount = tally.get().getCount();
        tally.get().setCount(curTallyCount + 1);
      }
      else {
        user.addTallies(KitchenCompanionManager.tallyBuilder()
            .offer(offer)
            .count(1)
            .build().get()
        );
      }
    };
  }

  @Override
  public Consumer<Void> removeTally(User user, Offer offer) {
    Optional<Tally> tally = findTally(user.getTalliesList(), offer.getName());
    return $ -> {
      if (tally.isPresent()) {
        int curTallyCount = tally.get().getCount();
        if (curTallyCount > 0) {
          tally.get().setCount(curTallyCount - 1);
        }
      }
    };
  }

  private Optional<Tally> findTally(List<Tally> tallyList, String offerName) {
    Optional<Tally> found = Optional.empty();
    for (Tally t : tallyList) {
      if (t.getOffer().getName().equals(offerName)) {
        found = Optional.of(t);
        break;
      }
    }
    return found;
  }
}
