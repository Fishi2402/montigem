package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReturnInteraction implements Interaction {

  private final Actor source;
  private final Actor target;
  private final Optional<ReturnValue> returnValue;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public ReturnInteraction(Actor source, Actor target, Optional<ReturnValue> returnValue) {
    this.source = source;
    this.target = target;
    this.returnValue = returnValue;
  }

  @Override
  public boolean isComplete() {
    return returnValue.isEmpty() || returnValue.get().isComplete();
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T> T matchVariant(InteractionVariantMatcher<T> matcher) {
    return matcher.whenReturn(this);
  }

  public Actor source() {
    return source;
  }

  public Actor target() {
    return target;
  }

  public Optional<ReturnValue> returnValue() {
    return returnValue;
  }

  @Override
  public String toString() {
    return "ReturnInteraction{" + "source=" + source + ", target=" + target + ", returnValue=" +
           returnValue + ", oclStatements=" + oclStatements + '}';
  }
}
