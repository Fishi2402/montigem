package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

public final class Call implements Action {

  private final String methodName;
  private final Optional<Object[]> args;

  public Call(String methodName, Optional<Object[]> args) {
    this.methodName = methodName;
    this.args = args;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenCall(this);
  }

  public String methodName() {
    return methodName;
  }

  public Optional<Object[]> args() {
    return args;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    Call call = (Call) object;
    return Objects.equals(methodName, call.methodName) && Objects.equals(args, call.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, args);
  }

  @Override
  public String toString() {
    return "Call{" + "methodName='" + methodName + '\'' + ", args=" + args + '}';
  }
}
