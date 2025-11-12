package umlp.py;

import com.google.common.collect.Iterables;
import de.monticore.cd.codegen.decorators.IDecorator;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class GemHWDecorator extends AbstractDecorator<AbstractDecorator. NoData> implements CDBasisVisitor2 {


  @Override
  public void visit(ASTCDClass originalNode) {
    for (var comment : originalNode.get_PostCommentList()) {
      if (comment.getText().trim().startsWith("// HW:")) {
        String content = comment.getText().replace("// HW:", "") + "\n";
        var decNode = decoratorData.getAsDecorated(originalNode);
        glexOpt.ifPresent(glex->glex.addAfterTemplate("cd2python.Class::postBody", decNode, new StringHookPoint(content)));
      }

    }
  }

  @Override
  public void visit(ASTCDAttribute originalNode) {
    for (var comment : originalNode.get_PostCommentList()) {
      if (comment.getText().trim().startsWith("// HW:"))
        Log.error("Unhandled hw comment", originalNode.get_SourcePositionStart());
    }
  }


  @Override
  public Iterable<Class<? extends IDecorator>> getMustRunAfter() {
    return Iterables.concat(super.getMustRunAfter(), List.of(GemBaseDecorator.class));
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }


}
