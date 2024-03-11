package umlp.generator.validator.ocl.visitors;

import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;

public class CustomCommonExpressionsHandler implements CommonExpressionsHandler {
  CommonExpressionsTraverser traverser;

  public CustomCommonExpressionsHandler(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
