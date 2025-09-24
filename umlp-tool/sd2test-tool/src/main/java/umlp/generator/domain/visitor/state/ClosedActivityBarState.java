package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.InteractionExtractable;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class ClosedActivityBarState implements OCLAttachableState, InteractionExtractable {

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

  @Override
  public StateExtractedInteraction extractInteraction() {
    var openActivityBarState = previousState();

    var activityBar = new ActivityBar();
    activityBar.body().addAll(openActivityBarState.interactions());
    activityBar.oclStatements().addAll(openActivityBarState.oclStatements());

    var callState = openActivityBarState.call();
    callState.setActivityBar(activityBar);
    return callState.extractInteraction();
  }

  @Override
  public String toString() {
    return "ClosedActivityBarState{" + "previousState=" + previousState + ", oclStatements=" +
           oclStatements + '}';
  }
}
