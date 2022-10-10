package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.TypeCalculator;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CallDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_INPUT_ARG = c -> c.getName().equals(INPUT_ARG_NAME);
  private final IGUIDSLScope artifactScope;

  public CallDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
    this.artifactScope = guiAst.getEnclosingScope();
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTCallExpression> allCalls = viewComponents.getCallExpressions();

    TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());

    allCalls.forEach(call -> {
          if (!isNavigateComponent(call)) {
            StringBuilder sb = new StringBuilder();
            for (ASTExpression argument : call.getArguments().getExpressionList()) {
              if (!(argument instanceof ASTNameExpression)) {
                continue;
              }
              String paramName = ((ASTNameExpression) argument).getName();
              Optional<GUIVariableSymbol> paramSymbol = ((GUIDSLScope) argument.getEnclosingScope()).resolveGUIVariable(paramName);
              if (paramSymbol.isEmpty()) {
                continue;
              }
              TypeSymbol typeInfo = paramSymbol.get().getType().getTypeInfo();
              if (sb.length() != 0) {
                sb.append(", ");
              }
              sb.append(typeInfo.getName()).append(" ").append(paramName);
            }
            getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.Call",
                getMethodName(call), sb.toString()
                /* new GUIDSLFullPrettyPrinter().prettyprint(call))*/);
          }
          else {
            ASTGUIComponent arg = (ASTGUIComponent) call.getArguments().getExpressionList().get(0);
            // GUIFunctionSymbol fS = arg.getNameSymbol();
            List<String> args = arg.getGUINamedArgs().getGUINamedArgList().stream().filter(a -> a.getExpression() instanceof ASTNameExpression)
                .map(a -> ((ASTNameExpression) a.getExpression()).getName()).collect(Collectors.toList());
            String result = String.join(",", args);
            getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.Navigate",
                "navigate", arg.getName(), result);
          }
        }
    );

  }

  protected boolean isNavigateComponent(ASTCallExpression call) {
    return getMethodName(call).equals("navigate") &&
        call.getArguments().getExpressionList().size() == 1 &&
        call.getArguments().getExpressionList().get(0) instanceof ASTGUIComponent;
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

}
