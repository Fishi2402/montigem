package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.SDObject;

import java.util.ArrayList;
import java.util.List;

/**
 * waiting for declaration of sd objects or start of the body
 */
public final class SDObjectSectionState implements VisitorState {

  private final DiagramState diagramState;
  private final List<SDObject> sdObjects = new ArrayList<>();

  public SDObjectSectionState(DiagramState diagramState) {
    this.diagramState = diagramState;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectSection(this);
  }

  public DiagramState diagramState() {
    return diagramState;
  }

  public List<SDObject> sdObjects() {
    return sdObjects;
  }

  @Override
  public String toString() {
    return "SDObjectSectionState{" + "diagramState=" + diagramState + ", sdObjects=" + sdObjects +
           '}';
  }
}
