package webshop;

import java.util.Optional;

public class Order extends OrderTOP {
  private static Optional<Order> anInstance = Optional.empty();

  @Override
  public int getTotal() {
    return getItemList().stream()
        .mapToInt(item -> item.getAmount() * item.getPerUnitCost())
        .sum();
  }

  protected void _doFoo() {
    throw new RuntimeException("foo");
  }

  public static Order getAnInstance() {
    if (anInstance.isPresent()) {
      return anInstance.get();
    }

    var order = WebshopManager.orderBuilder().build().orElseThrow();
    anInstance = Optional.of(order);
    return order;
  }

  //here for testing how static methods behave
  public static void foo() {
    getAnInstance()._doFoo();
  }
}
