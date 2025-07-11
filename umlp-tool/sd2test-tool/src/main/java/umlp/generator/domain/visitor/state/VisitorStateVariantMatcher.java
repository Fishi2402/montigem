package umlp.generator.domain.visitor.state;

public interface VisitorStateVariantMatcher<T> {

  default T whenImportSection(ImportSection importSection) {
    return otherwise(importSection);
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

  default T whenEarlyBodySection(EarlyBodySectionState earlyBodySectionState) {
    return otherwise(earlyBodySectionState);
  }

  default T whenVariableDeclaration(VariableDeclarationState variableDeclarationState) {
    return otherwise(variableDeclarationState);
  }

  default T whenBodySection(BodySectionState bodySectionState) {
    return otherwise(bodySectionState);
  }

  default T whenAwaitingTriggerSection(AwaitingTriggerSectionState awaitingTriggerSectionState) {
    return otherwise(awaitingTriggerSectionState);
  }

  default T whenTriggerSection(TriggerSectionState triggerSectionState) {
    return otherwise(triggerSectionState);
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

  T otherwise(VisitorState state);
}
