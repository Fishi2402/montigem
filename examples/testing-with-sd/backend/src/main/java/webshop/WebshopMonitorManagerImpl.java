package webshop;

import webshop.test_infrastructure.OrderBuilderMonitor;

public class WebshopMonitorManagerImpl extends WebshopManagerImpl {

  public static void init() {
    var instance = new WebshopMonitorManagerImpl();
    WebshopManager.setInstance(instance);
  }

  @Override
  protected OrderBuilder _doOrderBuilderFromId(long gemId) {
    return new OrderBuilderMonitor(gemId);
  }
}
