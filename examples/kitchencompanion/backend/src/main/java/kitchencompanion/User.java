package kitchencompanion;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class User extends UserTOP {
  private static String pw = "admin1234";

  public Double calculateSum() {
    Double sum = 0.;
    for (Tally t : getTalliesList()) {
      sum += t.getCount() * t.getOffer().getPrice();
    }
    for (Tally t : getPrevTalliesList()) {
      sum += t.getCount() * t.getOffer().getPrice();
    }
    return sum;
  }

  public void setSum() {
    this._sum = calculateSum();
  }

  public void sortPrevTallies() {
    List<Tally> list = getPrevTalliesList();
    if (list == null || list.isEmpty()) {
      return;
    }
    int length = list.size();
    for (int i = 0; i < length - 1; i++) {
      for (int j = 0; j < length - i - 1; j++) {
        Tally currentTally = list.get(j);
        Tally nextTally = list.get(j + 1);
        if (currentTally.getCount() < nextTally.getCount()) {
          list.set(j, nextTally);
          list.set(j + 1, currentTally);
        }
        else if (currentTally.getCount() == nextTally.getCount() ||
            String.valueOf(currentTally.getCount()).equals(String.valueOf(nextTally.getCount()))) {
          int typeCompare = currentTally.getOffer().getProductType().name()
              .compareToIgnoreCase(nextTally.getOffer().getProductType().name());
          if (typeCompare > 0) {
            list.set(j, nextTally);
            list.set(j + 1, currentTally);
          }
          else if (typeCompare == 0) {
            int nameCompare = currentTally.getOffer().getName().compareToIgnoreCase(nextTally.getOffer().getName());
            if (nameCompare > 0) {
              list.set(j, nextTally);
              list.set(j + 1, currentTally);
            }
          }
        }
      }
    }
  }
    public static void setPw (String pw){
      User.pw = pw;
    }

    public static String getPw () {
      return User.pw;
    }


    public void removeTally ( boolean adminRights, Offer offer){
      Tally tally = tallyExists(offer);
      int curTallyCount = tally.getCount();
      if (curTallyCount > 0 || adminRights) {
        tally.setCount(curTallyCount - 1);
      }
    }

    /**
     * Creates a monthly receipt with all relevant values;
     * only generated if orders and/or debt exists;
     * carries over unpaid debt from the previous month.
     */
    public void endMonth () {
      endPurchase();
      sortPrevTallies();
      if (hasOrderedTallies() || !isPaid()) {
        setSum();
        double sum = getSum();
        double balance = getBalance();
        double debt = 0.;
        double amountPaid = 0.;
        boolean paid = false;

        if (!isPaid()) {
          Receipt r = getReceiptsList().get(0);
          debt = r.getSum() + r.getDebt() - r.getPastBalance();
        }
        if (sum + debt <= getBalance()) {
          paid = true;
          setBalance(getBalance() - (sum + debt));
          amountPaid = balance;
        } else {
          setBalance(0.);
        }
        Receipt receipt = KitchenCompanionManager.receiptBuilder()
            .sum(sum)
            .pastBalance(balance)
            .debt(debt)
            .amountPaid(amountPaid)
            .confirmed(paid)
            .paid(paid)
            .date(LocalDate.now().minusMonths(1))
            .build()
            .get();

        for (Tally t : getPrevTalliesList()) {
          receipt.addPastTallies(
              KitchenCompanionManager.tallyBuilder()
                  .count(t.getCount())
                  .offer(t.getOffer())
                  .build()
                  .get()
          );
          t.setCount(0);
        }
        addReceipt(receipt);
      }
    }

    private void createReceipt ( double sum, double balance, double debt, double amountPaid,
    boolean paid){
      Receipt receipt = KitchenCompanionManager.receiptBuilder()
          .sum(sum)
          .pastBalance(balance)
          .debt(debt)
          .confirmed(paid)
          .amountPaid(amountPaid)
          .paid(isPaid())
          .date(LocalDate.now())
          .build()
          .get();
      for (Tally t : getPrevTalliesList()) {
        receipt.addPastTallies(
            KitchenCompanionManager.tallyBuilder()
                .count(t.getCount())
                .offer(t.getOffer())
                .build()
                .get()
        );
      }
      addReceipt(receipt);
    }

    public Boolean hasOrderedTallies () {
      for (Tally t : getPrevTalliesList()) {
        if (t.getCount() > 0) {
          return true;
        }
      }
      return false;
    }

    /**
     * This Method Fills The Tallies list with missing offers so newly added offers become visible
     */
    public void fillTallies () {
      for (Offer o : KitchenCompanionManager.getOfferList()) {
        if (o.isInStock()) {
          addOfferToList(getTalliesList(), o);
          addOfferToList(getPrevTalliesList(), o);
        }
      }
    }

    public void endPurchase () {
      for (Tally prev : getPrevTalliesList()) {
        for (Tally curr : getTalliesList()) {
          if (prev.getOffer().getName().compareToIgnoreCase(curr.getOffer().getName()) == 0) {
            prev.setCount(prev.getCount() + curr.getCount());
            curr.setCount(0);
          }
        }
      }
    }

    public void addTally (Offer offer){
      Tally tally = tallyExists(offer);
      tally.setCount(tally.getCount() + 1);
    }

    public void addReceipt (Receipt receipt){
      getReceiptsList().add(0, receipt);
    }

    public Double getPastSum () {
      if (getReceiptsList().isEmpty()) {
        return 0.;
      }
      return getReceiptsList().get(0).getSum();
    }


    public Boolean isValid () {
      if ((!(this.getName() + " " + this.getLastName()).equals("")
          && this.getNickname() != "") && isEmailValid() && this.getRole() != null) {
        return true;
      }
      return false;
    }

    public boolean isEmailValid () {
      if (_email.contains("@")) {
        String[] parts = _email.split("@");
        if (parts.length == 2) {
          String local = parts[0];
          String domain = parts[1];
          if (domain.contains(".") && !local.isEmpty() && !domain.startsWith(".") && !domain.endsWith(".")) {
            return true;
          }
        }
      }
      return false;
    }

    // Private Methods
    private void addOfferToList (List < Tally > list, Offer offer){
      if (!offerExists(list, offer)) {
        list.add(KitchenCompanionManager.tallyBuilder().offer(offer).count(0).build().get());
      }
    }

    private boolean offerExists (List < Tally > tallyList, Offer offer){
      for (Tally t : tallyList) {
        if (t.getOffer().getGemId() == offer.getGemId()) {
          return true;
        }
      }
      return false;
    }

    /**
     * Will never be null because of the way we fill the tallies list,
     * but will return null if there is a different error
     *
     * @param offer
     * @return
     */
    private Tally tallyExists (Offer offer){
      Tally found = null;
      for (Tally t : getTalliesList()) {
        if (t.getOffer().getGemId() == offer.getGemId()) {
          found = t;
          break;
        }
      }
      return found;
    }

    // static methods


    public static List<User> getUsersList () {
      return KitchenCompanionManager.getUserList();
    }

    public Boolean isPaid () {
      if (getReceiptsList() != null && !getReceiptsList().isEmpty()) {
        return getReceiptsList().get(0).isPaid();
      } else {
        return true;
      }
    }
    public String getFormatedName(){
      if(this.getName() == "" || this.getLastName() == ""){
        return this.getName() + this.getLastName();
      } else{
        return this.getLastName() + ", " + this.getName();
      }
    }
}
