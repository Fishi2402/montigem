package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.se_rwth.commons.StringTransformations;

public class NewInputValueDecorator extends InputValueDecorator {

  public NewInputValueDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.input.Notify",
        "notify" + StringTransformations.capitalize(attrName) + "Invalid", StringTransformations.uncapitalize(guiComponent.getName()));
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + componentName + "Error";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, String returnExpr, String returnType) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent", "get" + guiComponent.getName() + componentName,
        returnType, returnExpr);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, String attrType, String attrName, String setExpr) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName() + componentName,
        attrType, attrName, setExpr);
  }

}
