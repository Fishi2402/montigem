/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
      // TODO AGe: remove restrictions
      if (event.getGUIArgValue().isPresentExpression()) {
        if (event.getGUIArgValue().getExpression() instanceof ASTNameExpression) {
          String methodName = ((ASTNameExpression) event.getGUIArgValue().getExpression()).getName();

          getCD4C().addMethod(decoratedClass, getEventTemplate(),
              util.gui2cdEventCallName(guiComponent, event.getName()), methodName);

          getCD4C().addMethod(decoratedClass, getEventTemplate(), methodName, "");
        }
        if (event.getGUIArgValue().getExpression() instanceof ASTCallExpression
            && ((ASTCallExpression) event.getGUIArgValue().getExpression()).getExpression() instanceof ASTNameExpression) {
          ASTCallExpression call = (ASTCallExpression) event.getGUIArgValue().getExpression();
          ASTNameExpression callName = (ASTNameExpression) call.getExpression();

          List<String> args = new ArrayList<>();
          for (ASTExpression e : call.getArguments().getExpressionList()) {
            args.add(util.print(e));
          }
          String methodName = callName.getName();

          getCD4C().addMethod(decoratedClass, getEventCallbackTemplate(),
              util.gui2cdEventCallName(guiComponent, event.getName()), methodName, args);

          getCD4C().addMethod(decoratedClass, getEventCallbackTemplate(), methodName, "", args);
        }
      }
    }
  }

  protected String getEventTemplate() {
    return "umlp.gui2java.Event";
  }

  protected String getEventCallbackTemplate() {
    return "umlp.gui2java.EventCallback";
  }

}
