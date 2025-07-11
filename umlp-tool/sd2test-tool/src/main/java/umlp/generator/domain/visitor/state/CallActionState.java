package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.action.CallAction;

/**
 * We received a call action and are now waiting for an optional activity bar or ocl statements
 */
public final class CallActionState implements VisitorState {

  private final AwaitActionState previousState;
  private final CallAction action;

  public CallActionState(AwaitActionState previousState, CallAction action) {
    this.previousState = previousState;
    this.action = action;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenCallAction(this);
  }

  public AwaitActionState previousState() {
    return previousState;
  }

  public CallAction action() {
    return action;
  }
}
