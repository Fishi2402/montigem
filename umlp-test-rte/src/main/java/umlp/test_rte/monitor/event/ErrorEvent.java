package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ErrorEvent implements StackEvent {

  private final BasicEvent parent;
  private final Exception exception;

  public ErrorEvent(Object object, String methodName, Exception exception) {
    this(new BasicEvent(object, methodName), exception);
  }

  public ErrorEvent(BasicEvent parent, Exception exception) {
    this.parent = parent;
    this.exception = exception;
  }

  @Override
  public Object object() {
    return parent.object();
  }

  @Override
  public String methodName() {
    return parent.methodName();
  }

  public Exception exception() {
    return exception;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ErrorEvent that = (ErrorEvent) o;
    return Objects.equals(parent, that.parent) && Objects.equals(exception, that.exception);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parent, exception);
  }

  @Override
  public String toString() {
    return "ErrorEvent{" + "parent=" + parent + ", exception=" + exception + '}';
  }
}
