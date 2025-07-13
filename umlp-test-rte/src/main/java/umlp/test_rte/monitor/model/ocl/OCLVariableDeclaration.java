package umlp.test_rte.monitor.model.ocl;

public final class OCLVariableDeclaration implements OCLStatement {

  private final Runnable execution;

  public OCLVariableDeclaration(Runnable execution) {
    this.execution = execution;
  }

  @Override
  public <T> T matchVariant(OCLStatementVariantMatcher<T> matcher) {
    return matcher.whenVariableDeclaration(this);
  }

  public void execute() {
    execution.run();
  }
}
