package umlp.generator.domain.visitor.state;

public final class InteractionSource implements VisitorState {

  private final InteractionSection section;
  private final String sourceName;

  public InteractionSource(InteractionSection section, String sourceName) {
    this.section = section;
    this.sourceName = sourceName;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInteractionSource(this);
  }

  public InteractionSection section() {
    return section;
  }

  public String sourceName() {
    return sourceName;
  }
}
