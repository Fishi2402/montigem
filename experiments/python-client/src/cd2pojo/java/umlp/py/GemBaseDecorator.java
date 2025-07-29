package umlp.py;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd.codegen.decorators.data.AbstractDecorator;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDTypeFacade;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GemBaseDecorator extends AbstractDecorator implements CDBasisVisitor2 {

  protected SymTypeExpression _stringSymTypeExpression;
  protected CDGenService cdGenService = new CDGenService();

  protected SymTypeExpression getString() {
    if (_stringSymTypeExpression == null) {
      _stringSymTypeExpression = SymTypeExpressionFactory.createStringType();
    }
    return _stringSymTypeExpression;
  }

  @Override
  public void visit(ASTCDClass node) {
    // TODO: check if should decorate
    var decNode = decoratorData.getAsDecorated(node);
    if (node.getSuperclassList().isEmpty()) {
      if (!decNode.isPresentCDExtendUsage()) {
        decNode.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().uncheckedBuild());
      }

      decNode.getCDExtendUsage().addSuperclass(
          MCTypeFacade.getInstance().createQualifiedType("montigem_runtime.gembase.GemBase"));
    }

    decNode.addCDMember(CDAttributeFacade.getInstance()
                            .createAttribute(CDBasisMill.modifierBuilder().build(),
                                             createClassVar("str"), "TYPE",
                                             stringLiteralExpr(node.getName())));

    decNode.addCDMember(CDAttributeFacade.getInstance()
                            .createAttribute(CDBasisMill.modifierBuilder().build(),
                                             createClassVar("str"), "PACKAGE",
                                             stringLiteralExpr("mydomain"))); // TODO

  }

  @Override
  public void visit(ASTCDAttribute originalNode) {
    ASTCDAttribute decNode = decoratorData.getAsDecorated(originalNode);
    if (MCCollectionSymTypeRelations.isOptional(originalNode.getSymbol().getType())) {
      // Attribute is an Optional (field or assoc)
      var inner = MCCollectionSymTypeRelations.getCollectionElementType(
          originalNode.getSymbol().getType());

      ASTMCTypeArgument typeArgument;

      if (SymTypeRelations.isCompatible(inner, getString())) {
        // Attribute is a String -> turn into typing.Optional[str]
        typeArgument = MCTypeFacade.getInstance().createBasicTypeArgumentOf("str");
        decNode.setMCType(MCTypeFacade.getInstance()
                              .createBasicGenericTypeOf(Arrays.asList("typing", "Optional"),
                                                        Collections.singletonList(typeArgument)));
      } else if (inner.isPrimitive()) {
        // Attribute is a primitive -> turn into typing.Optional[...]
        typeArgument = MCTypeFacade.getInstance().createBasicTypeArgumentOf(inner.print());
        decNode.setMCType(MCTypeFacade.getInstance()
                              .createBasicGenericTypeOf(Arrays.asList("typing", "Optional"),
                                                        Collections.singletonList(typeArgument)));
      } else {
        // Attribute is an optional domain class -> turn into Gem
        CD4C.getInstance().addImport(
            decoratorData.getAsDecorated((ASTCDClass) decoratorData.getParent(originalNode).get()),
            inner.printFullName());
        decNode.setMCType(createGemType(inner.printFullName()));
      }


    } else if (MCCollectionSymTypeRelations.isSet(originalNode.getSymbol().getType())) {
      Log.warn("TODO set");
    } else if (MCCollectionSymTypeRelations.isList(originalNode.getSymbol().getType())) {
      Log.warn("TODO list");

    } else if (SymTypeRelations.isCompatible(originalNode.getSymbol().getType(), getString())) {
      // Change String to "str"
      decNode.setMCType(MCTypeFacade.getInstance().createQualifiedType("str"));
    } else if (!isBuiltIn(originalNode.getSymbol().getType())) {
      CD4C.getInstance().addImport(
          decoratorData.getAsDecorated((ASTCDClass) decoratorData.getParent(originalNode).get()),
          originalNode.getSymbol().getType().printFullName());
      decNode.setMCType(createGemType(originalNode.getSymbol().getType().printFullName()));
    }
  }

  protected ASTMCType createGemType(String classname) {
    return MCTypeFacade.getInstance()
        .createBasicGenericTypeOf("typing.Annotated", createGemTypeAs(classname));
  }

  protected String[] createGemTypeAs(String classname) {
    return new String[]{"montigem_runtime.gembase.Gem[" + classname + "]", "pydantic.BeforeValidator(montigem_runtime.gembase.create_gem_deserializer(" + classname + "))", "pydantic.PlainSerializer(montigem_runtime.gembase.serialize_gem,return_type=int)"};
  }

  protected boolean isBuiltIn(SymTypeExpression type) {
    return type.isPrimitive() || SymTypeRelations.isCompatible(type, getString());
  }

  protected ASTMCBasicGenericType createClassVar(String t) {
    return MCTypeFacade.getInstance().createBasicGenericTypeOf("typing.ClassVar",
                                                               MCTypeFacade.getInstance()
                                                                   .createBasicTypeArgumentOf(t));
  }


  protected ASTExpression stringLiteralExpr(String v) {
    return CD4CodeMill.literalExpressionBuilder()
        .setLiteral(CD4CodeMill.stringLiteralBuilder().setSource(v).build()).build();
  }

  @Override
  public void addToTraverser(CD4CodeTraverser traverser) {
    traverser.add4CDBasis(this);
  }
}
