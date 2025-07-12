package umlp.generator.domain.visitor.model.action;

import java.util.Optional;

public final class ReturnAction implements Action {

  private final Optional<String> returnValue;
  private final boolean hasReturnValue;

  public ReturnAction(Optional<String> returnValue, boolean hasReturnValue) {
    this.returnValue = returnValue;
    this.hasReturnValue = hasReturnValue;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  public Optional<String> returnValue() {
    return returnValue;
  }

  public boolean hasReturnValue() {
    return hasReturnValue;
  }

  @Override
  public String toString() {
    return "ReturnAction{" + "returnValue=" + returnValue + ", hasReturnValue=" + hasReturnValue +
           '}';
  }
}
