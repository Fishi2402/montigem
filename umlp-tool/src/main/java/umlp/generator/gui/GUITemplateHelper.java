/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIParam;

import java.util.List;

public class GUITemplateHelper {

  protected static GUITemplateHelper instance;

  private GUITemplateHelper() {
  }

  public static GUITemplateHelper getInstance() {
    if (instance == null) {
      instance = new GUITemplateHelper();
    }
    return instance;
  }

  public boolean isInputArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
    return GUIGeneratorUtil.isInputArgument(guiComponent, arg);
  }

  public boolean isEventArgument(ASTGUIComponent guiComponent, ASTGUINamedArg arg) {
    return GUIGeneratorUtil.isEventArgument(guiComponent, arg);
  }

  public List<ASTGUINamedArg> getInputArguments(ASTGUIComponent guiComponent) {
    return GUIGeneratorUtil.getInputArguments(guiComponent);
  }

  public List<ASTGUINamedArg> getEventArguments(ASTGUIComponent guiComponent) {
    return GUIGeneratorUtil.getEventArguments(guiComponent);
  }

  public String getComponentNameFromCamelCaseName(String camelCaseName) {
    return GUIGeneratorUtil.getComponentNameFromCamelCaseName(camelCaseName);
  }

  public boolean isComponentArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return GUIGeneratorUtil.isComponentArgument(guiComponent, arg);
  }

  public List<ASTGUIArgValue> getComponentArguments(ASTGUIComponent guiComponent) {
    return GUIGeneratorUtil.getComponentArguments(guiComponent);
  }

  public ASTGUIComponent getNestedComponent(ASTGUIArgValue arg) {
    return GUIGeneratorUtil.getNestedComponent(arg);
  }

  public boolean isComponentListArgument(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return GUIGeneratorUtil.isComponentListArgument(guiComponent, arg);
  }

  public List<ASTGUIArgValue> getComponentListArguments(ASTGUIComponent guiComponent, ASTGUIArgValue arg) {
    return GUIGeneratorUtil.getComponentListArguments(guiComponent, arg);
  }

  public List<ASTGUIParam> getGUIViewElementParameters(ASTGUICompilationUnit gui) {
    return GUIGeneratorUtil.getGUIViewElementParameters(gui);
  }

}
