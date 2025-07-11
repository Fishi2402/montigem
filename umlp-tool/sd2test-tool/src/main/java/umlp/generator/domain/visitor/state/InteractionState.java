package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.Interaction;
import umlp.generator.domain.visitor.model.InteractionEntity;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.action.Action;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We have read an interaction action and wait for ocl conditions, a new interaction, or the end of
 * an activity-bar
 */
public final class InteractionState implements OCLState, InteractionExtractable {

  private final VisitorState previousState;
  private final InteractionEntity source;
  private final InteractionEntity target;
  private final Action action;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public InteractionState(VisitorState previousState,
      InteractionEntity source,
      InteractionEntity target,
      Action action) {
    this.previousState = previousState;
    this.source = source;
    this.target = target;
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

  public VisitorState previousState() {
    return previousState;
  }

  public InteractionEntity source() {
    return source;
  }

  public InteractionEntity target() {
    return target;
  }

  public Action action() {
    return action;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {
    var interaction = new Interaction(source, target, action);
    interaction.oclStatements().addAll(oclStatements());
    return new StateExtractedInteraction(interaction, previousState);
  }
}
