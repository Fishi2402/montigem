package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Trigger;

import java.util.ArrayList;
import java.util.List;

public final class BodySection implements VisitorState {

  private final EarlyBodySection earlyBodySection;
  private final List<Trigger> trigger = new ArrayList<>();

  public BodySection(EarlyBodySection earlyBodySection) {
    this.earlyBodySection = earlyBodySection;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenBodySection(this);
  }

  public EarlyBodySection earlyBodySection() {
    return earlyBodySection;
  }

  public List<Trigger> trigger() {
    return trigger;
  }
}
