package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnVoidEvent implements StackEvent {
  private final BasicEvent parent;

  public ReturnVoidEvent(Object object, String methodName) {
    this(new BasicEvent(object, methodName));
  }

  ReturnVoidEvent(BasicEvent parent) {
    this.parent = parent;
  }

  @Override
  public Object object() {
    return parent.object();
  }

  @Override
  public String methodName() {
    return parent.methodName();
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenReturnVoidEvent(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReturnVoidEvent that = (ReturnVoidEvent) o;
    return Objects.equals(parent, that.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(parent);
  }

  @Override
  public String toString() {
    return "ReturnVoidEvent{" + "parent=" + parent + '}';
  }
}
