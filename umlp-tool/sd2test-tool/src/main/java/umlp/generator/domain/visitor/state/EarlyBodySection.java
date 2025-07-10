package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class EarlyBodySection implements OCLState {

  private final SDObjectSection objectSection;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public EarlyBodySection(SDObjectSection objectSection) {
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

  public SDObjectSection objectSection() {
    return objectSection;
  }
}
