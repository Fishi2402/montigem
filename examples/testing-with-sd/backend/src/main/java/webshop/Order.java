package webshop;

public class Order extends OrderTOP {
  @Override
  public int getTotal() {
    return getItemList().stream()
        .mapToInt(item -> item.getAmount() * item.getPerUnitCost())
        .sum();
  }

  protected void _doFoo() {
    throw new RuntimeException("foo");
  }

  //here for testing how static methods behave
  public static void foo() {
    new Order()._doFoo();
  }
}
