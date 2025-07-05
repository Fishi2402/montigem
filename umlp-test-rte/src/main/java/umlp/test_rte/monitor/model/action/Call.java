package umlp.test_rte.monitor.model.action;

import java.util.Arrays;
import java.util.Objects;

public final class Call implements Action {

  private final String methodName;
  private final Object[] args;

  public Call(String methodName, Object[] args) {
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

  public Object[] args() {
    return args;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Call call = (Call) o;
    return Objects.equals(methodName, call.methodName) && Objects.deepEquals(args, call.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, Arrays.hashCode(args));
  }

  @Override
  public String toString() {
    return "Call{" + "methodName='" + methodName + '\'' + ", args=" + Arrays.toString(args) + '}';
  }
}
