package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.StateExtractedInteraction;

public interface InteractionExtractable {
  StateExtractedInteraction extractInteraction();
}
