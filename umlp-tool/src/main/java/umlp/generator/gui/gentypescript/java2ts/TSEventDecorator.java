/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.EventDecorator;

public class TSEventDecorator extends EventDecorator {

  public TSEventDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst,
      GUIComponentsVisitor viewComponent
  ) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  protected String getEventTemplate() {
    return "umlp.gui2ts.TSEvent";
  }

  @Override
  protected String getEventCallbackTemplate() {
    return "umlp.gui2ts.TSEventCallback";
  }

  @Override
  protected String getEventArgCallbackTemplate() {
    return "umlp.gui2ts.TSEventArgCallback";
  }

  @Override
  protected String getEventTypeTemplate(EventType inbuild) {
    switch (inbuild) {
      case CREATE:
        return "umlp.gui2ts.TSCreateEvent";
      case DELETE:
        return "umlp.gui2ts.TSDeleteEvent";
      case ASSOC_ADD:
        return "umlp.gui2ts.TSAssocAddEvent";
      case ASSOC_REMOVE:
        return "umlp.gui2ts.TSAssocRemoveEvent";
    }
    return "";  }

}
