package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.interaction.ConstructorCallInteraction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public final class ConstructorCallState implements CallState {

  private final VisitorState previousState;
  private final ConstructorCallInteraction interaction;

  public ConstructorCallState(VisitorState previousState, ConstructorCallInteraction interaction) {
    this.previousState = previousState;
    this.interaction = interaction;
  }

  @Override
  public void setActivityBar(ActivityBar activityBar) {
    interaction.setActivityBar(activityBar);
  }

  @Override
  public VisitorState previousState() {
    return previousState;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {
    return new StateExtractedInteraction(interaction, previousState);
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return interaction.oclStatements();
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenConstructorCall(this);
  }

  public ConstructorCallInteraction interaction() {
    return interaction;
  }

  @Override
  public String toString() {
    return "ConstructorCallState{" + "previousState=" + previousState + ", interaction=" +
           interaction + '}';
  }
}
