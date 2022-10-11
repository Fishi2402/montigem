package umlp.generator.gui.decorator.deprecated;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.decorator.GUIComponentDecorator;

import java.util.Optional;
import java.util.function.Predicate;

public class NumberInputDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_INPUT_ARG = c -> c.getName().equals(INPUT_ARG_NAME);

  public NumberInputDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    Optional<ASTGUINamedArg> arg = getComponentArgSingle(guiComponent, PREDICATE_INPUT_ARG);
    if (arg.isEmpty() || !checkArgumentsSize(guiComponent, 1)) {
      return;
    }

    ASTGUINamedArg inputArg = arg.get();
    String returnExpr = "";
    String attrName = "";

    if (inputArg.getExpression() instanceof ASTFieldAccessExpression) {  //  :TextInput(value = p.nickname)
      ASTFieldAccessExpression expr = (ASTFieldAccessExpression) inputArg.getExpression(); //p.nickname
      attrName = expr.getName(); //nickname
      // resolve and check type

      if (!(expr.getExpression() instanceof ASTNameExpression)) {
        Log.error(guiComponent.getName() + "-GUIComponent args aren't correct in " + guiName + " : \n" +
            new GUIDSLFullPrettyPrinter().prettyprint(expr.getExpression()));
        return;
      }

      String domainAttrName = ((ASTNameExpression) expr.getExpression()).getName(); //p (Person)
      // resolve and check type

      returnExpr = domainAttrName + ".get" + StringTransformations.capitalize(attrName) + "()";

    }
    else if (inputArg.getExpression() instanceof ASTNameExpression) {
      attrName = ((ASTNameExpression) inputArg.getExpression()).getName(); // cityName
      // resolve and check type

      returnExpr = attrName;

    }
    else {
      Log.error(guiComponent.getName() + "-GUIComponent args aren't correct in " + guiName + " : \n" +
          new GUIDSLFullPrettyPrinter().prettyprint(inputArg.getExpression()));
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
