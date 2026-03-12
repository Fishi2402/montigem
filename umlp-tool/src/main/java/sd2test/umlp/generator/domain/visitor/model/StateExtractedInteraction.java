package sd2test.umlp.generator.domain.visitor.model;

import sd2test.umlp.generator.domain.visitor.model.interaction.Interaction;
import sd2test.umlp.generator.domain.visitor.state.VisitorState;

public final class StateExtractedInteraction implements InteractionExtractable {
  private final Interaction interaction;
  private final VisitorState previousState;

  public StateExtractedInteraction(Interaction interaction, VisitorState previousState) {
    this.interaction = interaction;
    this.previousState = previousState;
  }

  public Interaction interaction() {
    return interaction;
  }

  public VisitorState previousState() {
    return previousState;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {
    return this;
  }
}
