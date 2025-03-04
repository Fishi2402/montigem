package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.BasicEvent;
import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;

public final class MethodMonitor {

  private final BasicEvent basicEvent;
  private boolean active = true;

  public static MethodMonitor start(Object object, String methodName) {
    var basicEvent = new BasicEvent(object, methodName);
    StackEventQueue.report(new CallEvent(basicEvent));
    return new MethodMonitor(basicEvent);
  }

  private MethodMonitor(BasicEvent basicEvent) {
    this.basicEvent = basicEvent;
  }

  public Object object() {
    return basicEvent.object();
  }

  public String methodName() {
    return basicEvent.methodName();
  }

  public void reportError(Exception exception) {
    deactivate();
    StackEventQueue.report(new ErrorEvent(basicEvent, exception));
  }

  public void reportReturn(Object returnValue) {
    deactivate();
    StackEventQueue.report(new ReturnEvent(basicEvent, returnValue));
  }

  public void reportReturnVoid() {
    deactivate();
    StackEventQueue.report(new ReturnVoidEvent(basicEvent));
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }
}
