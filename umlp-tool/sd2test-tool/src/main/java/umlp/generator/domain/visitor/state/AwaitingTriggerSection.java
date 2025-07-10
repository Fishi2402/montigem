package umlp.generator.domain.visitor.state;

public final class AwaitingTriggerSection implements VisitorState {

  private final BodySection bodySection;

  public AwaitingTriggerSection(BodySection bodySection) {
    this.bodySection = bodySection;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAwaitingTriggerSection(this);
  }

  public BodySection bodySection() {
    return bodySection;
  }
}
