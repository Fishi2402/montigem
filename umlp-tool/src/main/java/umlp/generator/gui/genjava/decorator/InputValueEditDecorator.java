/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentAccessVisitor;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InputValueEditDecorator extends GUI2JavaComponentDecorator {

  public InputValueEditDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex, guiAst, viewComponent);
  }

  /*
  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTFieldAccessExpression> allZuweisungen = viewComponents.getFieldAccess();
    String componentName = viewComponent.getComponentName(guiComponent);
    Optional<ASTGUIIterate> iterate = viewComponent.getIterateForComponent(guiComponent);
    allZuweisungen.forEach(f -> handleZuweisung(f, decoratedClass, guiComponent, componentName, true, iterate));
  }

  @Override
  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String variableType, String attrName, Optional<ASTGUIIterate> iterate) {
    addComponentErrorAttribute(decoratedClass, guiComponent, componentName, iterate);
    addNotifyMethod(decoratedClass, guiComponent, componentName, variableType, variableName, attrName, iterate);
    addGetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName, iterate);
    addSetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName, iterate);
  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String variableType, String variableName, String attrName, Optional<ASTGUIIterate> iterate) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.input.Notify",
        "notify" + StringTransformations.capitalize(attrName) + "Invalid",
        StringTransformations.uncapitalize(guiComponent.getName() + componentName), variableType, variableName, iterate.isPresent());
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, Optional<ASTGUIIterate> iterate) {
    if (iterate.isEmpty()) {
      String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + componentName + VALUE + "Error";
      getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
    } else {
      String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + componentName + VALUE + "Error";
      getCD4C().addAttribute(decoratedClass, false, false, "protected Map<Long, String> " + componentAttributeName + "; //TODO MB: Can't be initialized");
      getCD4C().addMethod(decoratedClass, "umlp.gui2java.input.GetErrorMap",
          "get" + StringTransformations.capitalize(componentAttributeName), componentAttributeName);
    }
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String attrName, Optional<ASTGUIIterate> iterate) {
    if (iterate.isEmpty()) {
      getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent", "get" + guiComponent.getName() + componentName + VALUE,
          returnType, variableName, attrName);
    } else {
      variableName = StringTransformations.capitalize(viewComponent.getComponentName(iterate.get()));
      getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponentIterate", "get" + guiComponent.getName() + componentName + VALUE,
          returnType, variableName, attrName);
    }
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String attrType, String variableName, String attrName, Optional<ASTGUIIterate> iterate) {
    if (iterate.isEmpty()) {
      getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName() + componentName + VALUE,
          attrType, variableName, attrName);
    } else {
      variableName = StringTransformations.capitalize(viewComponent.getComponentName(iterate.get()));
      getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponentIterate", "set" + guiComponent.getName() + componentName + VALUE,
          attrType, variableName, attrName);
    }

  }
  /**/

}
