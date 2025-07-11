package umlp.generator.domain.visitor.state;

public final class EndMessageAwaitSource implements VisitorState {

  private final EndMessageAwaitTarget previousState;
  private final String targetName;
  private final boolean staticTarget;

  public EndMessageAwaitSource(EndMessageAwaitTarget previousState,
      String targetName,
      boolean staticTarget) {
    this.previousState = previousState;
    this.targetName = targetName;
    this.staticTarget = staticTarget;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenEndMessageAwaitSource(this);
  }

  public EndMessageAwaitTarget previousState() {
    return previousState;
  }

  public String targetName() {
    return targetName;
  }

  public boolean staticTarget() {
    return staticTarget;
  }
}
