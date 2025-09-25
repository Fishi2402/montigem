package umlp.test_rte.monitor.model.expectedinteraction;

public interface ExhaustiveExpectedInteractionVariantMatcher<T, E extends Throwable>
    extends ExpectedInteractionVariantMatcher<T, E> {

  @Override
  T whenMethodCall(ExpectedMethodCall expectedMethodCall) throws E;

  @Override
  T whenConstructorCall(ExpectedConstructorCall expectedConstructorCall) throws E;

  @Override
  T whenReturn(ExpectedReturn expectedReturn) throws E;

  @Override
  T whenThrow(ExpectedThrow expectedThrow) throws E;

  @Override
  T whenEndOfActivityBar(ExpectedEndOfActivityBar expectedEndOfActivityBar) throws E;

  @Override
  default T otherwise(ExpectedInteraction expectedInteraction) throws E, IllegalArgumentException {
    throw new IllegalArgumentException("Unexpected variant: " + expectedInteraction);
  }
}
