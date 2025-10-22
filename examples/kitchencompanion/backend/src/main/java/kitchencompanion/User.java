package kitchencompanion;

public class User extends UserTOP {
  @Override
  public double getSum() {
    double sum = 0;
    if (!this.getTalliesList().isEmpty()) {
      for (Tally tally : this.getTalliesList()) {
        sum += tally.getCount() * tally.getOffer().getPrice();
      }
    }
    return sum;
  }
}
