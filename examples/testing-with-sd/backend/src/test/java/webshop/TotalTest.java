package webshop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotalTest {

  @Test
  void matchVisible() {
    //TODO use objects from generated instantiator
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = WebshopManager.itemBuilder().amount(2).perUnitCost(3).build().orElseThrow();

    assertEquals(0, o.getTotal());

    o.addObserver(new OrderAllObserver(unused -> {

    }));
    o.addItem(item);

    //TODO make sure the add item call was registered by the observers

    assertEquals(6, o.getTotal());
  }
}
