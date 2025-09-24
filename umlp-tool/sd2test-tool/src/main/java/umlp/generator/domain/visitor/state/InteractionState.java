package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionExtractable;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

/**
 * We have read an interaction and wait for ocl conditions, a new interaction, or the end of
 * an activity-bar
 */
public final class InteractionState implements OCLAttachableState, InteractionExtractable {

  private final VisitorState previousState;
  private final Interaction interaction;

  public InteractionState(VisitorState previousState, Interaction interaction) {
    this.previousState = previousState;
    this.interaction = interaction;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return interaction.oclStatements();
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInteraction(this);
  }

  public VisitorState previousState() {
    return previousState;
  }

  public Interaction interaction() {
    return interaction;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {
    return new StateExtractedInteraction(interaction, previousState);
  }

  @Override
  public String toString() {
    return "InteractionState{" + "previousState=" + previousState + ", interaction=" + interaction +
           '}';
  }
}
