package umlp.generator.domain.visitor.state;

public final class InteractionTarget implements VisitorState {

  private final InteractionSection section;
  private final String targetName;

  public InteractionTarget(InteractionSection section, String targetName) {
    this.section = section;
    this.targetName = targetName;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInteractionTarget(this);
  }

  public InteractionSection section() {
    return section;
  }

  public String targetName() {
    return targetName;
  }
}
