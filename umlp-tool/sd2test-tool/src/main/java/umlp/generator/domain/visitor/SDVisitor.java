package umlp.generator.domain.visitor;

import de.monticore.ast.ASTNode;
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
import umlp.generator.domain.visitor.model.MatchingStrategy;
import umlp.generator.domain.visitor.model.SDObject;
import umlp.generator.domain.visitor.model.ocl.AssertStatement;
import umlp.generator.domain.visitor.model.ocl.LetStatement;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;
import umlp.generator.domain.visitor.state.CallActionState;
import umlp.generator.domain.visitor.state.ClosedActivityBarState;
import umlp.generator.domain.visitor.state.ImportSectionState;
import umlp.generator.domain.visitor.state.ImportState;
import umlp.generator.domain.visitor.state.InteractionState;
import umlp.generator.domain.visitor.state.OCLState;
import umlp.generator.domain.visitor.state.OpenActivityBarState;
import umlp.generator.domain.visitor.state.PreInteractionBodyState;
import umlp.generator.domain.visitor.state.SDObjectDeclarationState;
import umlp.generator.domain.visitor.state.SDObjectSectionState;
import umlp.generator.domain.visitor.state.VariableDeclarationState;
import umlp.generator.domain.visitor.state.VisitorState;
import umlp.generator.domain.visitor.state.VisitorStateVariantMatcher;

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
        return new SDObjectSectionState(importSectionState);
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
    updateObjectMatchingStrategy(MatchingStrategy.COMPLETE, node);
  }

  @Override
  public void visit(ASTSDFreeModifier node) {
    updateObjectMatchingStrategy(MatchingStrategy.FREE, node);
  }

  @Override
  public void visit(ASTSDInitialModifier node) {
    updateObjectMatchingStrategy(MatchingStrategy.INITIAL, node);
  }

  @Override
  public void visit(ASTSDVisibleModifier node) {
    updateObjectMatchingStrategy(MatchingStrategy.VISIBLE, node);
  }

  private void updateObjectMatchingStrategy(MatchingStrategy matchingStrategy, ASTNode node) {
    updateState(new VisitorStateVariantMatcher<>() {
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
        var previousState = callActionState.previousState().previousState();
        var source = callActionState.previousState().source();
        var target = callActionState.previousState().target();
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
    //TODO start of an interaction
  }

  @Override
  public void endVisit(ASTSDSendMessage node) {
    //TODO end of an interaction
  }

  @Override
  public void visit(ASTSDEndCall node) {
    //TODO start of an interaction
  }

  @Override
  public void endVisit(ASTSDEndCall node) {
    //TODO end of an interaction
  }

  @Override
  public void visit(ASTSDObjectSource node) {
    //TODO source of an interaction
  }

  @Override
  public void visit(ASTSDObjectTarget node) {
    //TODO target of an interaction
  }

  @Override
  public void visit(ASTSDClass node) {
    //TODO start of a description of source or target of an interaction
  }

  @Override
  public void endVisit(ASTSDClass node) {
    //TODO end of a description of source or target of an interaction
  }

  @Override
  public void visit(ASTSDCall node) {
    //TODO
  }

  @Override
  public void visit(ASTSDReturn node) {
    //TODO
  }

  @Override
  public void visit(ASTSDThrow node) {
    //TODO
  }

  @Override
  public void endVisit(ASTSDThrow node) {
    //TODO
  }

  @Override
  public void visit(ASTSDActivityBar node) {
    //TODO
  }

  @Override
  public void endVisit(ASTSDActivityBar node) {
    //TODO
  }

  @Override
  public void visit(ASTSDNew node) {
    raiseError("constructor monitoring is not supported", getSourcePosition(node));
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
}
