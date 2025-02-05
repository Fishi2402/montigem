package webshop;

import umlp.test_rte.monitor.MethodMonitor;

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
    var monitor = MethodMonitor.start("addItem");
    try {
      super.addItem(o);
      monitor.reportEnd(null);
      //TODO add a special reporting method for void case
      //returning res does not work with void
    } catch (Exception e) {
      monitor.reportError(e);
      throw e;
    }
  }
}
