package umlp.test_rte.monitor.event;

public interface ExhaustiveStackEventVariantMatcher<T> extends StackEventVariantMatcher<T> {

  @Override
  T whenCallEvent(CallEvent event);

  @Override
  T whenErrorEvent(ErrorEvent event);

  @Override
  T whenReturnEvent(ReturnEvent event);

  @Override
  T whenReturnVoidEvent(ReturnVoidEvent event);

  @Override
  default T otherwise(StackEvent event) {
    throw new IllegalStateException("unknown stack event variant: " + event);
  }
}
