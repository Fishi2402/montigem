/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;

import java.util.Optional;

public class IterableExtractVisitor implements CommonExpressionsVisitor2 {

  protected GUIDSLTraverser traverser;
  protected Optional<ASTExpression> out = Optional.empty();

  protected static IterableExtractVisitor instance;
  private IterableExtractVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4CommonExpressions(this);
  }

  public static IterableExtractVisitor getInstance() {
    if (instance == null) {
      instance = new IterableExtractVisitor();
    }
    return instance;
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public Optional<ASTExpression> getOut() {
    return out;
  }

  @Override
  public void visit(ASTFieldAccessExpression node) {
    if (node.getName().equals("size")) {
      out = Optional.of(node.getExpression());
    }
  }

}
