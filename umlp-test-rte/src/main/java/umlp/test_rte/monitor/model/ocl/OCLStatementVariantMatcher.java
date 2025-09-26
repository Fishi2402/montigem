package umlp.test_rte.monitor.model.ocl;

public interface OCLStatementVariantMatcher<T, E extends Throwable> {
  default T whenCondition(OCLCondition condition) throws E {
    return otherwise(condition);
  }

  default T whenVariableDeclaration(OCLVariableDeclaration variableDeclaration) throws E {
    return otherwise(variableDeclaration);
  }

  T otherwise(OCLStatement statement) throws E;
}
