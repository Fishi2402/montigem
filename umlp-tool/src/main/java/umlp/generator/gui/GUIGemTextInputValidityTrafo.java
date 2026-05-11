/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;

public class GUIGemTextInputValidityTrafo implements GUIDSLVisitor2, CommonExpressionsVisitor2, ExpressionsBasisVisitor2,
    MCCommonLiteralsVisitor2, SetExpressionsVisitor2 {

  protected GUIDSLTraverser traverser;

  public GUIGemTextInputValidityTrafo() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4CommonExpressions(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4SetExpressions(this);
    traverser.add4MCCommonLiterals(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTGUICompilationUnit ast) {
    ast.accept(getTraverser());
  }

  @Override
  public void visit(ASTGUIComponent node) {
    if ("GemTextInput".equals(node.getName())) {
      boolean hasValidity = node.getGUINamedArgs().getGUINamedArgList().stream()
          .anyMatch(arg -> "validity".equals(arg.getName()));

      if (!hasValidity) {
        ASTExpression nullExpr = CommonExpressionsMill.literalExpressionBuilder()
            .setLiteral(MCCommonLiteralsMill.nullLiteralBuilder().build())
            .build();

        ASTGUINamedArg validityArg = GUIDSLMill.gUINamedArgBuilder()
            .setName("validity")
            .setExpression(nullExpr)
            .build();

        node.getGUINamedArgs().getGUINamedArgList().add(validityArg);
      }
    }
  }
}
