/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.List;

public class InputValueEditDecorator extends GUI2JavaComponentDecorator {

  public InputValueEditDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTFieldAccessExpression> allZuweisungen = viewComponents.getFieldAccess();

    allZuweisungen.forEach(f -> handleZuweisung(f, decoratedClass, guiComponent, componentName, true));
  }

  @Override
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
        StringTransformations.uncapitalize(guiComponent.getName() + componentName), variableType, variableName);
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + componentName + VALUE + "Error";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent", "get" + guiComponent.getName() + componentName + VALUE,
        returnType, variableName, attrName);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String attrType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName() + componentName + VALUE,
        attrType, variableName, attrName);
  }

}
