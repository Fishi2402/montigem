package umlp.generator.domain.visitor.model;

import umlp.generator.domain.visitor.model.action.Action;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

public final class Interaction {
  private final InteractionEntity source;
  private final InteractionEntity target;
  private final Action action;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public Interaction(InteractionEntity source, InteractionEntity target, Action action) {
    this.source = source;
    this.target = target;
    this.action = action;
  }

  public InteractionEntity source() {
    return source;
  }

  public InteractionEntity target() {
    return target;
  }

  public Action action() {
    return action;
  }

  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }
}
