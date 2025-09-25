package umlp.test_rte.monitor.model.expectedinteraction;

import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.List;

public interface ExpectedInteraction {
  List<OCLStatement> oclStatements();
  <T, E extends Throwable> T matchVariant(ExpectedInteractionVariantMatcher<T, E> matcher) throws E;
}
