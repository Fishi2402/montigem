package umlp.test_rte.monitor.model;

import umlp.test_rte.monitor.model.action.Action;
import umlp.test_rte.monitor.model.action.ActivityBarEnd;
import umlp.test_rte.monitor.model.action.Call;
import umlp.test_rte.monitor.model.action.Error;
import umlp.test_rte.monitor.model.action.ExhaustiveActionVariantMatcher;
import umlp.test_rte.monitor.model.action.Return;

import java.util.Optional;
import java.util.function.Supplier;

public final class ExpectedEvent {

  //TODO explicitly support multiple and different kinds of ocl statements

  private final Object source;
  private final Object target;
  private final Action action;
  private final Supplier<Boolean> condition;
  private final String conditionStringRepresentation;

  public ExpectedEvent(Object source,
      Object target,
      Action action,
      Supplier<Boolean> condition,
      String conditionStringRepresentation) {
    this.source = source;
    this.target = target;
    this.action = action;
    this.condition = condition;
    this.conditionStringRepresentation = conditionStringRepresentation;
  }

  public Optional<String> getMethodName() {
    return action.matchVariant(new ExhaustiveActionVariantMatcher<>() {
      @Override
      public Optional<String> whenCall(Call call) {
        return Optional.of(call.methodName());
      }

      @Override
      public Optional<String> whenError(Error error) {
        return error.methodName();
      }

      @Override
      public Optional<String> whenReturn(Return returnAction) {
        return returnAction.methodName();
      }

      @Override
      public Optional<String> whenActivityBarEnd(ActivityBarEnd activityBarEnd) {
        return Optional.of(activityBarEnd.methodName());
      }
    });
  }

  public Object source() {
    return source;
  }

  public Object target() {
    return target;
  }

  public Action action() {
    return action;
  }

  public Supplier<Boolean> condition() {
    return condition;
  }

  public String conditionStringRepresentation() {
    return conditionStringRepresentation;
  }

  @Override
  public String toString() {
    return "ExpectedEvent{" + "source=" + source + ", target=" + target + ", action=" + action +
           ", condition=" + conditionStringRepresentation + '}';
  }
}
