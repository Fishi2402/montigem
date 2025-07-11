package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Interaction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are reading the contents of an activity bar
 */
public final class OpenActivityBarState implements OCLState {

  private final CallActionState call;
  private final List<Interaction> interactions = new ArrayList<>();
  private final List<OCLStatement> callOclStatements = new ArrayList<>();

  public OpenActivityBarState(CallActionState call) {
    this.call = call;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenOpenActivityBar(this);
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return callOclStatements;
  }

  public CallActionState call() {
    return call;
  }

  public List<Interaction> interactions() {
    return interactions;
  }
}
