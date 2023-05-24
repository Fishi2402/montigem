/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.ArrayList;
import java.util.List;

public class GUI2JavaComponentsDecorator extends GUIAbstractDecorator {

  protected final GUIComponentsVisitor viewComponent;

  public GUI2JavaComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.viewComponent = viewComponent;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {
    List<TemplateHookPoint> init = new ArrayList<>();
    for (ASTGUIComponent c : viewComponent.getComponents()) {
      GUI2JavaComponentDecorator decorator = new GUI2JavaComponentDecorator(
          glex, guiAst, viewComponent);
      decorator.transform(decoratedClass, c);
      init.addAll(decorator.getInitHPs());
      if (util.isEventComponent(c)) {
        new EventDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c);
      }
    }
    glex.replaceTemplate("umlp.gui2java.InitBody", init);
    for (ASTGUIGuard g : viewComponent.getGuards()) {
      GUI2JavaGuardDecorator decorator = new GUI2JavaGuardDecorator(glex, guiAst);
      decorator.transform(decoratedClass, g);
    }

    // TODO AGe: rework iterate, not critical
    /*
    viewComponent.getIterates().stream()
        .forEach(c -> new IterateDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c));
    /**/
  }

}
