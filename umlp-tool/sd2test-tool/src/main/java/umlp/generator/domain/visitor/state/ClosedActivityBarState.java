package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.Interaction;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClosedActivityBarState implements OCLState, InteractionExtractable {

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
    var callActionState = openActivityBarState.call();
    var awaitActionState = callActionState.previousState();
    var source = awaitActionState.source();
    var target = awaitActionState.target();
    var action = callActionState.action();
    var activityBar = new ActivityBar();
    activityBar.body().addAll(openActivityBarState.interactions());
    activityBar.oclStatements().addAll(openActivityBarState.oclStatements());
    action.activityBar(Optional.of(activityBar));
    var interaction = new Interaction(source, target, action);
    interaction.oclStatements().addAll(openActivityBarState.oclStatements());
    var previousState = awaitActionState.previousState();
    return new StateExtractedInteraction(interaction, previousState);
  }

  @Override
  public String toString() {
    return "ClosedActivityBarState{" + "previousState=" + previousState + ", oclStatements=" +
           oclStatements + '}';
  }
}
