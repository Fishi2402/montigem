package umlp.generator.domain.visitor.state;

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
}
