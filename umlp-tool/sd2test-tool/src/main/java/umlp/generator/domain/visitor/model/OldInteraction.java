package umlp.generator.domain.visitor.model;

import umlp.generator.domain.visitor.model.action.Action;
import umlp.generator.domain.visitor.model.interaction.Actor;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public final class OldInteraction {
  private final Actor source;
  private final Actor target;
  private final Action action;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public OldInteraction(Actor source, Actor target, Action action) {
    this.source = source;
    this.target = target;
    this.action = action;
  }

  public Actor source() {
    return source;
  }

  public Actor target() {
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
    return "Interaction{" + "source=" + source + ", target=" + target + ", action=" + action +
           ", oclStatements=" + oclStatements + '}';
  }
}
