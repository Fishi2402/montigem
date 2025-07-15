package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

public final class ErrorAction implements Action {

  private final String errorTypeName;
  private final Optional<Throwable> error;

  public ErrorAction(String errorTypeName, Optional<Throwable> error) {
    this.errorTypeName = errorTypeName;
    this.error = error;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenError(this);
  }

  @Deprecated(forRemoval = true)
  public Throwable getError() {
    return error.orElseThrow();
  }

  public String errorTypeName() {
    return errorTypeName;
  }

  public Optional<Throwable> error() {
    return error;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ErrorAction that = (ErrorAction) object;
    return Objects.equals(errorTypeName, that.errorTypeName) && Objects.equals(error, that.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorTypeName, error);
  }

  @Override
  public String toString() {
    return "ErrorAction{" + "errorType='" + errorTypeName + '\'' + ", error=" + error + '}';
  }
}
