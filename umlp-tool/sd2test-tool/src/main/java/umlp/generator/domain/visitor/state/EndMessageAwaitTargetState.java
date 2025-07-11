package umlp.generator.domain.visitor.state;

public final class EndMessageAwaitTargetState implements VisitorState {

  private final VisitorState previousState;

  public EndMessageAwaitTargetState(VisitorState previousState) {
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
