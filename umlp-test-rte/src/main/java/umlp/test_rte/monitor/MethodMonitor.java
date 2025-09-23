package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.ConstructorCallEvent;
import umlp.test_rte.monitor.event.ErrorEvent;
import umlp.test_rte.monitor.event.MethodCallEvent;
import umlp.test_rte.monitor.event.ReturnEvent;
import umlp.test_rte.monitor.event.ReturnWithValueEvent;
import umlp.test_rte.monitor.event.StackEvent;

import java.util.Optional;

public final class MethodMonitor {

  private static Optional<InteractionProcessor> activeProcessor = Optional.empty();

  private final Object reportingObject;
  private final String signature;
  private final Optional<InteractionProcessor> processor;
  private boolean active = true;

  public static void setActiveProcessor(InteractionProcessor processor) {
    activeProcessor = Optional.ofNullable(processor);
  }

  public static void removeActiveProcessor() {
    activeProcessor = Optional.empty();
  }

  public static MethodMonitor reportMethodCall(Object object,
      String signature,
      String methodName,
      Object... params) {
    var monitor = new MethodMonitor(object, signature, activeProcessor);
    monitor.process(new MethodCallEvent(object, signature, methodName, params));
    return monitor;
  }

  public static MethodMonitor reportConstructorCall(Object object,
      String signature,
      String className,
      Object... params) {
    var monitor = new MethodMonitor(object, signature, activeProcessor);
    monitor.process(new ConstructorCallEvent(object, signature, className, params));
    return monitor;
  }

  private MethodMonitor(Object reportingObject,
      String signature,
      Optional<InteractionProcessor> processor) {
    this.reportingObject = reportingObject;
    this.signature = signature;
    this.processor = processor;
  }

  public void reportError(Throwable error) {
    deactivate();
    //there is no other way to get the name, of the type, of an arbitrary thrown error
    var errorTypeName = error.getClass().getName();
    process(new ErrorEvent(reportingObject, signature, errorTypeName, error));
  }

  public void reportReturnWithValue(Object returnValue) {
    deactivate();
    process(new ReturnWithValueEvent(reportingObject, signature, returnValue));
  }

  public void reportReturn() {
    deactivate();
    process(new ReturnEvent(reportingObject, signature));
  }

  private void process(StackEvent event) {
    processor.ifPresent(processor -> processor.process(event));
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }
}
