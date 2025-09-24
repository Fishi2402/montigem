package umlp.generator.domain.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development._ast.ASTSDCall;
import de.monticore.lang.sd4development._ast.ASTSDClass;
import de.monticore.lang.sd4development._ast.ASTSDCondition;
import de.monticore.lang.sd4development._ast.ASTSDEndCall;
import de.monticore.lang.sd4development._ast.ASTSDNew;
import de.monticore.lang.sd4development._ast.ASTSDReturn;
import de.monticore.lang.sd4development._ast.ASTSDThrow;
import de.monticore.lang.sd4development._ast.ASTSDVariableDeclaration;
import de.monticore.lang.sd4development._visitor.SD4DevelopmentVisitor2;
import de.monticore.lang.sdbasis._ast.ASTSDActivityBar;
import de.monticore.lang.sdbasis._ast.ASTSDBody;
import de.monticore.lang.sdbasis._ast.ASTSDCompleteModifier;
import de.monticore.lang.sdbasis._ast.ASTSDFreeModifier;
import de.monticore.lang.sdbasis._ast.ASTSDInitialModifier;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSDObjectSource;
import de.monticore.lang.sdbasis._ast.ASTSDObjectTarget;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSDVisibleModifier;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.monticore.ocl.oclexpressions.OCLExpressionsMill;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.visitor.model.InteractionExtractable;
import umlp.generator.domain.visitor.model.MatchingStrategy;
import umlp.generator.domain.visitor.model.SDObject;
import umlp.generator.domain.visitor.model.SDTestExtractable;
import umlp.generator.domain.visitor.model.StateExtractedInteraction;
import umlp.generator.domain.visitor.model.Trigger;
import umlp.generator.domain.visitor.model.action.CallAction;
import umlp.generator.domain.visitor.model.action.ReturnAction;
import umlp.generator.domain.visitor.model.action.ThrowAction;
import umlp.generator.domain.visitor.model.interaction.Actor;
import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.interaction.InteractionVariantMatcher;
import umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;
import umlp.generator.domain.visitor.model.ocl.AssertStatement;
import umlp.generator.domain.visitor.model.ocl.LetStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;
import umlp.generator.domain.visitor.state.AwaitActionState;
import umlp.generator.domain.visitor.state.BodySectionState;
import umlp.generator.domain.visitor.state.CallActionState;
import umlp.generator.domain.visitor.state.ClosedActivityBarState;
import umlp.generator.domain.visitor.state.DiagramMatchingStrategyState;
import umlp.generator.domain.visitor.state.EndMessageAwaitSourceState;
import umlp.generator.domain.visitor.state.EndMessageAwaitTargetState;
import umlp.generator.domain.visitor.state.FinishedState;
import umlp.generator.domain.visitor.state.ImportSectionState;
import umlp.generator.domain.visitor.state.ImportState;
import umlp.generator.domain.visitor.state.InteractionState;
import umlp.generator.domain.visitor.state.OCLState;
import umlp.generator.domain.visitor.state.OpenActivityBarState;
import umlp.generator.domain.visitor.state.PreInteractionBodyState;
import umlp.generator.domain.visitor.state.SDObjectDeclarationState;
import umlp.generator.domain.visitor.state.SDObjectSectionState;
import umlp.generator.domain.visitor.state.SendMessageAwaitSourceState;
import umlp.generator.domain.visitor.state.SendMessageAwaitTargetState;
import umlp.generator.domain.visitor.state.VariableDeclarationState;
import umlp.generator.domain.visitor.state.VisitorState;
import umlp.generator.domain.visitor.state.VisitorStateVariantMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class SDVisitor implements SDBasisVisitor2,
    SD4DevelopmentVisitor2,
    OCLExpressionsVisitor2,
    MCBasicTypesVisitor2 {

  private VisitorState state = new ImportSectionState();

  @Override
  public void visit(ASTMCImportStatement node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenImportSection(ImportSectionState importSectionState) {
        return new ImportState(importSectionState, node);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void endVisit(ASTMCImportStatement node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenImport(ImportState importState) {
        var importSection = importState.section();
        importSection.imports().add(importState.importStatement());
        return importSection;
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedEndVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSequenceDiagram node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenImportSection(ImportSectionState importSectionState) {
        return new DiagramMatchingStrategyState(importSectionState);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDObject node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenDiagramMatchingStrategy(DiagramMatchingStrategyState diagramMatchingStrategy) {
        var objectSection = new SDObjectSectionState(diagramMatchingStrategy);
        return whenSDObjectSection(objectSection);
      }

      @Override
      public VisitorState whenSDObjectSection(SDObjectSectionState objectSection) {
        var name = node.getName();
        String qualifiedTypeName = node.isPresentMCObjectType() ?
            node.getMCObjectType().printType() :
            raiseError("missing variable type", getSourcePosition(node));
        return new SDObjectDeclarationState(objectSection, name, qualifiedTypeName);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void endVisit(ASTSDObject node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenSDObjectDeclaration(SDObjectDeclarationState sdObjectDeclarationState) {
        var matchingStrategy = sdObjectDeclarationState.matchingStrategy();
        var name = sdObjectDeclarationState.name();
        var qualifiedTypeName = sdObjectDeclarationState.qualifiedTypeName();
        var sdObject = new SDObject(matchingStrategy, name, qualifiedTypeName);
        var sdObjectSection = sdObjectDeclarationState.section();
        sdObjectSection.sdObjects().add(sdObject);
        return sdObjectSection;
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedEndVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDCompleteModifier node) {
    updateMatchingStrategy(MatchingStrategy.COMPLETE, node);
  }

  @Override
  public void visit(ASTSDFreeModifier node) {
    updateMatchingStrategy(MatchingStrategy.FREE, node);
  }

  @Override
  public void visit(ASTSDInitialModifier node) {
    updateMatchingStrategy(MatchingStrategy.INITIAL, node);
  }

  @Override
  public void visit(ASTSDVisibleModifier node) {
    updateMatchingStrategy(MatchingStrategy.VISIBLE, node);
  }

  private void updateMatchingStrategy(MatchingStrategy matchingStrategy, ASTNode node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenDiagramMatchingStrategy(DiagramMatchingStrategyState diagramMatchingStrategy) {
        return diagramMatchingStrategy.matchingStrategy(matchingStrategy);
      }

      @Override
      public VisitorState whenSDObjectDeclaration(SDObjectDeclarationState sdObjectDeclarationState) {
        return sdObjectDeclarationState.matchingStrategy(matchingStrategy);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDBody node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenSDObjectSection(SDObjectSectionState objectSectionState) {
        return new PreInteractionBodyState(objectSectionState);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDCondition node) {
    var expression = node.getExpression();
    var expressionString = OCLExpressionsMill.prettyPrint(expression, false);
    var assertStatement = new AssertStatement(expressionString);

    visitOCLStatement(assertStatement, node, state -> state);
  }

  @Override
  public void visit(ASTSDVariableDeclaration node) {
    var qualifiedTypeName = node.getMCType().printType();
    var name = node.getName();
    var assignmentExpression = OCLExpressionsMill.prettyPrint(node.getAssignment(), false);
    var letStatement = new LetStatement(qualifiedTypeName, name, assignmentExpression);

    visitOCLStatement(letStatement,
                      node,
                      state -> new VariableDeclarationState(state, letStatement));
  }

  private <T extends OCLStatement> void visitOCLStatement(T statement,
      ASTNode node,
      Function<OCLState, VisitorState> stateWrapper) {
    updateState(new VisitorStateVariantMatcher<VisitorState>() {
      @Override
      public VisitorState whenPreInteractionBody(PreInteractionBodyState preInteractionBodyState) {
        return addOCLStatementAndWrap(preInteractionBodyState);
      }

      @Override
      public VisitorState whenInteraction(InteractionState interactionState) {
        return addOCLStatementAndWrap(interactionState);
      }

      @Override
      public VisitorState whenClosedActivityBar(ClosedActivityBarState closedActivityBarState) {
        return addOCLStatementAndWrap(closedActivityBarState);
      }

      @Override
      public VisitorState whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
        return addOCLStatementAndWrap(openActivityBarState);
      }

      @Override
      public VisitorState whenCallAction(CallActionState callActionState) {
        var awaitActionState = callActionState.previousState();
        var previousState = awaitActionState.previousState();
        var source = awaitActionState.source();
        var target = awaitActionState.target();
        var action = callActionState.action();
        var interactionState = new InteractionState(previousState, source, target, action);
        return addOCLStatementAndWrap(interactionState);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }

      VisitorState addOCLStatementAndWrap(OCLState state) {
        state.oclStatements().add(statement);
        return stateWrapper.apply(state);
      }
    });
  }

  @Override
  public void endVisit(ASTSDVariableDeclaration node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenVariableDeclaration(VariableDeclarationState variableDeclarationState) {
        return variableDeclarationState.previousState();
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedEndVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDSendMessage node) {
    visitInteractionStart(node, SendMessageAwaitSourceState::new);
  }

  @Override
  public void visit(ASTSDEndCall node) {
    visitInteractionStart(node, EndMessageAwaitTargetState::new);
  }

  private void visitInteractionStart(ASTNode node,
      Function<VisitorState, VisitorState> stateWrapper) {
    var sourcePosition = getSourcePosition(node);
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenPreInteractionBody(PreInteractionBodyState preInteractionBodyState) {
        return stateWrapper.apply(preInteractionBodyState);
      }

      @Override
      public VisitorState whenCallAction(CallActionState callActionState) {
        return finish(callActionState.extractInteraction());
      }

      @Override
      public VisitorState whenInteraction(InteractionState interactionState) {
        return finish(interactionState.extractInteraction());
      }

      @Override
      public VisitorState whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
        return stateWrapper.apply(openActivityBarState);
      }

      @Override
      public VisitorState whenClosedActivityBar(ClosedActivityBarState closedActivityBarState) {
        return finish(closedActivityBarState.extractInteraction());
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }

      private VisitorState finish(StateExtractedInteraction extractedInteraction) {
        var interaction = extractedInteraction.interaction();
        var previousState = extractedInteraction.previousState();
        var newPreviousState = finishInteraction(interaction, previousState, sourcePosition);
        return stateWrapper.apply(newPreviousState);
      }
    });
  }

  private VisitorState finishInteraction(Interaction interaction,
      VisitorState previousState,
      Optional<SourcePosition> sourcePosition) {
    var triggerCall =
        interaction.matchVariant(new InteractionVariantMatcher<Optional<MethodCallInteraction>>() {

      @Override
      public Optional<MethodCallInteraction> whenMethodCall(MethodCallInteraction methodCall) {
        return Optional.of(methodCall).filter(MethodCallInteraction::isTrigger);
      }

      @Override
      public Optional<MethodCallInteraction> otherwise(Interaction interaction) {
        return Optional.empty();
      }
    });
    return previousState.matchVariant(triggerCall.isPresent() ? new VisitorStateVariantMatcher<>() {
      final Trigger trigger = new Trigger(triggerCall.get());

      @Override
      public VisitorState whenPreInteractionBody(PreInteractionBodyState preInteractionBodyState) {
        return new BodySectionState(preInteractionBodyState, trigger);
      }

      @Override
      public VisitorState whenBodySection(BodySectionState bodySectionState) {
        return bodySectionState.replaceLastTrigger(trigger);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseStateError("trigger is unreachable", state, sourcePosition);
      }
    } : new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenBodySection(BodySectionState bodySectionState) {
        bodySectionState.lastTrigger().resultingInteractions().add(interaction);
        return bodySectionState;
      }

      @Override
      public VisitorState whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
        openActivityBarState.interactions().add(interaction);
        return openActivityBarState;
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseStateError("interaction has no cause, did you forget a trigger?",
                               state,
                               sourcePosition);
      }
    });
  }

  @Override
  public void visit(ASTSDObjectSource node) {
    var name = node.getName();
    var source = new Actor(name, false);
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenSendMessageAwaitSource(SendMessageAwaitSourceState sendMessageAwaitSourceState) {
        return new SendMessageAwaitTargetState(sendMessageAwaitSourceState, source);
      }

      @Override
      public VisitorState whenEndMessageAwaitSource(EndMessageAwaitSourceState endMessageAwaitSourceState) {
        var previousState = endMessageAwaitSourceState.previousState().previousState();
        var target = endMessageAwaitSourceState.target();
        return new AwaitActionState(previousState, source, target);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDObjectTarget node) {
    var name = node.getName();
    var target = new Actor(name, false);
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenSendMessageAwaitTarget(SendMessageAwaitTargetState sendMessageAwaitTargetState) {
        var previousState = sendMessageAwaitTargetState.previousState().previousState();
        var source = sendMessageAwaitTargetState.source();
        return new AwaitActionState(previousState, source, target);
      }

      @Override
      public VisitorState whenEndMessageAwaitTarget(EndMessageAwaitTargetState endMessageAwaitTargetState) {
        return new EndMessageAwaitSourceState(endMessageAwaitTargetState, target);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDClass node) {
    var name = node.getMCObjectType().printType();
    var entity = new Actor(name, true);
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenSendMessageAwaitSource(SendMessageAwaitSourceState sendMessageAwaitSourceState) {
        return new SendMessageAwaitTargetState(sendMessageAwaitSourceState, entity);
      }

      @Override
      public VisitorState whenSendMessageAwaitTarget(SendMessageAwaitTargetState sendMessageAwaitTargetState) {
        var previousState = sendMessageAwaitTargetState.previousState().previousState();
        var source = sendMessageAwaitTargetState.source();
        return new AwaitActionState(previousState, source, entity);
      }

      @Override
      public VisitorState whenEndMessageAwaitTarget(EndMessageAwaitTargetState endMessageAwaitTargetState) {
        return new EndMessageAwaitSourceState(endMessageAwaitTargetState, entity);
      }

      @Override
      public VisitorState whenEndMessageAwaitSource(EndMessageAwaitSourceState endMessageAwaitSourceState) {
        var previousState = endMessageAwaitSourceState.previousState().previousState();
        var target = endMessageAwaitSourceState.target();
        return new AwaitActionState(previousState, entity, target);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDCall node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenAwaitAction(AwaitActionState awaitActionState) {
        var methodName = node.getName();
        var isStatic = node.isStatic();
        var isTrigger = node.isTrigger();
        Optional<List<String>> argumentExpressions = node.isPresentArguments() ?
            stringifyExpressions(node.getArguments().getExpressionList()) :
            Optional.empty();
        var action = new CallAction(methodName, isStatic, isTrigger, argumentExpressions);
        return new CallActionState(awaitActionState, action);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }


  @Override
  public void visit(ASTSDReturn node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenAwaitAction(AwaitActionState awaitActionState) {
        var previousState = awaitActionState.previousState();
        var source = awaitActionState.source();
        var target = awaitActionState.target();
        var hasReturnValue = node.isPresentExpression();
        var returnValue = hasReturnValue ?
            stringifyExpression(node.getExpression()) :
            Optional.<String>empty();
        var action = new ReturnAction(returnValue, hasReturnValue);
        return new InteractionState(previousState, source, target, action);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDThrow node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenAwaitAction(AwaitActionState awaitActionState) {
        var previousState = awaitActionState.previousState();
        var source = awaitActionState.source();
        var target = awaitActionState.target();
        var throwableTypeName = node.getMCObjectType().printType();
        Optional<List<String>> arguments = node.isPresentArguments() ?
            stringifyExpressions(node.getArguments().getExpressionList()) :
            Optional.empty();
        var action = new ThrowAction(throwableTypeName, arguments);
        return new InteractionState(previousState, source, target, action);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void visit(ASTSDActivityBar node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenCallAction(CallActionState callActionState) {
        return new OpenActivityBarState(callActionState);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }
    });
  }

  @Override
  public void endVisit(ASTSDActivityBar node) {
    updateState(new VisitorStateVariantMatcher<>() {

      @Override
      public VisitorState whenCallAction(CallActionState callActionState) {
        return finishInteraction(callActionState.extractInteraction());
      }

      @Override
      public VisitorState whenInteraction(InteractionState interactionState) {
        return finishInteraction(interactionState.extractInteraction());
      }

      @Override
      public VisitorState whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
        return new ClosedActivityBarState(openActivityBarState);
      }

      @Override
      public VisitorState whenClosedActivityBar(ClosedActivityBarState closedActivityBarState) {
        return finishInteraction(closedActivityBarState.extractInteraction());
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedVisitError(node, state);
      }

      private ClosedActivityBarState finishInteraction(StateExtractedInteraction extractedInteraction) {
        var interaction = extractedInteraction.interaction();
        var previousState = extractedInteraction.previousState();
        var openActivityBarState =
            previousState.matchVariant(new VisitorStateVariantMatcher<OpenActivityBarState>() {
          @Override
          public OpenActivityBarState whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
            return openActivityBarState;
          }

          @Override
          public OpenActivityBarState otherwise(VisitorState state) {
            return raiseStateError("expected OpenActivityBarState", state, getSourcePosition(node));
          }
        });
        openActivityBarState.interactions().add(interaction);
        return new ClosedActivityBarState(openActivityBarState);
      }
    });
  }

  @Override
  public void visit(ASTSDNew node) {
    raiseError("constructor monitoring is not supported", getSourcePosition(node));
  }

  @Override
  public void endVisit(ASTSDBody node) {
    updateState(new VisitorStateVariantMatcher<>() {
      @Override
      public VisitorState whenPreInteractionBody(PreInteractionBodyState preInteractionBodyState) {
        return new FinishedState(preInteractionBodyState);
      }

      @Override
      public VisitorState whenBodySection(BodySectionState bodySectionState) {
        return new FinishedState(bodySectionState);
      }

      @Override
      public VisitorState whenCallAction(CallActionState callActionState) {
        return finish(callActionState);
      }

      @Override
      public VisitorState whenInteraction(InteractionState interactionState) {
        return finish(interactionState);
      }

      @Override
      public VisitorState whenClosedActivityBar(ClosedActivityBarState closedActivityBarState) {
        return finish(closedActivityBarState);
      }

      @Override
      public VisitorState otherwise(VisitorState state) {
        return raiseUnexpectedEndVisitError(node, state);
      }

      private FinishedState finish(InteractionExtractable extractable) {
        var extractedInteraction = extractable.extractInteraction();
        var sourcePosition = getSourcePosition(node);
        var interaction = extractedInteraction.interaction();
        var previousState = extractedInteraction.previousState();
        var newPreviousState = finishInteraction(interaction, previousState, sourcePosition);
        var testExtractable =
            newPreviousState.matchVariant(new VisitorStateVariantMatcher<SDTestExtractable>() {
          @Override
          public SDTestExtractable whenBodySection(BodySectionState bodySectionState) {
            return bodySectionState;
          }

          @Override
          public SDTestExtractable otherwise(VisitorState state) {
            return raiseStateError("did not expect end of body here", state, sourcePosition);
          }
        });

        return new FinishedState(testExtractable);
      }
    });
  }

  private void updateState(VisitorStateVariantMatcher<VisitorState> matcher) {
    this.state = state.matchVariant(matcher);
  }

  private <T> T raiseUnexpectedVisitError(ASTNode node, VisitorState state) {
    return raiseStateError("unexpected visit of node " + node, state, getSourcePosition(node));
  }

  private <T> T raiseUnexpectedEndVisitError(ASTNode node, VisitorState state) {
    return raiseStateError("unexpected visit-end of node " + node, state, getSourcePosition(node));
  }

  private <T> T raiseStateError(String message,
      VisitorState state,
      Optional<SourcePosition> sourcePosition) {
    return raiseError(message + " in state " + state, sourcePosition);
  }

  private <T> T raiseError(String message, Optional<SourcePosition> sourcePosition) {
    if (sourcePosition.isPresent()) {
      Log.error(message, sourcePosition.get());
    } else {
      Log.error(message);
    }

    throw new IllegalStateException(message);
  }

  public VisitorState currentState() {
    return state;
  }

  private static Optional<SourcePosition> getSourcePosition(ASTNode node) {
    return node.isPresent_SourcePositionStart() ?
        Optional.of(node.get_SourcePositionStart()) :
        Optional.empty();
  }

  private Optional<List<String>> stringifyExpressions(List<ASTExpression> expressions) {
    var expressionStrings = new ArrayList<String>();
    for (var expression : expressions) {
      var optExprStr = stringifyExpression(expression);
      if (optExprStr.isEmpty()) {
        return Optional.empty();
      }
      expressionStrings.add(optExprStr.get());
    }
    return Optional.of(expressionStrings);
  }

  private Optional<String> stringifyExpression(ASTExpression expression) {
    return SD4DevelopmentMill
        .typeDispatcher()
        .isSD4DevelopmentASTSDIncompleteExpression(expression) ?
        Optional.empty() :
        Optional.of(SD4DevelopmentMill.prettyPrint(expression, false));
  }
}
