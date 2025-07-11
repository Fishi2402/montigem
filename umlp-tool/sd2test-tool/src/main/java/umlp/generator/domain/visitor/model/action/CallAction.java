package umlp.generator.domain.visitor.model.action;

import umlp.generator.domain.visitor.model.ActivityBar;

import java.util.List;
import java.util.Optional;

public final class CallAction implements Action {

  private final String methodName;
  private final boolean isStatic;
  private final boolean isTrigger;
  private final Optional<List<String>> argumentExpressions;
  private Optional<ActivityBar> activityBar = Optional.empty();

  public CallAction(String methodName,
      boolean isStatic,
      boolean isTrigger,
      Optional<List<String>> argumentExpressions) {
    this.methodName = methodName;
    this.isStatic = isStatic;
    this.isTrigger = isTrigger;
    this.argumentExpressions = argumentExpressions;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenCall(this);
  }

  public String methodName() {
    return methodName;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public boolean isTrigger() {
    return isTrigger;
  }

  public Optional<List<String>> argumentExpressions() {
    return argumentExpressions;
  }

  public Optional<ActivityBar> activityBar() {
    return activityBar;
  }

  public CallAction activityBar(Optional<ActivityBar> activityBar) {
    this.activityBar = activityBar;
    return this;
  }
}
