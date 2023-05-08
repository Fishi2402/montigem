/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.EventDecorator;
import umlp.generator.gui.genjava.decorator.GUI2JavaComponentsDecorator;

import java.util.ArrayList;
import java.util.List;

public class GUI2TypeScriptComponentsDecorator extends GUI2JavaComponentsDecorator {

  public GUI2TypeScriptComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsVisitor viewComponent) {
    super(glex, viewComponent);
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {
    List<TemplateHookPoint> init = new ArrayList<>();
    for (ASTGUIComponent c : viewComponent.getComponents()) {
      GUI2TypeScriptComponentDecorator decorator = new GUI2TypeScriptComponentDecorator(
          glex, guiAst, viewComponent);
      decorator.transform(decoratedClass, c);
      init.addAll(decorator.getInitHPs());
      if (util.isEventComponent(c)) {
        new EventDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c);
      }
    }
    glex.replaceTemplate("umlp.gui2java.InitBody", init);

    // TODO AGe: rework iterate, not critical
    /*
    viewComponent.getIterates().stream()
        .forEach(c -> new IterateDecorator(glex, guiAst, viewComponent).transform(decoratedClass, c));
    /**/
  }

}
