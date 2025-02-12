package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnVoidEvent implements StackEvent {
  private final String methodName;

  public ReturnVoidEvent(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public String methodName() {
    return methodName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReturnVoidEvent that = (ReturnVoidEvent) o;
    return Objects.equals(methodName, that.methodName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(methodName);
  }

  @Override
  public String toString() {
    return "ReturnVoidEvent{" + "methodName='" + methodName + '\'' + '}';
  }
}
