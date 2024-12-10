package webshop;

import org.junit.jupiter.api.Test;
import totaltest.TotalTestInstantiator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotalTest {

  @Test
  void matchVisible() {
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = new TotalTestInstantiator().instantiateItem();

    assertEquals(0, o.getTotal());

    o.addObserver(new OrderAllObserver(unused -> {

    }));
    o.addItem(item);

    //TODO make sure the add item call was registered by the observers

    assertEquals(6, o.getTotal());
  }
}
