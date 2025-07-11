package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class ClosedActivityBarState implements OCLState {

  private final OpenActivityBarState previousState;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ClosedActivityBarState(OpenActivityBarState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenClosedActivityBar(this);
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  public OpenActivityBarState previousState() {
    return previousState;
  }
}
