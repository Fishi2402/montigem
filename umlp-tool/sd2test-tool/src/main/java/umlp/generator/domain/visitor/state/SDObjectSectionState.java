package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.SDObject;

import java.util.ArrayList;
import java.util.List;

/**
 * waiting for declaration of sd objects or start of the body
 */
public final class SDObjectSectionState implements VisitorState {

  private final DiagramMatchingStrategyState diagramMatchingStrategy;
  private final List<SDObject> sdObjects = new ArrayList<>();

  public SDObjectSectionState(DiagramMatchingStrategyState diagramMatchingStrategy) {
    this.diagramMatchingStrategy = diagramMatchingStrategy;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectSection(this);
  }

  public DiagramMatchingStrategyState diagramMatchingStrategy() {
    return diagramMatchingStrategy;
  }

  public List<SDObject> sdObjects() {
    return sdObjects;
  }

  @Override
  public String toString() {
    return "SDObjectSectionState{" + "diagramMatchingStrategy=" + diagramMatchingStrategy +
           ", sdObjects=" + sdObjects + '}';
  }
}
