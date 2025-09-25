package umlp.test_rte.monitor.model.action;

@Deprecated
public interface Action {

  <T> T matchVariant(ActionVariantMatcher<T> matcher);
}
