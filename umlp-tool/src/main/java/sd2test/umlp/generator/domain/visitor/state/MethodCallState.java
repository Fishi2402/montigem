package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.ActivityBar;
import sd2test.umlp.generator.domain.visitor.model.StateExtractedInteraction;
import sd2test.umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;
import sd2test.umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

/**
 * We received a call action and are now waiting for an optional activity bar or ocl statements
 */
public final class MethodCallState implements CallState {

  private final VisitorState previousState;
  private final MethodCallInteraction interaction;

  public MethodCallState(VisitorState previousState, MethodCallInteraction interaction) {
    this.previousState = previousState;
    this.interaction = interaction;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenMethodCall(this);
  }

  @Override
  public void setActivityBar(ActivityBar activityBar) {
    interaction.setActivityBar(activityBar);
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return interaction.oclStatements();
  }

  @Override
  public VisitorState previousState() {
    return previousState;
  }

  public MethodCallInteraction interaction() {
    return interaction;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {
    return new StateExtractedInteraction(interaction, previousState);
  }

  @Override
  public String toString() {
    return "MethodCallState{" + "previousState=" + previousState + ", interaction=" + interaction +
           '}';
  }


}
