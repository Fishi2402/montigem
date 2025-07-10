package umlp.generator.domain.visitor.state;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.ArrayList;
import java.util.List;

public final class ImportSection implements VisitorState {

  private final List<ASTMCImportStatement> imports = new ArrayList<>();

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenImportSection(this);
  }

  public List<ASTMCImportStatement> imports() {
    return imports;
  }
}
