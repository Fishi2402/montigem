package umlp.generator.domain.visitor.state;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are waiting for imports or the start of the sd
 */
public final class ImportSectionState implements VisitorState {

  private final List<ASTMCImportStatement> imports = new ArrayList<>();

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenImportSection(this);
  }

  public List<ASTMCImportStatement> imports() {
    return imports;
  }
}
