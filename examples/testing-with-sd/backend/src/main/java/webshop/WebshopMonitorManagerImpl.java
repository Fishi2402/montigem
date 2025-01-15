package webshop;

public class WebshopMonitorManagerImpl extends WebshopManagerImpl {

  public static void init() {
    var instance = new WebshopMonitorManagerImpl();
    WebshopManager.setInstance(instance);
  }

  @Override
  protected OrderBuilder _doOrderBuilderFromId(long gemId) {
    return new OrderMonitorBuilder(gemId);
  }
}
