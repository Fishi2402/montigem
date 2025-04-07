package bin;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development._visitor.SD4DevelopmentTraverser;
import de.monticore.lang.sd4development._visitor.SD4DevelopmentVisitor2;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.monticore.ocl.oclexpressions.OCLExpressionsMill;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;

public interface CompleteSDVisitor extends SDBasisVisitor2, SD4DevelopmentVisitor2, OCLExpressionsVisitor2 {

  // TODO: wahrscheinlich nicht in diesem Interface, sondern in einem Decorator o.Ä.
  public static void apply(ASTSDArtifact model){
    SD4DevelopmentTraverser traverser = SD4DevelopmentMill.traverser();
    CompleteSDVisitor visitor = new CompleteSDVisitor() {
      // ...
    };
    traverser.add4SDBasis(visitor);
    traverser.add4SD4Development(visitor);

    model.accept(traverser);

    // Erstmal als workaround für expressions:
    ASTExpression exp = null;
    String s = OCLExpressionsMill.prettyPrint(exp, false);
  }
}
