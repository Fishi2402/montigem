package umlp.generator.domain.visitor.state;

/**
 * waiting for the source of a send-message interaction
 */
public final class SendMessageAwaitSourceState implements VisitorState {

  private final VisitorState previousState;

  public SendMessageAwaitSourceState(VisitorState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSendMessageAwaitSource(this);
  }

  public VisitorState previousState() {
    return previousState;
  }

  @Override
  public String toString() {
    return "SendMessageAwaitSourceState{" + "previousState=" + previousState + '}';
  }
}
