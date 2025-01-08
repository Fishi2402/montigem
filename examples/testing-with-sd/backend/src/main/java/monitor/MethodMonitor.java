package monitor;

import monitor.event.CallEvent;
import monitor.event.EndEvent;
import monitor.event.ErrorEvent;

public final class MethodMonitor {

  private final String methodName;
  private Exception exception;
  private Object returnValue;

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
    checkActive();
    this.exception = exception;
    StackEventQueue.report(new ErrorEvent(methodName, exception));
  }

  public void reportEnd(Object returnValue) {
    checkActive();
    this.returnValue = returnValue;
    StackEventQueue.report(new EndEvent(methodName, returnValue));
  }

  public void checkActive() {
    if (exception != null || returnValue != null) {
      throw new IllegalStateException("method end has already been reported");
    }
  }
}
