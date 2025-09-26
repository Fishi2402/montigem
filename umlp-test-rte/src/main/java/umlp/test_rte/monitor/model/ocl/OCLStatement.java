package umlp.test_rte.monitor.model.ocl;

public interface OCLStatement {
  <T, E extends Throwable> T matchVariant(OCLStatementVariantMatcher<T, E> matcher) throws E;
}
