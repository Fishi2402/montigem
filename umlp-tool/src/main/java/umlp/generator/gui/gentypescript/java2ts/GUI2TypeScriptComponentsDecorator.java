/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.EventDecorator;
import umlp.generator.gui.genjava.decorator.GUI2JavaComponentsDecorator;

public class GUI2TypeScriptComponentsDecorator extends GUI2JavaComponentsDecorator {

  public GUI2TypeScriptComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsVisitor viewComponent) {
    super(glex, viewComponent);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {
    for (ASTGUIComponent c : viewComponent.getComponents()) {
      new GUI2TypeScriptComponentDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c);
      if (util.isEventComponent(c)) {
        new EventDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c);
      }
    }

    // TODO AGe: rework iterate, not critical
    /*
    viewComponent.getIterates().stream()
        .forEach(c -> new IterateDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c));
    /**/
  }

}
