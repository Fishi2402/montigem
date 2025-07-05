package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class CallEvent implements StackEvent {

  private final BasicEvent parent;

  public CallEvent(Object object, String methodName) {
    this(new BasicEvent(object, methodName));
  }

  CallEvent(BasicEvent parent) {
    this.parent = parent;
  }

  @Override
  public String methodName() {
    return parent.methodName();
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
    if (o == null || getClass() != o.getClass()) return false;
    CallEvent callEvent = (CallEvent) o;
    return Objects.equals(parent, callEvent.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(parent);
  }

  @Override
  public String toString() {
    return "CallEvent{" + "parent=" + parent + '}';
  }
}
