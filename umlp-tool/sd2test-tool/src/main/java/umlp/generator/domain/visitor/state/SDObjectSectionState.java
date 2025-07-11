package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.SDObject;

import java.util.ArrayList;
import java.util.List;

public final class SDObjectSectionState implements VisitorState {

  private final ImportSection importSection;
  private final List<SDObject> sdObjects = new ArrayList<>();

  public SDObjectSectionState(ImportSection importSection) {
    this.importSection = importSection;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectSection(this);
  }

  public ImportSection importSection() {
    return importSection;
  }

  public List<SDObject> sdObjects() {
    return sdObjects;
  }
}
