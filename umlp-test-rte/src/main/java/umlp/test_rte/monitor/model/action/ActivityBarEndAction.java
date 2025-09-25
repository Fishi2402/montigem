package umlp.test_rte.monitor.model.action;

@Deprecated
public final class ActivityBarEndAction implements Action {
  @Override
  public <T> T matchVariant(ActionVariantMatcher<T> matcher) {
    return matcher.whenActivityBarEnd(this);
  }
}
