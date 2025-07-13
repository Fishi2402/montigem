package umlp.test_rte.monitor.model.ocl;

public interface OCLStatement {
  <T> T matchVariant(OCLStatementVariantMatcher<T> matcher);
}
