package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

@Deprecated
public final class ReturnAction implements Action {

  private final boolean hasReturnValue;
  private final Optional<Object> returnValue;

  public ReturnAction() {
    this(false, Optional.empty());
  }

  public ReturnAction(Optional<Object> returnValue) {
    this(true, returnValue);
  }

  private ReturnAction(boolean hasReturnValue, Optional<Object> returnValue) {
    this.hasReturnValue = hasReturnValue;
    this.returnValue = returnValue;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  public boolean hasReturnValue() {
    return hasReturnValue;
  }

  public Optional<Object> returnValue() {
    return returnValue;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    ReturnAction that = (ReturnAction) object;
    return hasReturnValue == that.hasReturnValue && Objects.equals(returnValue, that.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hasReturnValue, returnValue);
  }

  @Override
  public String toString() {
    return "ReturnAction{" + "hasReturnValue=" + hasReturnValue + ", returnValue=" + returnValue +
           '}';
  }
}
