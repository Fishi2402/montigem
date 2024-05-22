package carrental;

public abstract class RentableItem extends RentableItemTOP {

  public RentableItem() {
  }

  @Override
  public boolean isAvailable() {
    return false;
//     TODO:
//    return this.getRentableStatus() == RentableStatus.AVAILABLE;
  }
}
