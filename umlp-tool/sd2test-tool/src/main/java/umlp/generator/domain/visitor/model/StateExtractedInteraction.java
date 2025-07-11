package umlp.generator.domain.visitor.model;

import umlp.generator.domain.visitor.state.VisitorState;

public final class StateExtractedInteraction {
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
}
