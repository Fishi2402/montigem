package umlp.generator.domain.visitor.state;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

/**
 * We are waiting for an import statement to finish
 */
public final class ImportState implements VisitorState {

  private final ImportSectionState section;
  private final ASTMCImportStatement importStatement;

  public ImportState(ImportSectionState section, ASTMCImportStatement importStatement) {
    this.section = section;
    this.importStatement = importStatement;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenImport(this);
  }

  public ImportSectionState section() {
    return section;
  }

  public ASTMCImportStatement importStatement() {
    return importStatement;
  }

  @Override
  public String toString() {
    return "ImportState{" + "section=" + section + ", importStatement=" + importStatement + '}';
  }
}
