package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Interaction;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.action.CallAction;

/**
 * We received a call action and are now waiting for an optional activity bar or ocl statements
 */
public final class CallActionState implements VisitorState, InteractionExtractable {

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

  public StateExtractedInteraction extractInteraction() {
    var awaitActionState = previousState();
    var source = awaitActionState.source();
    var target = awaitActionState.target();
    var interaction = new Interaction(source, target, action);
    var previousState = awaitActionState.previousState();
    return new StateExtractedInteraction(interaction, previousState);
  }
}
