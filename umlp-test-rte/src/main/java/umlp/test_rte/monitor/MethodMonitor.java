package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;
import umlp.test_rte.monitor.event.StackEvent;

import java.util.Optional;

public final class MethodMonitor {

  private static Optional<StackEventProcessor> activeProcessor = Optional.empty();

  private final StackEvent callEvent;
  private final Optional<StackEventProcessor> processor;
  private boolean active = true;

  public static void setActiveStackEventProcessor(StackEventProcessor processor) {
    activeProcessor = Optional.of(processor);
  }

  public static void removeActiveStackEventProcessor() {
    activeProcessor = Optional.empty();
  }

  public static MethodMonitor start(Object object, String methodName, Object... params) {
    var callEvent = new CallEvent(object, methodName, params);
    activeProcessor.ifPresent(processor -> processor.process(callEvent));
    return new MethodMonitor(callEvent, activeProcessor);
  }

  private MethodMonitor(StackEvent callEvent, Optional<StackEventProcessor> processor) {
    this.callEvent = callEvent;
    this.processor = processor;
  }

  public Object object() {
    return callEvent.object();
  }

  public String methodName() {
    return callEvent.methodName();
  }

  public void reportError(Exception exception) {
    deactivate();
    processor.ifPresent(processor -> processor.process(new ErrorEvent(object(),
        methodName(),
        exception)));
  }

  public void reportReturn(Object returnValue) {
    deactivate();
    processor.ifPresent(processor -> processor.process(new ReturnEvent(object(),
        methodName(),
        returnValue)));
  }

  public void reportReturnVoid() {
    deactivate();
    processor.ifPresent(processor -> processor.process(new ReturnVoidEvent(object(),
        methodName())));
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }
}
