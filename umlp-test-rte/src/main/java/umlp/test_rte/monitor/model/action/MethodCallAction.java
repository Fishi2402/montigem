package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

public final class MethodCallAction implements Action {

  private final String methodName;
  private final Optional<Object[]> args;
  private final boolean hasActivityBar;

  public MethodCallAction(String methodName, Optional<Object[]> args, boolean hasActivityBar) {
    this.methodName = methodName;
    this.args = args;
    this.hasActivityBar = hasActivityBar;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenMethodCall(this);
  }

  public String methodName() {
    return methodName;
  }

  public Optional<Object[]> args() {
    return args;
  }

  public boolean hasActivityBar() {
    return hasActivityBar;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    MethodCallAction that = (MethodCallAction) object;
    return hasActivityBar == that.hasActivityBar && Objects.equals(methodName, that.methodName) &&
           Objects.equals(args, that.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, args, hasActivityBar);
  }

  @Override
  public String toString() {
    return "MethodCallAction{" + "methodName='" + methodName + '\'' + ", args=" + args +
           ", hasActivityBar=" + hasActivityBar + '}';
  }
}
