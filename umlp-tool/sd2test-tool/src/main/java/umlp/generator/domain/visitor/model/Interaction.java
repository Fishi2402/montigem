package umlp.generator.domain.visitor.model;

import umlp.generator.domain.visitor.model.action.Action;

public final class Interaction {
  private final String sourceName;
  private final String targetName;
  private final Action action;

  public Interaction(String sourceName, String targetName, Action action) {
    this.sourceName = sourceName;
    this.targetName = targetName;
    this.action = action;
  }

  public String sourceName() {
    return sourceName;
  }

  public String targetName() {
    return targetName;
  }

  public Action action() {
    return action;
  }
}
