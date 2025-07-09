package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

public final class Error implements Action {

  private final Optional<String> methodName;
  private final Throwable error;

  public Error(Optional<String> methodName, Throwable error) {
    this.methodName = methodName;
    this.error = error;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenError(this);
  }

  public Optional<String> methodName() {
    return methodName;
  }

  public Throwable error() {
    return error;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    Error error1 = (Error) object;
    return Objects.equals(methodName, error1.methodName) && Objects.equals(error, error1.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, error);
  }

  @Override
  public String toString() {
    return "Error{" + "methodName=" + methodName + ", error=" + error + '}';
  }
}
