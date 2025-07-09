package umlp.test_rte.monitor.event;

import java.util.Arrays;
import java.util.Objects;

public final class CallEvent implements StackEvent {

  private final BasicEvent parent;
  private final Object[] params;

  public CallEvent(Object object, String methodName, Object[] params) {
    this(new BasicEvent(object, methodName), params);
  }

  CallEvent(BasicEvent parent, Object[] params) {
    this.parent = parent;
    this.params = params;
  }

  @Override
  public String methodName() {
    return parent.methodName();
  }

  public Object[] params() {
    return params;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenCallEvent(this);
  }

  @Override
  public Object object() {
    return parent.object();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    CallEvent callEvent = (CallEvent) o;
    return Objects.equals(parent, callEvent.parent) && Objects.deepEquals(params, callEvent.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parent, Arrays.hashCode(params));
  }

  @Override
  public String toString() {
    return "CallEvent{" + "parent=" + parent + ", params=" + Arrays.toString(params) + '}';
  }
}
