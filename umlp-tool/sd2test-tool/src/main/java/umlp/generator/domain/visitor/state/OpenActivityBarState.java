package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.interaction.Interaction;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are reading the contents of an activity bar
 */
public final class OpenActivityBarState implements OCLAttachableState {

  private final CallState call;
  private final List<Interaction> interactions = new ArrayList<>();

  public OpenActivityBarState(CallState call) {
    this.call = call;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenOpenActivityBar(this);
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return call.oclStatements();
  }

  public CallState call() {
    return call;
  }

  public List<Interaction> interactions() {
    return interactions;
  }

  @Override
  public String toString() {
    return "OpenActivityBarState{" + "call=" + call + ", interactions=" + interactions + '}';
  }
}
