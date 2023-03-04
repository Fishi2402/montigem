/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIGeneratorUtil;

public class EventDecorator extends GUIComponentDecorator {

  public EventDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    GUIGeneratorUtil.getEventArguments(guiComponent).forEach(click -> {
      if (click.getGUIArgValue().isPresentExpression()
          && click.getGUIArgValue().getExpression() instanceof ASTNameExpression) {

        String methodName = ((ASTNameExpression) click.getGUIArgValue().getExpression()).getName();

        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event",
            StringTransformations.uncapitalize(guiComponent.getName()) + StringTransformations.capitalize(componentName) + StringTransformations.capitalize(click.getName()),
            new GUIDSLFullPrettyPrinter(new IndentPrinter()).prettyprint(click.getGUIArgValue().getExpression()) + "()");

        getCD4C().addMethod(decoratedClass, "umlp.gui2java.Event", methodName, "");
      }
    });

  }

  @Override
  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String variableType, String attrName) {
  }

}
