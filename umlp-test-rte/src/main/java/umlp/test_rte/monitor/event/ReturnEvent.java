package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnEvent implements StackEvent {

  private final String methodName;
  private final Object returnValue;

  public ReturnEvent(String methodName, Object returnValue) {
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
    ReturnEvent returnEvent = (ReturnEvent) o;
    return Objects.equals(methodName, returnEvent.methodName) && Objects.equals(returnValue,
                                                                                returnEvent.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, returnValue);
  }

  @Override
  public String toString() {
    return "EndEvent{" + "methodName='" + methodName + '\'' + ", returnValue=" + returnValue + '}';
  }
}
