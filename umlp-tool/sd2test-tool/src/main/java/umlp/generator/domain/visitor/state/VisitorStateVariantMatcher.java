package umlp.generator.domain.visitor.state;

public interface VisitorStateVariantMatcher<T> {

  default T whenImportSection(ImportSectionState importSectionState) {
    return otherwise(importSectionState);
  }

  default T whenImport(ImportState importState) {
    return otherwise(importState);
  }

  default T whenSDObjectSection(SDObjectSectionState objectSection) {
    return otherwise(objectSection);
  }

  default T whenSDObjectDeclaration(SDObjectDeclarationState sdObjectDeclarationState) {
    return otherwise(sdObjectDeclarationState);
  }

  default T whenPreInteractionBody(PreInteractionBodyState preInteractionBodyState) {
    return otherwise(preInteractionBodyState);
  }

  default T whenVariableDeclaration(VariableDeclarationState variableDeclarationState) {
    return otherwise(variableDeclarationState);
  }

  default T whenBodySection(BodySectionState bodySectionState) {
    return otherwise(bodySectionState);
  }

  default T whenSendMessageAwaitSource(SendMessageAwaitSourceState sendMessageAwaitSourceState) {
    return otherwise(sendMessageAwaitSourceState);
  }

  default T whenSendMessageAwaitTarget(SendMessageAwaitTargetState sendMessageAwaitTargetState) {
    return otherwise(sendMessageAwaitTargetState);
  }

  default T whenEndMessageAwaitTarget(EndMessageAwaitTargetState endMessageAwaitTargetState) {
    return otherwise(endMessageAwaitTargetState);
  }

  default T whenEndMessageAwaitSource(EndMessageAwaitSourceState endMessageAwaitSourceState) {
    return otherwise(endMessageAwaitSourceState);
  }

  default T whenAwaitAction(AwaitActionState awaitActionState) {
    return otherwise(awaitActionState);
  }

  default T whenCallAction(CallActionState callActionState) {
    return otherwise(callActionState);
  }

  default T whenInteraction(InteractionState interactionState) {
    return otherwise(interactionState);
  }

  default T whenOpenActivityBar(OpenActivityBarState openActivityBarState) {
    return otherwise(openActivityBarState);
  }

  default T whenClosedActivityBar(ClosedActivityBarState closedActivityBarState) {
    return otherwise(closedActivityBarState);
  }

  T otherwise(VisitorState state);
}
