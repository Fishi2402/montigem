/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;

public class GUI2JavaComponentsDecorator extends GUIAbstractDecorator {

  protected final GUIComponentsVisitor viewComponent;

  public GUI2JavaComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.viewComponent = viewComponent;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {
    for (ASTGUIComponent c : viewComponent.getComponents()) {
      new GUI2JavaComponentDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c);
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
