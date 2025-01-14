package webshop;

import monitor.MethodMonitor;

public class OrderMonitor extends OrderBuilder {
  public OrderMonitor(long gemId) {
    super(gemId);
  }

  @Override
  public int getTotal() {
    var monitor = MethodMonitor.start("getTotal");
    try {
      var res = super.getTotal();
      monitor.reportEnd(res);
      return res;
    } catch (Exception e) {
      monitor.reportError(e);
      throw e;
    }
  }

  @Override
  public void addItem(Item o) {
    var monitor = MethodMonitor.start("getTotal");
    try {
      super.addItem(o);
      monitor.reportEnd(null);
      //returning res does not work with void
    } catch (Exception e) {
      monitor.reportError(e);
      throw e;
    }
  }
}
