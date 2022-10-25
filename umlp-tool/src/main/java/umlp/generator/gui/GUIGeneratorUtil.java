/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIParam;

import java.util.List;

public class GUIGeneratorUtil {

  protected GUIGeneratorUtil() {
  }

  public static final boolean isInputGui(ASTGUICompilationUnit guiAst, List<ASTGUIComponent> inputComponents) {
    return !inputComponents.isEmpty() &&
        guiAst.getGUIComponentDeclaration().getGUIParams().getGUIParamList().stream()
            .filter(p -> !p.getSymbol().getType().isGenericType()).findAny().isPresent();
  }

  public static final boolean isDomainType(ASTGUIParam astguiParam) {
    return !astguiParam.getSymbol().getType().isGenericType();
  }

}
