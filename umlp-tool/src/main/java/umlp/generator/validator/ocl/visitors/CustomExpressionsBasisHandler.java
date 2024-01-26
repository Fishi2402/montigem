package umlp.generator.validator.ocl.visitors;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;

public class CustomExpressionsBasisHandler implements ExpressionsBasisHandler {
  ExpressionsBasisTraverser traverser;

  public CustomExpressionsBasisHandler(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
