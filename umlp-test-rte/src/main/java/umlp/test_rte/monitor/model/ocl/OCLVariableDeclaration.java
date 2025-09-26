package umlp.test_rte.monitor.model.ocl;

public final class OCLVariableDeclaration implements OCLStatement {

  private final Runnable execution;

  private OCLVariableDeclaration(Runnable execution) {
    this.execution = execution;
  }

  public static OCLVariableDeclaration fromExecutor(Runnable execution) {
    return new OCLVariableDeclaration(execution);
  }

  @Override
  public <T, E extends Throwable> T matchVariant(OCLStatementVariantMatcher<T, E> matcher) throws E {
    return matcher.whenVariableDeclaration(this);
  }

  public void execute() {
    execution.run();
  }
}
