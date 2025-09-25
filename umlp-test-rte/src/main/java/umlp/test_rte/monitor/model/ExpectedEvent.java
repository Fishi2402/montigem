package umlp.test_rte.monitor.model;

import umlp.test_rte.monitor.model.action.Action;
import umlp.test_rte.monitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class ExpectedEvent {

  private final Object source;
  private final Object target;
  private final Action action;
  private final List<OCLStatement> oclStatements;

  public ExpectedEvent(Object source,
      Object target,
      Action action,
      OCLStatement... oclStatements) {
    this.source = source;
    this.target = target;
    this.action = action;
    this.oclStatements = new ArrayList<>(List.of(oclStatements));
  }

  public Object source() {
    return source;
  }

  public Object target() {
    return target;
  }

  public Action action() {
    return action;
  }

  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public String toString() {
    return "ExpectedEvent{" + "source=" + source + ", target=" + target + ", action=" + action +
           ", oclStatements=" + oclStatements + '}';
  }
}
