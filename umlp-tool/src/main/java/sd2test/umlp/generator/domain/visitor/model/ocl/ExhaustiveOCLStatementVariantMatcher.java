package sd2test.umlp.generator.domain.visitor.model.ocl;

public interface ExhaustiveOCLStatementVariantMatcher<T> extends OCLStatementVariantMatcher<T> {

  @Override
  T whenLetStatement(LetStatement letStatement);

  @Override
  T whenAssertStatement(AssertStatement assertStatement);

  @Override
  default T otherwise(OCLStatement statement) {
    throw new IllegalArgumentException("unknown variant: " + statement);
  }
}
