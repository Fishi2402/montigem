/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.genhtml;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;

public class GUI2GUITrafo {

  public static ASTGUICompilationUnit createDecoratedGUIAst(ASTGUICompilationUnit guiAst) {
    // create gui ast to decorate
    ASTGUICompilationUnit transformedGUI = guiAst.deepClone();

    return transformedGUI;
  }

}
