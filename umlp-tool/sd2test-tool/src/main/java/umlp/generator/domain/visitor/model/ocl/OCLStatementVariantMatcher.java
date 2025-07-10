package umlp.generator.domain.visitor.model.ocl;

public interface OCLStatementVariantMatcher<T> {
  default T whenLetStatement(LetStatement letStatement) {
    return otherwise(letStatement);
  }
  default T whenAssertStatement(AssertStatement assertStatement) {
    return otherwise(assertStatement);
  }
  T otherwise(OCLStatement statement);
}
