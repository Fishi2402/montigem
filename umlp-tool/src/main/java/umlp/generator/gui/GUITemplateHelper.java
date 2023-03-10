/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;

import java.util.List;

public class GUITemplateHelper {

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

}
