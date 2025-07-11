package umlp.generator.domain.visitor.state;

public final class SendMessageAwaitTarget implements VisitorState {

  private final SendMessageAwaitSource previousState;
  private final String sourceName;

  public SendMessageAwaitTarget(SendMessageAwaitSource previousState, String sourceName) {
    this.previousState = previousState;
    this.sourceName = sourceName;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSendMessageAwaitTarget(this);
  }

  public SendMessageAwaitSource previousState() {
    return previousState;
  }

  public String sourceName() {
    return sourceName;
  }
}
