package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.SDTest;
import sd2test.umlp.generator.domain.visitor.model.SDTestExtractable;
import sd2test.umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * We are expecting interactions or ocl statements, which should be checked before any interactions
 * happen
 */
public final class PreInteractionBodyState implements OCLAttachableState, SDTestExtractable {

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
    var objectSection = objectSection();
    var diagramMatchingStrategy = objectSection.diagramState();
    test.imports().addAll(diagramMatchingStrategy.initialState().imports());
    diagramMatchingStrategy.matchingStrategy().ifPresent(test::setMatchingStrategy);
    test.sdObjects().addAll(objectSection.sdObjects());
    test.preInteractionOCLStatements().addAll(oclStatements());
    return test;
  }
}
