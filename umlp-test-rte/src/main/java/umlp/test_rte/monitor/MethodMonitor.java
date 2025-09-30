package umlp.test_rte.monitor;

import umlp.test_rte.monitor.interaction.ConstructorCallInteraction;
import umlp.test_rte.monitor.interaction.Interaction;
import umlp.test_rte.monitor.interaction.MethodCallInteraction;
import umlp.test_rte.monitor.interaction.ReturnInteraction;
import umlp.test_rte.monitor.interaction.ReturnValue;
import umlp.test_rte.monitor.interaction.ThrowInteraction;

import java.util.Optional;
import java.util.function.Function;

public final class MethodMonitor {

  private static Optional<InteractionProcessor> activeProcessor = Optional.empty();

  private final Object reportingObject;
  private final String signature;
  private final Optional<ReportingContext> context;
  private boolean active = true;

  private MethodMonitor(Object reportingObject,
                        String signature,
                        Optional<ReportingContext> context) {
    this.reportingObject = reportingObject;
    this.signature = signature;
    this.context = context;
  }

  public static void setActiveProcessor(InteractionProcessor processor) {
    MethodMonitor.activeProcessor = Optional.ofNullable(processor);
  }

  public static void removeActiveProcessor() {
    MethodMonitor.activeProcessor = Optional.empty();
  }

  public static MethodMonitor reportMethodCall(Object object,
                                               String signature,
                                               String methodName,
                                               Object... params) {
    var interaction = new MethodCallInteraction(object, signature, methodName, params);
    return maybeReport(object, signature, interaction);
  }

  public static MethodMonitor reportConstructorCall(Object object,
                                                    String signature,
                                                    String className,
                                                    Object... params) {
    var interaction = new ConstructorCallInteraction(object, signature, className, params);
    return maybeReport(object, signature, interaction);
  }

  private static MethodMonitor maybeReport(Object object,
                                           String signature,
                                           Interaction interaction) {
    var context = activeProcessor.map(it -> {
      var instructions = it.process(interaction);
      return new ReportingContext(it, instructions);
    });
    return new MethodMonitor(object, signature, context);
  }

  public void reportError(Throwable error) {
    maybeReport(instructions -> {
      //there is no other way to get the name, of the type, of an arbitrary thrown error
      var errorTypeName = error.getClass().getName();
      var endsCurrentActivityBar = instructions.reportActivityEnd();
      return new ThrowInteraction(reportingObject,
                                  signature,
                                  errorTypeName,
                                  error,
                                  endsCurrentActivityBar);
    });
  }

  public void reportReturnWithValue(Optional<Object> value) {
    maybeReport(instructions -> {
      var endsCurrentActivityBar = instructions.reportActivityEnd();
      var returnValue = Optional.of(new ReturnValue(value));
      return new ReturnInteraction(reportingObject, signature, returnValue, endsCurrentActivityBar);
    });
  }

  public void reportReturn() {
    maybeReport(instructions -> {
      var endsCurrentActivityBar = instructions.reportActivityEnd();
      var returnValue = Optional.<ReturnValue>empty();
      return new ReturnInteraction(reportingObject, signature, returnValue, endsCurrentActivityBar);
    });
  }

  private void maybeReport(Function<MonitorInstructions, Interaction> interactionSupplier) {
    deactivate();

    context.ifPresent(context -> {
      var interaction = interactionSupplier.apply(context.instructions());
      context.processor().process(interaction);
    });
  }

  private void deactivate() {
    if (!active) {
      throw new IllegalStateException("method end has already been reported");
    }

    this.active = false;
  }

  private static final class ReportingContext {

    private final InteractionProcessor processor;
    private final MonitorInstructions instructions;

    public ReportingContext(InteractionProcessor processor, MonitorInstructions instructions) {
      this.processor = processor;
      this.instructions = instructions;
    }

    public InteractionProcessor processor() {
      return processor;
    }

    public MonitorInstructions instructions() {
      return instructions;
    }
  }
}
