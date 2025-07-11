package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class EarlyBodySectionState implements OCLState {

  private final SDObjectSectionState objectSection;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public EarlyBodySectionState(SDObjectSectionState objectSection) {
    this.objectSection = objectSection;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenEarlyBodySection(this);
  }

  public SDObjectSectionState objectSection() {
    return objectSection;
  }
}
