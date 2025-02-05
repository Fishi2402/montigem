package webshop;

import org.junit.jupiter.api.Test;
import totaltest.TotalTestInstantiator;
import umlp.test_rte.ClientSetup;
import umlp.test_rte.monitor.StackEventQueue;
import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.EndEvent;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotalTest extends WebshopSQLDBTestSetup {

  public TotalTest() {
    super(ClientSetup.of(1));
  }

  @Override
  protected void initBackendManager() {
    WebshopMonitorManagerImpl.init();
  }

  @Test
  void matchVisible() {
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
