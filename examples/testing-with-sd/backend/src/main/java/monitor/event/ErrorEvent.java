package monitor.event;

import java.util.Objects;

public final class ErrorEvent implements StackEvent {

  private final String methodName;
  private final Exception exception;

  public ErrorEvent(String methodName, Exception exception) {
    this.methodName = methodName;
    this.exception = exception;
  }

  @Override
  public String methodName() {
    return methodName;
  }

  public Exception exception() {
    return exception;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    ErrorEvent that = (ErrorEvent) o;
    return Objects.equals(methodName, that.methodName) && Objects.equals(exception, that.exception);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, exception);
  }
}
