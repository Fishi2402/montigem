package umlp.generator.domain.visitor.state;

/**
 * We are waiting for an assert statement to finish
 */
public final class AssertStatementState implements VisitorState {

  private final OCLAttachableState previousState;

  public AssertStatementState(OCLAttachableState previousState) {
    this.previousState = previousState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenAssertStatement(this);
  }

  public OCLAttachableState previousState() {
    return previousState;
  }

  @Override
  public String toString() {
    return "AssertStatementState{" + "previousState=" + previousState + '}';
  }
}
