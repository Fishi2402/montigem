/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import umlp.generator.gui.GUIComponentsVisitor;

public class EventDecorator extends GUI2JavaComponentDecorator {

  public EventDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst,
      GUIComponentsVisitor viewComponent
  ) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    for (ASTGUINamedArg event : util.getEventArguments(guiComponent)) {
      // TODO AGe: remove restriction on NameExpression
      if (event.getGUIArgValue().isPresentExpression()
          && event.getGUIArgValue().getExpression() instanceof ASTNameExpression) {

        String methodName = ((ASTNameExpression) event.getGUIArgValue().getExpression()).getName();

        String call = methodName + "()";
        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event",
            util.gui2cdEventCallName(guiComponent, event.getName()), call);

        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event", methodName, "");
      }
    }
  }

}
