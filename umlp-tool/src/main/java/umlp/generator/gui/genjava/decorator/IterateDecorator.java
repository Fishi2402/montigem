package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Optional;

public class IterateDecorator extends GUIAbstractDecorator {

  protected final String guiName;
  protected final GUIComponentsVisitor viewComponent;

  public IterateDecorator(GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.guiName = guiAst.getGUIComponentDeclaration().getName();
    this.viewComponent = viewComponent;
  }

  public void transform(ASTCDClass decoratedClass, ASTGUIIterate iterate) {
    getCD4C().addImport(decoratedClass, "umlp.backendrte.common.GemList;");
    getCD4C().addImport(decoratedClass, "java.util.HashMap;");

    String componentName = viewComponent.getComponentName(iterate);
    String returnType = "";
    String variableName = "";
    String attrName = "";

    if (iterate.getIterateControl() instanceof ASTGUIShortIterateControl) {
      ASTGUIShortIterateControl it = (ASTGUIShortIterateControl) iterate.getIterateControl();
      if (it.getIterable() instanceof ASTFieldAccessExpression) {
        attrName = ((ASTFieldAccessExpression) it.getIterable()).getName();
        String p = ((ASTNameExpression) ((ASTFieldAccessExpression) it.getIterable()).getExpression()).getName();
        Optional<GUIParamSymbol> guiParam = iterate.getEnclosingScope().resolveGUIParam(p);
        if (guiParam.isEmpty()) {
          return;
        }
        variableName = guiParam.get().getName();
        Optional<VariableSymbol> iterateAttribute = resolveAttribute(guiParam, attrName);
        if (iterateAttribute.isEmpty()) {
          return;
        }
        returnType = GUIGeneratorUtil.removeDiagramName(iterateAttribute.get().getType().getTypeInfo().getName());
      }
      else if (it.getIterable() instanceof ASTNameExpression) {
        attrName = ((ASTNameExpression) it.getIterable()).getName();
        Optional<GUIParamSymbol> guiParam = iterate.getEnclosingScope().resolveGUIParam(attrName);
        if (guiParam.isEmpty()) {
          return;
        }
        variableName = guiParam.get().getName();
        Optional<VariableSymbol> iterateAttribute = resolveAttribute(guiParam, attrName);
        if (iterateAttribute.isEmpty()) {
          return;
        }
        returnType = GUIGeneratorUtil.removeDiagramName(iterateAttribute.get().getType().getTypeInfo().getName());
      }
    }

    generateGetMethodForIterate(decoratedClass, iterate, componentName, returnType, variableName, attrName);
  }

  protected void generateGetMethodForIterate(ASTCDClass decoratedClass, ASTGUIIterate iterate, String componentName,
      String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetIterate", "getIterate" +
            StringTransformations.capitalize(componentName) + "It",
        returnType, variableName, attrName);
  }

}
