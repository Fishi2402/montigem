package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public final class BodySection implements OCLState {

  private final EarlyBodySection earlyBodySection;
  //TODO triggers

  public BodySection(EarlyBodySection earlyBodySection) {
    this.earlyBodySection = earlyBodySection;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    //TODO return olcStatements of last interaction
    return earlyBodySection.oclStatements();
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenBodySection(this);
  }
}
