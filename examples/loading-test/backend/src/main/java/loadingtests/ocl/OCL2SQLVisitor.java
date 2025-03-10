package loadingtests.ocl;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class OCL2SQLVisitor implements SetExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected OCLTraverser traverser;
  protected final QueryBuilder queryBuilder;

  public OCL2SQLVisitor() {
    traverser = OCLMill.traverser();
    traverser.add4SetExpressions(this);
    traverser.add4ExpressionsBasis(this);
    queryBuilder = new QueryBuilder();
  }

  public String getQuery(ASTSetComprehension setComprehension) {
    setComprehension.accept(traverser);
    return queryBuilder.build();
  }

  @Override
  public void visit(ASTSetComprehension node) {
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {

    }
  }

  @Override
  public void visit(ASTGeneratorDeclaration node) {

  }

  @Override
  public void visit(ASTNameExpression node) {
    queryBuilder.select("*").from(node.getName());
  }
}