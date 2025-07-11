package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * The body of a SD, where triggers are stored
 */
public final class BodySectionState implements VisitorState {

  private final PreInteractionBodyState earlyBodySection;
  private final List<Trigger> trigger = new ArrayList<>();
  private Trigger lastTrigger;

  public BodySectionState(PreInteractionBodyState earlyBodySection, Trigger lastTrigger) {
    this.earlyBodySection = earlyBodySection;
    this.lastTrigger = lastTrigger;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenBodySection(this);
  }

  public PreInteractionBodyState earlyBodySection() {
    return earlyBodySection;
  }

  public List<Trigger> trigger() {
    return trigger;
  }

  public Trigger lastTrigger() {
    return lastTrigger;
  }

  public BodySectionState replaceLastTrigger(Trigger trigger) {
    this.trigger.add(lastTrigger);
    this.lastTrigger = trigger;
    return this;
  }
}
