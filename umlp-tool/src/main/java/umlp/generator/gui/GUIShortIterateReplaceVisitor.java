/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIRangeIterateControl;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._ast.ASTGUIVariableDeclaration;
import de.monticore.guidsl._ast.ASTIterateControl;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GUIShortIterateReplaceVisitor implements GUIDSLVisitor2, ExpressionsBasisVisitor2 {

  protected ASTGUIIterate iterate;
  protected ASTIterateControl iterateControl;

  protected Map.Entry<ASTNode, ASTExpression> potentialItOldHolder;
  protected boolean active = false;
  protected String itOld;
  protected ASTExpression itNew;

  protected GUIDSLTraverser traverser;

  public GUIShortIterateReplaceVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4ExpressionsBasis(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTGUIIterate node) {
    node.accept(getTraverser());
    iterate = node;
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (node.equals(iterate)) {
      active = true;
      iterateControl = node.getIterateControl();
    }
  }

  @Override
  public void visit(ASTGUIShortIterateControl node) {
    if (!node.equals(iterateControl)) {
      if (active) {
        potentialItOldHolder = Map.entry(node, node.getIterable());
      }
      return;
    }
    itOld = node.getItem().getName();

    ASTGUIVariableDeclaration iVar = GUIDSLMill.gUIVariableDeclarationBuilder()
        .setName("i" + iterate.getId())
        .build();

    ASTExpression itCounter = ExpressionsBasisMill.nameExpressionBuilder()
        .setName(iVar.getName())
        .build();

    itNew = CommonExpressionsMill.callExpressionBuilder()
        .setExpression(
            CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setExpression(node.getIterable())
                .setName("get")
                .build()
        )
        .setArguments(
            ExpressionsBasisMill.argumentsBuilder()
                .setExpressionsList(Arrays.asList(itCounter))
                .build()
        )
        .build();

    ASTCallExpression collectionSize = CommonExpressionsMill.callExpressionBuilder()
        .setExpression(
            CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setExpression(node.getIterable())
                .setName("size")
                .build()
        )
        .setArguments(
            ExpressionsBasisMill.argumentsBuilder()
                .setExpressionsList(Collections.emptyList())
                .build()
        )
        .build();

    ASTGUIRangeIterateControl control = GUIDSLMill.gUIRangeIterateControlBuilder()
        .setIndex(iVar)
        .setStart(
            CommonExpressionsMill.literalExpressionBuilder()
                .setLiteral(
                    MCCommonLiteralsMill.natLiteralBuilder()
                        .setDigits("0")
                        .build()
                )
                .build()
        )
        .setEnd(
            CommonExpressionsMill.minusExpressionBuilder()
                .setLeft(collectionSize)
                .setRight(
                    CommonExpressionsMill.literalExpressionBuilder()
                        .setLiteral(
                            MCCommonLiteralsMill.natLiteralBuilder()
                                .setDigits("1")
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .build();

    iterate.setIterateControl(control);
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (!active) {
      return;
    }
    if (potentialItOldHolder.getValue().equals(node)) {
      GUIShortIterateItemReplaceVisitor visitor = new GUIShortIterateItemReplaceVisitor();
      visitor.transform(potentialItOldHolder.getKey(), itNew, node);
    }
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (node.equals(iterate)) {
      active = false;
    }
  }

}
