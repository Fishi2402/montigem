package umlp.test_rte.monitor.event;

import java.util.Objects;

final class BasicEvent {

  /**
   * The object on which the method has been called
   */
  private final Object object;
  private final String methodName;

  public BasicEvent(Object object, String methodName) {
    this.object = object;
    this.methodName = methodName;
  }

  public Object object() {
    return object;
  }

  public String methodName() {
    return methodName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    BasicEvent that = (BasicEvent) o;
    return Objects.equals(methodName, that.methodName) && Objects.equals(object, that.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, object);
  }

  @Override
  public String toString() {
    return "BasicEvent{" + "methodName='" + methodName + '\'' + ", object=" + object + '}';
  }
}
