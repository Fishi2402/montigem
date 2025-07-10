package umlp.generator.domain.visitor.state;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

public final class Import implements VisitorState {

  private final ImportSection section;
  private final ASTMCImportStatement importStatement;

  public Import(ImportSection section, ASTMCImportStatement importStatement) {
    this.section = section;
    this.importStatement = importStatement;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenImport(this);
  }

  public ImportSection section() {
    return section;
  }

  public ASTMCImportStatement importStatement() {
    return importStatement;
  }
}
