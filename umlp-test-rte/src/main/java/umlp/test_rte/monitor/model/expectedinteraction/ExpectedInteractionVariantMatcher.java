package umlp.test_rte.monitor.model.expectedinteraction;

public interface ExpectedInteractionVariantMatcher<T, E extends Throwable> {
  default T whenMethodCall(ExpectedMethodCall expectedMethodCall) throws E {
    return otherwise(expectedMethodCall);
  }

  default T whenConstructorCall(ExpectedConstructorCall expectedConstructorCall) throws E {
    return otherwise(expectedConstructorCall);
  }

  default T whenReturn(ExpectedReturn expectedReturn) throws E {
    return otherwise(expectedReturn);
  }

  default T whenThrow(ExpectedThrow expectedThrow) throws E {
    return otherwise(expectedThrow);
  }

  default T whenEndOfActivityBar(ExpectedEndOfActivityBar expectedEndOfActivityBar) throws E {
    return otherwise(expectedEndOfActivityBar);
  }

  T otherwise(ExpectedInteraction expectedInteraction) throws E;
}
