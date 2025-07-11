package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Trigger;

public final class TriggerSectionState implements VisitorState {

  private final BodySectionState bodySectionState;
  private final Trigger trigger;

  public TriggerSectionState(BodySectionState bodySectionState, Trigger trigger) {
    this.bodySectionState = bodySectionState;
    this.trigger = trigger;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenTriggerSection(this);
  }

  public BodySectionState bodySection() {
    return bodySectionState;
  }

  public Trigger trigger() {
    return trigger;
  }
}
