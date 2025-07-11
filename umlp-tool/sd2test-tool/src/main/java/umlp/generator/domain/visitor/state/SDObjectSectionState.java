package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.SDObject;

import java.util.ArrayList;
import java.util.List;

/**
 * waiting for declaration of sd objects or start of the body
 */
public final class SDObjectSectionState implements VisitorState {

  private final ImportSectionState importSection;
  private final List<SDObject> sdObjects = new ArrayList<>();

  public SDObjectSectionState(ImportSectionState importSection) {
    this.importSection = importSection;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenSDObjectSection(this);
  }

  public ImportSectionState importSection() {
    return importSection;
  }

  public List<SDObject> sdObjects() {
    return sdObjects;
  }
}
