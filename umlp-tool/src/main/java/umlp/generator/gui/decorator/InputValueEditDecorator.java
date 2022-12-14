package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.List;
import java.util.Optional;

public class InputValueEditDecorator extends GUIComponentDecorator {

  public InputValueEditDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTFieldAccessExpression> allZuweisungen = viewComponents.getFieldAccess();

    allZuweisungen.forEach(f -> {
          if (f.getExpression() instanceof ASTNameExpression) {
            String variableName = ((ASTNameExpression) f.getExpression()).getName();
            Optional<GUIParamSymbol> symbol = ((GUIDSLScope) f.getEnclosingScope()).resolveGUIParam(variableName);

            Optional<VariableSymbol> attr = resolveAttribute(symbol, f.getName());

            if (attr.isPresent()) {
              String returnType = attr.get().getType().getTypeInfo().getName();
              String attrName = attr.get().getName();
              String variableType = symbol.get().getType().getTypeInfo().getName();

              generateMethodsForInputValueComponent(decoratedClass, guiComponent, componentName, returnType, variableName, variableType, attrName);
            }
          }
        }
    );

  }

  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                                       String returnType, String variableName, String variableType, String attrName) {
    addComponentErrorAttribute(decoratedClass, guiComponent, componentName);
    addNotifyMethod(decoratedClass, guiComponent, componentName, attrName, variableType, variableName);
    addGetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName);
    addSetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName);
  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, String attrName,
                                 String variableType, String variableName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.input.Notify",
        "notify" + StringTransformations.capitalize(attrName) + "Invalid",
        StringTransformations.uncapitalize(guiComponent.getName() + componentName),variableType, variableName);
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + componentName + "Error";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                       String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent", "get" + guiComponent.getName() + componentName,
        returnType, variableName, attrName);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                       String attrType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName() + componentName,
        attrType, variableName, attrName);
  }

}
