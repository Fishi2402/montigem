package umlp.test_rte.monitor.model.ocl;

public interface OCLStatementVariantMatcher<T> {
  default T whenCondition(OCLCondition condition) {
    return otherwise(condition);
  }

  default T whenVariableDeclaration(OCLVariableDeclaration variableDeclaration) {
    return otherwise(variableDeclaration);
  }

  T otherwise(OCLStatement statement);
}
