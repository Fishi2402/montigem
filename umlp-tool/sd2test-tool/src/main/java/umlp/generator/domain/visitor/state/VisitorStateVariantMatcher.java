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
  default T whenTriggerSection(BodySection bodySection) {
    return otherwise(bodySection);
  }
  T otherwise(VisitorState state);
}
