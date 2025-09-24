package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.InteractionExtractable;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.action.Action;
import umlp.generator.domain.visitor.model.action.ActionVariantMatcher;
import umlp.generator.domain.visitor.model.action.CallAction;
import umlp.generator.domain.visitor.model.action.ReturnAction;
import umlp.generator.domain.visitor.model.action.ThrowAction;
import umlp.generator.domain.visitor.model.interaction.Actor;
import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;
import umlp.generator.domain.visitor.model.interaction.ReturnInteraction;
import umlp.generator.domain.visitor.model.interaction.ReturnValue;
import umlp.generator.domain.visitor.model.interaction.ThrowInteraction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * We have read an interaction action and wait for ocl conditions, a new interaction, or the end of
 * an activity-bar
 */
public final class InteractionState implements OCLState, InteractionExtractable {

  private final VisitorState previousState;
  private final Actor source;
  private final Actor target;
  private final Action action;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public InteractionState(VisitorState previousState, Actor source, Actor target, Action action) {
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

  public Actor source() {
    return source;
  }

  public Actor target() {
    return target;
  }

  public Action action() {
    return action;
  }

  @Override
  public StateExtractedInteraction extractInteraction() {

    var interaction = action.matchVariant(new ActionVariantMatcher<Interaction>() {
      @Override
      public Interaction whenCall(CallAction callAction) {
        return new MethodCallInteraction(source,
                                         target,
                                         callAction.methodName(),
                                         callAction.isStatic(),
                                         callAction.isTrigger(),
                                         callAction.argumentExpressions());
      }

      @Override
      public Interaction whenReturn(ReturnAction returnAction) {
        return new ReturnInteraction(source,
                                     target,
                                     returnAction.hasReturnValue() ?
                                         Optional.of(new ReturnValue(returnAction.returnValue())) :
                                         Optional.empty());
      }

      @Override
      public Interaction whenThrow(ThrowAction throwAction) {
        return new ThrowInteraction(source, target, throwAction.throwableTypeName(), throwAction.arguments());
      }

      @Override
      public Interaction otherwise(Action action) {
        throw new IllegalStateException();
      }
    });
    interaction.oclStatements().addAll(oclStatements());
    return new StateExtractedInteraction(interaction, previousState);
  }

  @Override
  public String toString() {
    return "InteractionState{" + "previousState=" + previousState + ", source=" + source +
           ", target=" + target + ", action=" + action + ", oclStatements=" + oclStatements + '}';
  }
}
