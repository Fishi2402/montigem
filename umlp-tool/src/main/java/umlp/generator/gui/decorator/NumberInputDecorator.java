package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

public class NumberInputDecorator extends GUIComponentDecorator {

  public NumberInputDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    if (guiComponent.getGUINamedArgs().getGUINamedArgList().size() != 1) {
      Log.error("NumberInputDecorator GUIComponent args aren't correct"); //TODO msg
      return;
    }
    ASTGUINamedArg arg = guiComponent.getGUINamedArgs().getGUINamedArgList().get(0);
    if (!arg.getName().equals(INPUT_ARG_NAME)) {
      Log.error("NumberInputDecorator GUIComponent args aren't correct");
      return;
    }

    String returnExpr = "";
    String attrName = "";

    if (arg.getExpression() instanceof ASTFieldAccessExpression) {  //  :TextInput(value = p.nickname)
      ASTFieldAccessExpression expr = (ASTFieldAccessExpression) arg.getExpression(); //p.nickname
      attrName = expr.getName(); //nickname
      // resolve and check type

      if (!(expr.getExpression() instanceof ASTNameExpression)) {
        Log.error("NumberInputDecorator GUIComponent args aren't correct");
        return;
      }

      String domainAttrName = ((ASTNameExpression) expr.getExpression()).getName(); //p (Person)
      // resolve and check type

      returnExpr = domainAttrName + ".get" + StringTransformations.capitalize(attrName) + "()";

    }
    else if (arg.getExpression() instanceof ASTNameExpression) { //MB Visitor? //  :TextInput(value = cityName)
      attrName = ((ASTNameExpression) arg.getExpression()).getName(); // cityName
      // resolve and check type

      returnExpr = attrName;

    }
    else {
      Log.error("NumberInputDecorator GUIComponent args aren't correct"); //TODO msg
      return;
    }

    String returnType = "int"; // TODO GV

    addComponentErrorAttribute(decoratedClass, guiComponent);
    addNotifyMethod(decoratedClass, guiComponent, attrName);
    addGetComponentMethod(decoratedClass, guiComponent, returnExpr, returnType);

  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.input.Notify",
        "notify" + StringTransformations.capitalize(attrName) + "Invalid", StringTransformations.uncapitalize(guiComponent.getName()));
  }

}
