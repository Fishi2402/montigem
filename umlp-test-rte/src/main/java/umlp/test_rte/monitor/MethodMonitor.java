package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;

public final class MethodMonitor {

  private final String methodName;
  private boolean active = true;

  public static MethodMonitor start(String methodName) {
    StackEventQueue.report(new CallEvent(methodName));
    return new MethodMonitor(methodName);
  }

  private MethodMonitor(String methodName) {
    this.methodName = methodName;
  }

  public String methodName() {
    return methodName;
  }

  public void reportError(Exception exception) {
    deactivate();
    StackEventQueue.report(new ErrorEvent(methodName, exception));
  }

  public void reportReturn(Object returnValue) {
    deactivate();
    StackEventQueue.report(new ReturnEvent(methodName, returnValue));
  }

  public void reportReturnVoid() {
    deactivate();
    StackEventQueue.report(new ReturnVoidEvent(methodName));
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }
}
