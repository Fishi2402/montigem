/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIRangeIterateControl;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._ast.ASTGUIVariableDeclaration;
import de.monticore.guidsl._ast.ASTIterateControl;
import de.monticore.guidsl._visitor.GUIDSLHandler;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GUIShortIterateReplaceVisitor extends ExpressionReplaceSearchVisitor implements GUIDSLHandler {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  protected ASTGUIIterate iterate;
  protected ASTIterateControl controlOld;

  protected Map.Entry<ASTNode, String> iterableOldHolder;
  protected List<ExpressionReplacement> replacements = new ArrayList<>();

  protected boolean active = false;
  protected Supplier<ASTExpression> itNew;
  protected ASTGUIRangeIterateControl controlNew;

  protected GUIDSLTraverser traverser;

  public GUIShortIterateReplaceVisitor() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
    traverser.add4ExpressionsBasis(this);
    traverser.add4CommonExpressions(this);
    traverser.setCommonExpressionsHandler(this);
    traverser.setGUIDSLHandler(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public void transform(ASTGUIIterate node) {
    iterate = node;
    node.accept(getTraverser());
  }

  @Override
  public void visit(ASTGUIIterate node) {
    if (node.equals(iterate)) {
      controlOld = node.getIterateControl();
    }
  }

  @Override
  public void visit(ASTGUIBlockContent node) {
    if (node.isPresentExpression()) {
      potentialHolder = Map.entry(node, node.getExpression());
    }
  }

  @Override
  public void traverse(ASTGUIRangeIterateControl node) {
    if (null != node.getIndex()) {
      node.getIndex().accept(getTraverser());
    }
    if (null != node.getStart()) {
      potentialHolder = Map.entry(node, node.getStart());
      node.getStart().accept(getTraverser());
    }
    if (null != node.getEnd()) {
      potentialHolder = Map.entry(node, node.getEnd());
      node.getEnd().accept(getTraverser());
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    potentialHolder = Map.entry(node, node.getCondition());
  }

  @Override
  public void visit(ASTGUIShortIterateControl node) {
    if (!node.equals(controlOld)) {
      potentialHolder = Map.entry(node, node.getIterable());
      return;
    }
    iterableOldHolder = Map.entry(node, node.getItem().getName());

    ASTGUIVariableDeclaration iVar = GUIDSLMill.gUIVariableDeclarationBuilder()
        .setName(util.getIteratorCounterName(iterate))
        .build();

    ASTExpression itCounter = ExpressionsBasisMill.nameExpressionBuilder()
        .setName(iVar.getName())
        .build();

    itNew = () -> CommonExpressionsMill.callExpressionBuilder()
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

    controlNew = GUIDSLMill.gUIRangeIterateControlBuilder()
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
                .setOperator("-")
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
  }

  @Override
  public void endVisit(ASTGUIShortIterateControl node) {
    if (!node.equals(controlOld)) {
      return;
    }
    active = true;
    iterate.setIterateControl(controlNew);
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (!active) {
      return;
    }
    if (iterableOldHolder.getValue().equals(node.getName()) && potentialHolder.getValue().equals(node)) {
      replacements.add(new ExpressionReplacement(
          potentialHolder.getKey(), node, itNew));
    }
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (node.equals(iterate)) {
      active = false;
      controlOld = node.getIterateControl();
      for (ExpressionReplacement nr : replacements) {
        ExpressionReplaceVisitor visitor = new ExpressionReplaceVisitor();
        visitor.transform(nr.getHolder(), nr.getOldValue(), nr.getNewValue());
      }
    }
  }

}
