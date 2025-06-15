package umlp.py;

import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.Arrays;
import java.util.Collections;

public class GemBaseDecorator extends AbstractDecorator implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDClass node) {
    // TODO: check
    var decNode = decoratorData.getAsDecorated(node);
    if (node.getSuperclassList().isEmpty()) {
      if (!decNode.isPresentCDExtendUsage())
        decNode.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().uncheckedBuild());

      decNode.getCDExtendUsage().addSuperclass(MCTypeFacade.getInstance().createQualifiedType("GemBase"));
    }

    decNode.addCDMember(CDAttributeFacade.getInstance().createAttribute(CDBasisMill.modifierBuilder().build(),
        createClassVar("str"), "TYPE", stringLiteralExpr(node.getName())));

    decNode.addCDMember(CDAttributeFacade.getInstance().createAttribute(CDBasisMill.modifierBuilder().build(),
        createClassVar("str"), "PACKAGE", stringLiteralExpr("mydomain"))); // TODO

  }

  protected ASTMCBasicGenericType createClassVar(String t) {
    return MCTypeFacade.getInstance().createBasicGenericTypeOf(Arrays.asList("typing", "ClassVar"),
        Collections.singletonList(MCTypeFacade.getInstance().createBasicTypeArgumentOf(t))
        );
  }

  protected ASTExpression stringLiteralExpr(String v) {
    return CD4CodeMill.literalExpressionBuilder()
        .setLiteral(CD4CodeMill.stringLiteralBuilder().setSource(v).build())
        .build();
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
