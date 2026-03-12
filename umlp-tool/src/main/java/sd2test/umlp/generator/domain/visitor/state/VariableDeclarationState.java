package sd2test.umlp.generator.domain.visitor.state;

/**
 * Wait for the end of a let statement
 */
public final class VariableDeclarationState implements VisitorState {

  private final OCLAttachableState previousState;

  public VariableDeclarationState(OCLAttachableState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenVariableDeclaration(this);
  }

  public OCLAttachableState previousState() {
    return previousState;
  }

  @Override
  public String toString() {
    return "VariableDeclarationState{" + "previousState=" + previousState + '}';
  }
}
