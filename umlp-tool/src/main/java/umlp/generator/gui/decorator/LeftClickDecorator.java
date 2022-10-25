package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUINamedArgs;
import de.monticore.guidsl._symboltable.GUIFunctionSymbol;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.Optional;
import java.util.function.Predicate;

import static de.monticore.guidsl._symboltable.BuiltInTypes.EVENT;

public class LeftClickDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_TEXT = c -> c.getName().equals("text");
  public static final Predicate<ASTGUINamedArg> PREDICATE_LEFT_CLICK = c -> c.getName().equals("leftClick");

  public static final String NAVIGATE = "navigate";

  public LeftClickDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
/*    SymTypeExpression result = SymTypeExpressionFactory.createObscureType();
    ASTGUINamedArgs guiNamedArgs = guiComponent.getGUINamedArgs();
    Optional<GUIFunctionSymbol> function = guiComponent.getEnclosingScope().resolveGUIFunction(guiComponent.getQualifiedName());
    if (function.isPresent()) {
        if (function.get().getType().isVoidType()) {
          Optional<TypeSymbol> eventType = guiComponent.getEnclosingScope().resolveType(EVENT.getName());
          if (eventType.isPresent()) {
            result = SymTypeExpressionFactory.createTypeObject(eventType.get());
          }
        }
        else {
          result = function.get().getType();
        }
    }*/
  /*  TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());
    Optional<GUIFunctionSymbol> symbol = guiComponent.getEnclosingScope().resolveGUIFunction("leftClick");
    Optional<FunctionSymbol> symbol2 = guiComponent.getEnclosingScope().resolveFunction("leftClick");
   // SymTypeExpression ss = tc.typeOf(click.get().getExpression());*/

    filterArgumentsWithPredicate(new GUIComponentAccessVisitor(guiComponent).getNamedArguments(), PREDICATE_LEFTCLICK).forEach(click -> {
      if (click.getExpression() instanceof ASTCallExpression) {
        ASTCallExpression call = (ASTCallExpression) click.getExpression();

        String buttonText = getButtonText(call);

        if (isNavigateComponent(call)) {
          ASTGUIComponent arg = (ASTGUIComponent) call.getArguments().getExpressionList().get(0);
          getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.button.PressNavigateButton",
             // LEFT_CLICK + buttonText.replaceAll("\\s+", ""),
              LEFT_CLICK + guiComponent.getName() + componentName,
              arg.getName());
        }

        else {
          getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.button.PressButton",
              //LEFT_CLICK + buttonText.replaceAll("\\s+", ""),
              LEFT_CLICK + guiComponent.getName() + componentName,
              new GUIDSLFullPrettyPrinter().prettyprint(click.getExpression()));
        }
      }
    });

  }

  protected String getButtonText(ASTCallExpression call) {
    return StringTransformations.capitalize(guiDSLPrinter.prettyprint(call.getExpression()).replaceAll("\\.", "")
        .replaceAll("\"", ""));
  }

}
