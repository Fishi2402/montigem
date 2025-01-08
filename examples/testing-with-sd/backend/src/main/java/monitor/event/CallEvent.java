package monitor.event;

import java.util.Objects;

public final class CallEvent implements StackEvent {

  private final String methodName;

  public CallEvent(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public String methodName() {
    return methodName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    CallEvent callEvent = (CallEvent) o;
    return Objects.equals(methodName, callEvent.methodName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(methodName);
  }
}
