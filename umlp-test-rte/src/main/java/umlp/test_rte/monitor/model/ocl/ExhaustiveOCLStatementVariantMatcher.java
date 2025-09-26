package umlp.test_rte.monitor.model.ocl;

public interface ExhaustiveOCLStatementVariantMatcher<T, E extends Throwable> extends OCLStatementVariantMatcher<T, E> {

  @Override
  T whenCondition(OCLCondition condition) throws E;

  @Override
  T whenVariableDeclaration(OCLVariableDeclaration variableDeclaration) throws E;

  @Override
  default T otherwise(OCLStatement statement) throws E, IllegalArgumentException {
    throw new IllegalArgumentException("Unexpected variant: " + statement);
  }
}
