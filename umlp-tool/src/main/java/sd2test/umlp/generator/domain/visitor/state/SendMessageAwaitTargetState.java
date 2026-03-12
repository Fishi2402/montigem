package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.interaction.Actor;

/**
 * waiting for the target of a send-message interaction
 */
public final class SendMessageAwaitTargetState implements VisitorState {

  private final SendMessageAwaitSourceState previousState;
  private final Actor source;

  public SendMessageAwaitTargetState(SendMessageAwaitSourceState previousState, Actor source) {
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

  public Actor source() {
    return source;
  }

  @Override
  public String toString() {
    return "SendMessageAwaitTargetState{" + "previousState=" + previousState + ", source=" +
           source + '}';
  }
}
