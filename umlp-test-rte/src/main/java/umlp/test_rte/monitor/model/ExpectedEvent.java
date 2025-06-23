package umlp.test_rte.monitor.model;

import umlp.test_rte.monitor.model.action.Action;

import java.util.function.Supplier;

public final class ExpectedEvent {
  private final Object source;
  private final Object target;
  private final Action action;
  private final Supplier<Boolean> condition;
  private final boolean trigger;

  public ExpectedEvent(Object source,
                       Object target,
                       Action action,
                       Supplier<Boolean> condition,
                       boolean trigger) {
    this.source = source;
    this.target = target;
    this.action = action;
    this.condition = condition;
    this.trigger = trigger;
  }
}
