package webshop;

public class Order extends OrderTOP {
  @Override
  public int getTotal() {
    return getItemsList().stream()
        .mapToInt(item -> item.getAmount() * item.getPerUnitCost())
        .sum();
  }
}
