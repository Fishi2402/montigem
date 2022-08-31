package umlp.generator.gui.decorator;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Log;

public class GUIDecorator {

  private final GlobalExtensionManagement glex;

  public GUIDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTGUICompilationUnit decoratedAST) {
    Log.info("After decorator: TypeAdapterDecorator", GUIDecorator.class.getName());
  }

}
