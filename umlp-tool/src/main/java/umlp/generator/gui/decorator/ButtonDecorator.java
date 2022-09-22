package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ButtonDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_TEXT = c -> c.getName().equals("text");
  public static final Predicate<ASTGUINamedArg> PREDICATE_LEFT_CLICK = c -> c.getName().equals("leftClick");

  public static final String NAVIGATE = "navigate";

  public ButtonDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    Optional<ASTGUINamedArg> text = getTextArgument(guiComponent);
    Optional<ASTGUINamedArg> click = getLeftClickArgument(guiComponent);
    if (text.isEmpty() || click.isEmpty()) {
      return;
    }

    String buttonText = getButtonText(text.get());

    if (click.get().getExpression() instanceof ASTCallExpression) {  // :Button(text = "Decrease", leftClick = decreaseAge(p))
      String clickMethodName = getMethodName((ASTCallExpression) click.get().getExpression());

      if (NAVIGATE.equals(clickMethodName)) {
        handleNavigate((ASTCallExpression) click.get().getExpression(), buttonText);
      }

      else {
        ASTArguments arguments = ((ASTCallExpression) click.get().getExpression()).getArguments();
        getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.button.PressButton",
            "pressButton" + buttonText.replaceAll("\\s+", ""),
            new GUIDSLFullPrettyPrinter().prettyprint(click.get().getExpression()));
      }
    }
  }

  protected String getMethodName(ASTCallExpression callExpression) {
    if (callExpression.getExpression() instanceof ASTNameExpression) {
      return ((ASTNameExpression) callExpression.getExpression()).getName();
    }
    return "";
  }

  protected String getButtonText(ASTGUINamedArg text) {
    if (text.getExpression() instanceof ASTLiteralExpression
        && ((ASTLiteralExpression) text.getExpression()).getLiteral() instanceof ASTStringLiteral) {  // :Button(text = "Decrease", leftClick = decreaseAge(p))
      return ((ASTStringLiteral) ((ASTLiteralExpression) text.getExpression()).getLiteral()).getSource();
    }
    return "";
  }

  protected void handleNavigate(ASTCallExpression expression, String buttonText) {
  }

  protected Optional<ASTGUINamedArg> getTextArgument(ASTGUIComponent guiComponent) {
    List<ASTGUINamedArg> textArgs = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), PREDICATE_TEXT);
    if (textArgs.size() != 1) {
      Log.error("Button GUIComponent args aren't correct");
      return Optional.empty();
    }
    return Optional.of(textArgs.get(0));
  }

  protected Optional<ASTGUINamedArg> getLeftClickArgument(ASTGUIComponent guiComponent) {
    List<ASTGUINamedArg> clickArgs = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), PREDICATE_LEFT_CLICK);
    if (clickArgs.size() != 1) {
      Log.error("Button GUIComponent args aren't correct");
      return Optional.empty();
    }
    return Optional.of(clickArgs.get(0));
  }


}
