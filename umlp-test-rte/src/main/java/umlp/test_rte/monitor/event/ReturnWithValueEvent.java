package umlp.test_rte.monitor.event;

import java.util.Objects;

public final class ReturnWithValueEvent implements StackEvent {

  private final Object object;
  private final String signature;
  private final Object returnValue;

  public ReturnWithValueEvent(Object object, String signature, Object returnValue) {
    this.object = object;
    this.signature = signature;
    this.returnValue = returnValue;
  }

  @Override
  public Object object() {
    return object;
  }

  @Override
  public String signature() {
    return signature;
  }

  public Object returnValue() {
    return returnValue;
  }

  @Override
  public <T> T matchVariant(StackEventVariantMatcher<T> matcher) {
    return matcher.whenReturnWithValue(this);
  }

  @Override
  public boolean equals(Object object1) {
    if (object1 == null || getClass() != object1.getClass()) return false;
    ReturnWithValueEvent that = (ReturnWithValueEvent) object1;
    return Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(returnValue, that.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, returnValue);
  }

  @Override
  public String toString() {
    return "ReturnWithValueEvent{" + "object=" + object + ", signature='" + signature + '\'' +
           ", returnValue=" + returnValue + '}';
  }
}
