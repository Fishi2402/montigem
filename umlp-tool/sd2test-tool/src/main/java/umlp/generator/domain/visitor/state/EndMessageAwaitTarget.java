package umlp.generator.domain.visitor.state;

public final class EndMessageAwaitTarget implements VisitorState {

  private final VisitorState previousState;

  public EndMessageAwaitTarget(VisitorState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenEndMessageAwaitTarget(this);
  }

  public VisitorState previousState() {
    return previousState;
  }
}
