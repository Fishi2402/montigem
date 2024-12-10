package webshop;

import org.junit.jupiter.api.Test;
import totaltest.TotalTestInstantiator;
import umlp.backendrte.common.NotificationScope;
import umlp.test_rte.ClientSetup;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TotalTest extends WebshopSQLDBTestSetup {

  public TotalTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void matchVisible() {
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = new TotalTestInstantiator().instantiateItem();

    assertEquals(0, o.getTotal());

    var obsCalled = new AtomicBoolean();
    o.addObserver(new OrderObserver() {
      @Override
      public void maybeNotifyAddItem(Order order,
                                     long arg,
                                     int indexInList,
                                     NotificationScope scope) {
        assertEquals(o, order);
        obsCalled.set(true);
      }
    });
    o.addItem(item);
    assertTrue(obsCalled.get());

    assertEquals(6, o.getTotal());
  }
}
