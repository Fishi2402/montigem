package umlp.test_rte.monitor.model.action;

import java.util.Objects;
import java.util.Optional;

public final class Return implements Action {

  private final Optional<String> methodName;
  private final boolean hasReturnValue;
  private final Optional<Object> returnValue;

  public Return(Optional<String> methodName) {
    this(methodName, false, Optional.empty());
  }

  public Return(Optional<String> methodName, Optional<Object> returnValue) {
    this(methodName, true, returnValue);
  }

  private Return(Optional<String> methodName, boolean hasReturnValue, Optional<Object> returnValue) {
    this.methodName = methodName;
    this.hasReturnValue = hasReturnValue;
    this.returnValue = returnValue;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  public Optional<String> methodName() {
    return methodName;
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
    Return aReturn = (Return) object;
    return hasReturnValue == aReturn.hasReturnValue &&
           Objects.equals(methodName, aReturn.methodName) &&
           Objects.equals(returnValue, aReturn.returnValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methodName, hasReturnValue, returnValue);
  }

  @Override
  public String toString() {
    return "Return{" + "methodName=" + methodName + ", hasReturnValue=" + hasReturnValue +
           ", returnValue=" + returnValue + '}';
  }
}
