package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

/**
 * waiting for the target of a send-message interaction
 */
public final class SendMessageAwaitTargetState implements VisitorState {

  private final SendMessageAwaitSourceState previousState;
  private final InteractionEntity source;

  public SendMessageAwaitTargetState(SendMessageAwaitSourceState previousState, InteractionEntity source) {
    this.previousState = previousState;
    this.source = source;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSendMessageAwaitTarget(this);
  }

  public SendMessageAwaitSourceState previousState() {
    return previousState;
  }

  public InteractionEntity source() {
    return source;
  }
}
