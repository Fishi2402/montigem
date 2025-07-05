package umlp.test_rte.monitor.model.action;

public interface Action {

  <T> T matchVariant(ActionVariantMatcher<T> matcher);
}
