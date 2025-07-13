package umlp.test_rte.monitor.model;

import umlp.test_rte.monitor.model.action.Action;
import umlp.test_rte.monitor.model.action.ActivityBarEnd;
import umlp.test_rte.monitor.model.action.Call;
import umlp.test_rte.monitor.model.action.Error;
import umlp.test_rte.monitor.model.action.ExhaustiveActionVariantMatcher;
import umlp.test_rte.monitor.model.action.Return;
import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ExpectedEvent {

  private final Object source;
  private final Object target;
  private final Action action;
  private final List<OCLStatement> oclStatements;

  public ExpectedEvent(Object source,
      Object target,
      Action action,
      OCLStatement... oclStatements) {
    this.source = source;
    this.target = target;
    this.action = action;
    this.oclStatements = new ArrayList<>(List.of(oclStatements));
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

  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public String toString() {
    return "ExpectedEvent{" + "source=" + source + ", target=" + target + ", action=" + action +
           ", oclStatements=" + oclStatements + '}';
  }
}
