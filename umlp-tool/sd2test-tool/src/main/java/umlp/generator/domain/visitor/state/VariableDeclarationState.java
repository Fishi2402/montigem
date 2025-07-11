package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ocl.LetStatement;

/**
 * We are reading an ocl statement which declares a variable
 */
public final class VariableDeclarationState implements VisitorState {

  private final OCLState previousState;
  private final LetStatement letStatement;

  public VariableDeclarationState(OCLState previousState, LetStatement letStatement) {
    this.previousState = previousState;
    this.letStatement = letStatement;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenVariableDeclaration(this);
  }

  public OCLState previousState() {
    return previousState;
  }

  public LetStatement letStatement() {
    return letStatement;
  }
}
