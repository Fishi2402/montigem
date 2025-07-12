package umlp.generator.domain.visitor.model;

import java.util.ArrayList;
import java.util.List;

public final class Trigger {
  private final Interaction triggerInteraction;
  private final List<Interaction> resultingInteractions = new ArrayList<>();

  public Trigger(Interaction triggerInteraction) {
    this.triggerInteraction = triggerInteraction;
  }

  public Interaction triggerInteraction() {
    return triggerInteraction;
  }

  public List<Interaction> resultingInteractions() {
    return resultingInteractions;
  }

  @Override
  public String toString() {
    return "Trigger{" + "triggerInteraction=" + triggerInteraction + ", resultingInteractions=" +
           resultingInteractions + '}';
  }
}
