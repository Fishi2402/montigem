package webshop;

import webshop.test_infrastructure.OrderBuilderMonitor;
import webshop.test_infrastructure.WebshopManagerImplMonitor;

public class WebshopMonitorManagerImpl extends WebshopManagerImplMonitor {

  public static void init() {
    var instance = new WebshopMonitorManagerImpl();
    WebshopManager.setInstance(instance);
  }

  @Override
  public OrderBuilder _doOrderBuilderFromId(long gemId) {
    return new OrderBuilderMonitor(gemId);
  }
}
