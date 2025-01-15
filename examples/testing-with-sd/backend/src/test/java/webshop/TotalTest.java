package webshop;

import monitor.StackEventQueue;
import monitor.event.CallEvent;
import monitor.event.EndEvent;
import org.junit.jupiter.api.Test;
import totaltest.TotalTestInstantiator;
import umlp.test_rte.ClientSetup;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotalTest extends WebshopSQLDBTestSetup {

  public TotalTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void matchVisible() {
    WebshopMonitorManagerImpl.init(); //TODO this needs to be called in the _init method instead of WebShopManagerImpl.init()

    var testInstances = new TotalTestInstantiator().instantiate();
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = testInstances.getItem();

    assertEquals(0, o.getTotal());

    StackEventQueue.clear();

    o.addItem(item);

    assertEquals(new CallEvent("addItem"), StackEventQueue.poll().orElseThrow());
    assertEquals(new EndEvent("addItem", null), StackEventQueue.poll().orElseThrow());

    assertEquals(6, o.getTotal());
  }
}
