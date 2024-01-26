package umlp.generator.validator.ocl.visitors;

import de.monticore.ocl.ocl._visitor.OCLHandler;
import de.monticore.ocl.ocl._visitor.OCLTraverser;

public class CustomOCLHandler implements OCLHandler {
  OCLTraverser traverser;

  public CustomOCLHandler(OCLTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public OCLTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(OCLTraverser traverser) {
    this.traverser = traverser;
  }
}
