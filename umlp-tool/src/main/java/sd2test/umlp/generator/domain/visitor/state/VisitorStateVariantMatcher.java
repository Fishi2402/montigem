package sd2test.umlp.generator.domain.visitor.state;

public interface VisitorStateVariantMatcher<T> {

  default T whenInitial(InitialState initial) {
    return otherwise(initial);
  }

  default T whenImport(ImportState importState) {
    return otherwise(importState);
  }

  default T whenDiagram(DiagramState diagram) {
    return otherwise(diagram);
  }

  default T whenSDObjectSection(SDObjectSectionState objectSection) {
    return otherwise(objectSection);
  }

  default T whenSDObjectDeclaration(SDObjectDeclarationState sdObjectDeclaration) {
    return otherwise(sdObjectDeclaration);
  }

  default T whenPreInteractionBody(PreInteractionBodyState preInteractionBody) {
    return otherwise(preInteractionBody);
  }

  default T whenAssertStatement(AssertStatementState assertStatement) {
    return otherwise(assertStatement);
  }

  default T whenVariableDeclaration(VariableDeclarationState variableDeclaration) {
    return otherwise(variableDeclaration);
  }

  default T whenBodySection(BodySectionState bodySection) {
    return otherwise(bodySection);
  }

  default T whenSendMessageAwaitSource(SendMessageAwaitSourceState sendMessageAwaitSource) {
    return otherwise(sendMessageAwaitSource);
  }

  default T whenSendMessageAwaitTarget(SendMessageAwaitTargetState sendMessageAwaitTarget) {
    return otherwise(sendMessageAwaitTarget);
  }

  default T whenEndMessageAwaitTarget(EndMessageAwaitTargetState endMessageAwaitTarget) {
    return otherwise(endMessageAwaitTarget);
  }

  default T whenEndMessageAwaitSource(EndMessageAwaitSourceState endMessageAwaitSource) {
    return otherwise(endMessageAwaitSource);
  }

  default T whenAwaitAction(AwaitActionState awaitAction) {
    return otherwise(awaitAction);
  }

  default T whenMethodCall(MethodCallState methodCall) {
    return otherwise(methodCall);
  }

  default T whenAwaitConstructorSource(AwaitConstructorSourceState awaitConstructorSource) {
    return otherwise(awaitConstructorSource);
  }

  default T whenConstructorCall(ConstructorCallState constructorCall) {
    return otherwise(constructorCall);
  }

  default T whenInteraction(InteractionState interaction) {
    return otherwise(interaction);
  }

  default T whenOpenActivityBar(OpenActivityBarState openActivityBar) {
    return otherwise(openActivityBar);
  }

  default T whenClosedActivityBar(ClosedActivityBarState closedActivityBar) {
    return otherwise(closedActivityBar);
  }

  default T whenFinished(FinishedState finished) {
    return otherwise(finished);
  }

  T otherwise(VisitorState state);
}
