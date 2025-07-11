package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionEntity;

public final class SendMessageAwaitTarget implements VisitorState {

  private final SendMessageAwaitSource previousState;
  private final InteractionEntity source;

  public SendMessageAwaitTarget(SendMessageAwaitSource previousState, InteractionEntity source) {
    this.previousState = previousState;
    this.source = source;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSendMessageAwaitTarget(this);
  }

  public SendMessageAwaitSource previousState() {
    return previousState;
  }

  public InteractionEntity source() {
    return source;
  }
}
