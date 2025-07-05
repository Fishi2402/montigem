package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;
import umlp.test_rte.monitor.event.StackEvent;

public final class MethodMonitor {

  //TODO add infrastructure to set a single stack processor instance as active

  private final StackEvent event;
  private boolean active = true;

  public static MethodMonitor start(Object object, String methodName) {
    var event = new CallEvent(object, methodName);
    StackEventQueue.report(event);
    return new MethodMonitor(event);
  }

  private MethodMonitor(StackEvent event) {
    this.event = event;
  }

  public Object object() {
    return event.object();
  }

  public String methodName() {
    return event.methodName();
  }

  public void reportError(Exception exception) {
    deactivate();
    StackEventQueue.report(new ErrorEvent(object(), methodName(), exception));
  }

  public void reportReturn(Object returnValue) {
    deactivate();
    StackEventQueue.report(new ReturnEvent(object(), methodName(), returnValue));
  }

  public void reportReturnVoid() {
    deactivate();
    StackEventQueue.report(new ReturnVoidEvent(object(), methodName()));
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }
}
