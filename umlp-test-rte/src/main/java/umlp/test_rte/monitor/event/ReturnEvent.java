package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnEvent implements StackEvent {

  private final Object object;
  private final String signature;

  public ReturnEvent(Object object, String signature) {
    this.object = object;
    this.signature = signature;
  }

  @Override
  public Object object() {
    return object;
  }

  @Override
  public String signature() {
    return signature;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  @Override
  public boolean equals(Object object1) {
    if (object1 == null || getClass() != object1.getClass()) return false;
    ReturnEvent that = (ReturnEvent) object1;
    return Objects.equals(object, that.object) && Objects.equals(signature, that.signature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature);
  }

  @Override
  public String toString() {
    return "ReturnEvent{" + "object=" + object + ", signature='" + signature + '\'' + '}';
  }
}
