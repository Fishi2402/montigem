package sd2test.umlp.generator.domain.visitor.model.ocl;

public interface OCLStatement {
  <T> T matchVariant(OCLStatementVariantMatcher<T> matcher);
}
