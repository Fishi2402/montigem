package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.action.Action;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class InteractionState implements OCLState {

  private final AwaitActionState previousState;
  private final Action action;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public InteractionState(AwaitActionState previousState, Action action) {
    this.previousState = previousState;
    this.action = action;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInteraction(this);
  }

  public AwaitActionState previousState() {
    return previousState;
  }

  public Action action() {
    return action;
  }
}
