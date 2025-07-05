package umlp.test_rte.monitor.model.action;

import java.util.Optional;

public final class Return implements Action {

  private final String methodName;
  private final boolean hasReturnValue;
  private final Optional<Object> returnValue;

  public Return(String methodName) {
    this(methodName, false, Optional.empty());
  }

  public Return(String methodName, Optional<Object> returnValue) {
    this(methodName, true, returnValue);
  }

  private Return(String methodName, boolean hasReturnValue, Optional<Object> returnValue) {
    this.methodName = methodName;
    this.hasReturnValue = hasReturnValue;
    this.returnValue = returnValue;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }
}
