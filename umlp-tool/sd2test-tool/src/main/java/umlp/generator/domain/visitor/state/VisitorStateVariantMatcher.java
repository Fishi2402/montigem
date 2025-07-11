package umlp.generator.domain.visitor.state;

public interface VisitorStateVariantMatcher<T> {

  default T whenImportSection(ImportSection importSection) {
    return otherwise(importSection);
  }

  default T whenImport(Import importState) {
    return otherwise(importState);
  }

  default T whenSDObjectSection(SDObjectSection objectSection) {
    return otherwise(objectSection);
  }

  default T whenSDObjectDeclaration(SDObjectDeclaration sdObjectDeclaration) {
    return otherwise(sdObjectDeclaration);
  }

  default T whenEarlyBodySection(EarlyBodySection earlyBodySection) {
    return otherwise(earlyBodySection);
  }

  default T whenVariableDeclaration(VariableDeclaration variableDeclaration) {
    return otherwise(variableDeclaration);
  }

  default T whenBodySection(BodySection bodySection) {
    return otherwise(bodySection);
  }

  default T whenAwaitingTriggerSection(AwaitingTriggerSection awaitingTriggerSection) {
    return otherwise(awaitingTriggerSection);
  }

  default T whenTriggerSection(TriggerSection triggerSection) {
    return otherwise(triggerSection);
  }

  default T whenSendMessageAwaitSource(SendMessageAwaitSource sendMessageAwaitSource) {
    return otherwise(sendMessageAwaitSource);
  }

  default T whenSendMessageAwaitTarget(SendMessageAwaitTarget sendMessageAwaitTarget) {
    return otherwise(sendMessageAwaitTarget);
  }

  default T whenEndMessageAwaitTarget(EndMessageAwaitTarget endMessageAwaitTarget) {
    return otherwise(endMessageAwaitTarget);
  }

  default T whenEndMessageAwaitSource(EndMessageAwaitSource endMessageAwaitSource) {
    return otherwise(endMessageAwaitSource);
  }

  default T whenAwaitAction(AwaitAction awaitAction) {
    return otherwise(awaitAction);
  }

  T otherwise(VisitorState state);
}
