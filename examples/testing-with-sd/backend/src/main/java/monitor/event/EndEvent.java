package monitor.event;

import java.util.Objects;

public final class EndEvent implements StackEvent {

  private final String methodName;
  private final Object returnValue;

  public EndEvent(String methodName, Object returnValue) {
    this.methodName = methodName;
    this.returnValue = returnValue;
  }

  @Override
  public String methodName() {
    return methodName;
  }

  public Object returnValue() {
    return returnValue;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    EndEvent endEvent = (EndEvent) o;
    return Objects.equals(methodName, endEvent.methodName) && Objects.equals(returnValue,
        endEvent.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, returnValue);
  }
}
