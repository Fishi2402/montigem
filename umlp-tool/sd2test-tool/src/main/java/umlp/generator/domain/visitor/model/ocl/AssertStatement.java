package umlp.generator.domain.visitor.model.ocl;

public final class AssertStatement implements OCLStatement {

  private final String javaExpression;

  public AssertStatement(String javaExpression) {
    this.javaExpression = javaExpression;
  }

  @Override
  public <T> T matchVariant(OCLStatementVariantMatcher<T> matcher) {
    return matcher.whenAssertStatement(this);
  }

  public String javaExpression() {
    return javaExpression;
  }
}
