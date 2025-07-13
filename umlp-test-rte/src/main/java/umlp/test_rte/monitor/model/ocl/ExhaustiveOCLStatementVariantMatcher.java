package umlp.test_rte.monitor.model.ocl;

public interface ExhaustiveOCLStatementVariantMatcher<T> extends OCLStatementVariantMatcher<T> {

  @Override
  T whenCondition(OCLCondition condition);

  @Override
  T whenVariableDeclaration(OCLVariableDeclaration variableDeclaration);

  @Override
  default T otherwise(OCLStatement statement) {
    throw new IllegalArgumentException("Unexpected variant: " + statement);
  }
}
