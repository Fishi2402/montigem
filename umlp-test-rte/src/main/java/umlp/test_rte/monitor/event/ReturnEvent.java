package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnEvent implements StackEvent {

  private final BasicEvent parent;
  private final Object returnValue;

  public ReturnEvent(Object object, String methodName, Object returnValue) {
    this(new BasicEvent(object, methodName), returnValue);
  }

  public ReturnEvent(BasicEvent parent, Object returnValue) {
    this.parent = parent;
    this.returnValue = returnValue;
  }

  @Override
  public Object object() {
    return parent.object();
  }

  @Override
  public String methodName() {
    return parent.methodName();
  }

  public Object returnValue() {
    return returnValue;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReturnEvent that = (ReturnEvent) o;
    return Objects.equals(parent, that.parent) && Objects.equals(returnValue, that.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parent, returnValue);
  }

  @Override
  public String toString() {
    return "ReturnEvent{" + "parent=" + parent + ", returnValue=" + returnValue + '}';
  }
}
