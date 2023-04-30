/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIIterate;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.Optional;

public class InputValueCreateDecorator extends InputValueEditDecorator {

  public InputValueCreateDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex, guiAst, viewComponent);
  }

  /*
  @Override
  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent,
      String componentName, String returnType, String variableName, String variableType, String attrName, Optional<ASTGUIIterate> iterate) {
    addGetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName, iterate);
    addSetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName, iterate);
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponentBuilder", "get" + guiComponent.getName() + componentName + VALUE,
        returnType, variableName, attrName);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String attrType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponentBuilder", "set" + guiComponent.getName() + componentName + VALUE,
        attrType, variableName, attrName);
  }
  /**/

}
