package umlp.generator.domain.visitor.state;

public final class AwaitingTriggerSectionState implements VisitorState {

  private final BodySectionState bodySectionState;

  public AwaitingTriggerSectionState(BodySectionState bodySectionState) {
    this.bodySectionState = bodySectionState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAwaitingTriggerSection(this);
  }

  public BodySectionState bodySection() {
    return bodySectionState;
  }
}
