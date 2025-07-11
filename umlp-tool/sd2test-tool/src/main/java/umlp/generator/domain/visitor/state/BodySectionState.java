package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Trigger;

import java.util.ArrayList;
import java.util.List;

public final class BodySectionState implements VisitorState {

  private final EarlyBodySectionState earlyBodySectionState;
  private final List<Trigger> trigger = new ArrayList<>();

  public BodySectionState(EarlyBodySectionState earlyBodySectionState) {
    this.earlyBodySectionState = earlyBodySectionState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenBodySection(this);
  }

  public EarlyBodySectionState earlyBodySection() {
    return earlyBodySectionState;
  }

  public List<Trigger> trigger() {
    return trigger;
  }
}
