package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;

public class InputValueCreateDecorator extends InputValueEditDecorator {

  public InputValueCreateDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                                       String returnType, String variableName, String variableType, String attrName) {
    addGetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName);
    addSetComponentMethod(decoratedClass, guiComponent, componentName, returnType, variableName, attrName);
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                       String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponentBuilder", "get" + guiComponent.getName() + componentName,
        returnType, variableName, attrName);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
                                       String attrType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponentBuilder", "set" + guiComponent.getName() + componentName,
        attrType, variableName, attrName);
  }

}
