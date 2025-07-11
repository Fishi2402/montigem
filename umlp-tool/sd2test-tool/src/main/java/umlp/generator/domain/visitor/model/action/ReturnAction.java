package umlp.generator.domain.visitor.model.action;

import java.util.Optional;

public final class ReturnAction implements Action {

  private final Optional<Object> returnValue;
  private final boolean hasReturnValue;

  public ReturnAction(Optional<Object> returnValue, boolean hasReturnValue) {
    this.returnValue = returnValue;
    this.hasReturnValue = hasReturnValue;
  }

  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  public Optional<Object> returnValue() {
    return returnValue;
  }

  public boolean hasReturnValue() {
    return hasReturnValue;
  }
}
