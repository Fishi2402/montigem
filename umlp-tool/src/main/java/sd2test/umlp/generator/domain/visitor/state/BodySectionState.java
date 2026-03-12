package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.SDTest;
import sd2test.umlp.generator.domain.visitor.model.SDTestExtractable;
import sd2test.umlp.generator.domain.visitor.model.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * The body of a SD, where triggers are stored
 */
public final class BodySectionState implements VisitorState, SDTestExtractable {

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

  public PreInteractionBodyState preInteractionBody() {
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

  @Override
  public SDTest extractTest() {
    var test = earlyBodySection.extractTest();
    test.trigger().addAll(trigger());
    test.trigger().add(lastTrigger());
    return test;
  }

  @Override
  public String toString() {
    return "BodySectionState{" + "earlyBodySection=" + earlyBodySection + ", trigger=" + trigger +
           ", lastTrigger=" + lastTrigger + '}';
  }
}
