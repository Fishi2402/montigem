/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

public class EventDecorator extends GUI2JavaComponentDecorator {

  public EventDecorator(
      final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst,
      GUIComponentsVisitor viewComponent
  ) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    for (ASTGUINamedArg event : GUIGeneratorUtil.getEventArguments(guiComponent)) {
      // TODO AGe: remove restriction on NameExpression
      if (event.getGUIArgValue().isPresentExpression()
          && event.getGUIArgValue().getExpression() instanceof ASTNameExpression) {

        String methodName = ((ASTNameExpression) event.getGUIArgValue().getExpression()).getName();

        String call = methodName + "()";
        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event",
            GUIGeneratorUtil.gui2cdEventCallName(guiComponent, event.getName()), call);

        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event", methodName, "");
      }
    }
  }

}
