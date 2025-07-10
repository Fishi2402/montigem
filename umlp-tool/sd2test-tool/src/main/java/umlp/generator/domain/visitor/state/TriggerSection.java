package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Trigger;

public final class TriggerSection implements VisitorState {

  private final BodySection bodySection;
  private final Trigger trigger;

  public TriggerSection(BodySection bodySection, Trigger trigger) {
    this.bodySection = bodySection;
    this.trigger = trigger;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenTriggerSection(this);
  }

  public BodySection bodySection() {
    return bodySection;
  }

  public Trigger trigger() {
    return trigger;
  }
}
