package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.genjava.decorator.GUI2JavaGuardDecorator;

public class GUI2TypeScriptGuardDecorator extends GUI2JavaGuardDecorator {

  public GUI2TypeScriptGuardDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  protected String getGetterTemplate() {
    return "umlp.gui2ts.TSGetComponentReplaced";
  }


}
