package umlp.generator.domain.visitor.model.ocl;

public final class LetStatement implements OCLStatement {

  private final String qualifiedTypeName;
  private final String name;
  private final String valueExpression;

  public LetStatement(String qualifiedTypeName, String name, String valueExpression) {
    this.qualifiedTypeName = qualifiedTypeName;
    this.name = name;
    this.valueExpression = valueExpression;
  }

  @Override
  public <T> T matchVariant(OCLStatementVariantMatcher<T> matcher) {
    return matcher.whenLetStatement(this);
  }

  public String qualifiedTypeName() {
    return qualifiedTypeName;
  }

  public String name() {
    return name;
  }

  public String valueExpression() {
    return valueExpression;
  }

  @Override
  public String toString() {
    return "LetStatement{" + "qualifiedTypeName='" + qualifiedTypeName + '\'' + ", name='" + name +
           '\'' + ", valueExpression='" + valueExpression + '\'' + '}';
  }
}
