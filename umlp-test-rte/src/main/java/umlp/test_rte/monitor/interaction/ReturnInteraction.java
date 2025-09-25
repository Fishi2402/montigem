package umlp.test_rte.monitor.interaction;

import java.util.Objects;
import java.util.Optional;

public final class ReturnInteraction implements Interaction {

  private final Object object;
  private final String signature;
  private final Optional<ReturnValue> returnValue;
  private final boolean endsCurrentActivityBar;

  public ReturnInteraction(Object object, String signature, Optional<ReturnValue> returnValue,
      boolean endsCurrentActivityBar) {
    this.object = object;
    this.signature = signature;
    this.returnValue = returnValue;
    this.endsCurrentActivityBar = endsCurrentActivityBar;
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

  public Optional<ReturnValue> returnValue() {
    return returnValue;
  }

  public boolean endsCurrentActivityBar() {
    return endsCurrentActivityBar;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ReturnInteraction that = (ReturnInteraction) o;
    return endsCurrentActivityBar == that.endsCurrentActivityBar &&
           Objects.equals(object, that.object) && Objects.equals(signature, that.signature) &&
           Objects.equals(returnValue, that.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, signature, returnValue, endsCurrentActivityBar);
  }

  @Override
  public String toString() {
    return "ReturnInteraction{" + "object=" + object + ", signature='" + signature + '\'' +
           ", returnValue=" + returnValue + ", endsCurrentActivityBar=" + endsCurrentActivityBar +
           '}';
  }
}
