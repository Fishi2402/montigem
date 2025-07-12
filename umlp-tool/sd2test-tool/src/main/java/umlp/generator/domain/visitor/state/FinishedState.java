package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.SDTest;
import umlp.generator.domain.visitor.model.SDTestExtractable;

public final class FinishedState implements VisitorState, SDTestExtractable {

  private final SDTest test;

  public FinishedState(SDTestExtractable extractable) {
    this.test = extractable.extractTest();
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenFinished(this);
  }

  @Override
  public SDTest extractTest() {
    return test;
  }

  @Override
  public String toString() {
    return "FinishedState{" + "test=" + test + '}';
  }
}
