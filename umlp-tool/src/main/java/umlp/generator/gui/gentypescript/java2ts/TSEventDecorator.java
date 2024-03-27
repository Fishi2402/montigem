/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.EventDecorator;
import umlp.generator.gui.genjava.decorator.GUI2JavaComponentDecorator;

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
  protected String getEventTypeTemplate(EventType inbuild) {
    switch (inbuild) {
      case CREATE:
        return "umlp.gui2ts.TSCreateEvent";
      case DELETE:
        return "umlp.gui2ts.TSDeleteEvent";
    }
    return "";  }

}
