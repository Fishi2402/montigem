package umlp.generator.domain.visitor.state;

public final class AwaitAction implements VisitorState {

  private final VisitorState previousState;
  private final String sourceName;
  private final String targetName;
  private final boolean staticTarget;

  public AwaitAction(VisitorState previousState,
      String sourceName,
      String targetName,
      boolean staticTarget) {
    this.previousState = previousState;
    this.sourceName = sourceName;
    this.targetName = targetName;
    this.staticTarget = staticTarget;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAwaitAction(this);
  }

  public VisitorState previousState() {
    return previousState;
  }

  public String sourceName() {
    return sourceName;
  }

  public String targetName() {
    return targetName;
  }

  public boolean staticTarget() {
    return staticTarget;
  }
}
