package umlp.generator.domain.visitor.state;

/**
 * We are waiting for the target of an end message interaction
 */
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
