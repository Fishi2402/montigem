package umlp.generator.validator.ocl.visitors;

import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsHandler;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsTraverser;

public class CustomOCLExpressionsHandler implements OCLExpressionsHandler {
  OCLExpressionsTraverser traverser;

  public CustomOCLExpressionsHandler(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public OCLExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
