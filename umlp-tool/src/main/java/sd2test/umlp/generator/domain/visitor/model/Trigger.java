package sd2test.umlp.generator.domain.visitor.model;

import sd2test.umlp.generator.domain.visitor.model.interaction.Interaction;
import sd2test.umlp.generator.domain.visitor.model.interaction.MethodCallInteraction;

import java.util.ArrayList;
import java.util.List;

public final class Trigger {
  private final MethodCallInteraction triggerInteraction;
  private final List<Interaction> resultingInteractions = new ArrayList<>();

  public Trigger(MethodCallInteraction triggerInteraction) {
    this.triggerInteraction = triggerInteraction;
  }

  public MethodCallInteraction triggerInteraction() {
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
