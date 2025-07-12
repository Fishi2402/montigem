package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.SDTest;
import umlp.generator.domain.visitor.model.SDTestExtractable;
import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are expecting interactions or ocl statements, which should be checked before any interactions
 * happen
 */
public final class PreInteractionBodyState implements OCLState, SDTestExtractable {

  private final SDObjectSectionState objectSection;
  private final List<OCLStatement> oclStatements = new ArrayList<>();

  public PreInteractionBodyState(SDObjectSectionState objectSection) {
    this.objectSection = objectSection;
  }

  @Override
  public List<OCLStatement> oclStatements() {
    return oclStatements;
  }

  @Override
  public <T> T matchVariant(VisitorStateVariantMatcher<T> matcher) {
    return matcher.whenPreInteractionBody(this);
  }

  public SDObjectSectionState objectSection() {
    return objectSection;
  }

  @Override
  public String toString() {
    return "PreInteractionBodyState{" + "objectSection=" + objectSection + ", oclStatements=" +
           oclStatements + '}';
  }

  @Override
  public SDTest extractTest() {
    var test = new SDTest();
    test.imports().addAll(objectSection.importSection().imports());
    test.sdObjects().addAll(objectSection.sdObjects());
    test.preInteractionOCLStatements().addAll(oclStatements);
    return test;
  }
}
