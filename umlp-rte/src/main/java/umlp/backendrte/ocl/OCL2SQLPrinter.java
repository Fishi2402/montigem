package umlp.backendrte.ocl;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class OCL2SQLPrinter implements SetExpressionsVisitor2, ExpressionsBasisVisitor2 {

  protected OCLTraverser traverser;
  protected QueryBuilder queryBuilder;

  public OCL2SQLPrinter() {
    traverser = OCLMill.traverser();
    traverser.add4SetExpressions(this);
    traverser.add4ExpressionsBasis(this);
  }

  public String getQuery(ASTSetComprehension setComprehension) {
    setComprehension.accept(traverser);
    return queryBuilder.build();
  }

  @Override
  public void visit(ASTSetComprehension node) {
    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (item.isPresentGeneratorDeclaration()) {
        String set = OCLMill.prettyPrint(item.getGeneratorDeclaration().getExpression(), false);
        Optional<QueryBuilder> qb = QueryBuilderMap.getInstance().getBuilder(set);
        String name = item.getGeneratorDeclaration().getName();
        if (qb.isPresent()) {
          queryBuilder = qb.get();
          queryBuilder.as(name);
        }
        else {
          Log.error("0xB0007 Unable to process generator `" + set
              + "` in the set comprehension\n" + OCLMill.prettyPrint(node, false));
        }
        break;
      }
    }

    for (ASTSetComprehensionItem item : node.getSetComprehensionItemList()) {
      if (item.isPresentExpression()) {
        var expPrinter = new CommonExpressions2SQLFullPrinter(new IndentPrinter(), false);
        String where = expPrinter.prettyprint(item.getExpression());
        queryBuilder.where(where);
      }
    }
  }

}