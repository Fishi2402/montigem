package umlp.test_rte.monitor.model.action;

import java.util.Objects;

public final class Error implements Action {

  private final String methodName;
  private final Throwable error;

  public Error(String methodName, Throwable error) {
    this.methodName = methodName;
    this.error = error;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenError(this);
  }

  public String methodName() {
    return methodName;
  }

  public Throwable error() {
    return error;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Error error1 = (Error) o;
    return Objects.equals(methodName, error1.methodName) && Objects.equals(error, error1.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, error);
  }

  @Override
  public String toString() {
    return "Error{" + "methodName='" + methodName + '\'' + ", error=" + error + '}';
  }
}
