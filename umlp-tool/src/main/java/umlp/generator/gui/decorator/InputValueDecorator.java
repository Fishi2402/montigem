package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class InputValueDecorator extends GUIComponentDecorator {

  public static final Predicate<ASTGUINamedArg> PREDICATE_INPUT_ARG = c -> c.getName().equals(INPUT_ARG_NAME);
  private final IGUIDSLScope artifactScope;

  public InputValueDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex, guiAst);
    this.artifactScope = guiAst.getEnclosingScope();
  }

  @Override
  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    // Get all ASTGUIComponents
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);
    List<ASTFieldAccessExpression> allZuweisungen = viewComponents.getFieldAccess();

    TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());

    allZuweisungen.forEach(f -> {
          if (f.getExpression() instanceof ASTNameExpression) {

            String variableName = ((ASTNameExpression) f.getExpression()).getName();
            Optional<GUIVariableSymbol> symbol = ((GUIDSLScope) f.getEnclosingScope()).resolveGUIVariable(variableName);

            Optional<VariableSymbol> attr = resolveAttribute(symbol, f.getName());

            if (attr.isPresent()) {
              String returnType = attr.get().getType().getTypeInfo().getName();
              String attrName = attr.get().getName();
              String returnExpr = variableName + ".get" + StringTransformations.capitalize(attrName) + "()";
              String setExpr = variableName+ ".set" + StringTransformations.capitalize(attrName) + "(" + attrName + ")";

              addComponentErrorAttribute(decoratedClass, guiComponent);
              addNotifyMethod(decoratedClass, guiComponent, attrName);
              addGetComponentMethod(decoratedClass, guiComponent, returnExpr, returnType);
              addSetComponentMethod(decoratedClass, guiComponent, returnType, attrName, setExpr);
            }
          }
        }
    );

  }

  protected void addNotifyMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.components.input.Notify",
        "notify" + StringTransformations.capitalize(attrName) + "Invalid", StringTransformations.uncapitalize(guiComponent.getName()));
  }

}
