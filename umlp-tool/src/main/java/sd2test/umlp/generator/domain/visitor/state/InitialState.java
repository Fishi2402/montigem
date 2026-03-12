package sd2test.umlp.generator.domain.visitor.state;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are waiting for imports or the start of the sd
 */
public final class InitialState implements VisitorState {

  private final List<ASTMCImportStatement> imports = new ArrayList<>();

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenInitial(this);
  }

  public List<ASTMCImportStatement> imports() {
    return imports;
  }

  @Override
  public String toString() {
    return "ImportSectionState{" + "imports=" + imports + '}';
  }
}
