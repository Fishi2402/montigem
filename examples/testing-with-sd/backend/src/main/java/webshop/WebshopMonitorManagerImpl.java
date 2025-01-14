package webshop;

public class WebshopMonitorManagerImpl extends WebshopManagerImpl {
  @Override
  protected OrderBuilder _doOrderBuilderFromId(long gemId) {
    return new OrderMonitorBuilder(gemId);
  }
}
