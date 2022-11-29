package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIDSLNode;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class GUIComponentDecorator extends GUIAbstractDecorator {

  protected static final String INPUT_ARG_NAME = "value";
  protected final String guiName;
  private final ASTGUICompilationUnit guiAst;

  public GUIComponentDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst) {
    super(glex);
    this.guiName = guiAst.getGUIComponentDeclaration().getName();
    this.guiAst = guiAst;
  }

  protected static List<ASTGUINamedArg> filterArgWithPredicate(List<ASTGUINamedArg> components,
                                                               Predicate<ASTGUINamedArg> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

  public abstract void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName);

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "Error";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String returnExpr, String returnType) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent", "get" + guiComponent.getName(),
        returnType, returnExpr);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String attrType, String attrName, String setExpr) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName(),
        attrType, attrName, setExpr);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  protected boolean checkArgumentsSize(ASTGUIComponent guiComponent, int size) {
    if (guiComponent.getGUINamedArgs().getGUINamedArgList().size() != size) {
      Log.error("Anzahl der Argumente der GUI-Komponente muss gleich " + size + " sein in " + guiName);
      return false;
    }
    return true;
  }

  protected Optional<ASTGUINamedArg> getComponentArgSingle(ASTGUIComponent guiComponent, Predicate<ASTGUINamedArg> predicate) {
    List<ASTGUINamedArg> args = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), predicate);
    if (args.size() != 1) {
      //Log.error(getErrorMessage(guiComponent.getName(), guiComponent.getGUINamedArgs()));
      return Optional.empty();
    }
    return Optional.of(args.get(0));
  }

  protected String getErrorMessage(String guiComponentName, ASTGUIDSLNode astNode) {
    return guiComponentName + "-GUIComponent args aren't correct in " + guiName + " : \n" +
        new GUIDSLFullPrettyPrinter().prettyprint(astNode);
  }

  protected Optional<ASTGUINamedArg> getComponentArgOptional(ASTGUIComponent guiComponent, Predicate<ASTGUINamedArg> predicate) {
    List<ASTGUINamedArg> args = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), predicate);
    return args.size() == 1 ? Optional.of(args.get(0)) : Optional.empty();
  }

  public Optional<VariableSymbol> resolveAttribute(Optional<GUIVariableSymbol> guiVariableSymbol, String name) {
    if (guiVariableSymbol.isEmpty()) {
      return Optional.empty();
    }
    TypeSymbol typeInfo = guiVariableSymbol.get().getType().getTypeInfo();
    Optional<VariableSymbol> res = typeInfo.getSpannedScope().resolveVariable(name);
    if (res.isEmpty() && typeInfo.isPresentSuperClass()) {
      res = typeInfo.getSuperClass().getTypeInfo().getSpannedScope().resolveVariable(name);
    }
    return res;
  }

  public boolean isNavigateComponent(ASTCallExpression call, TypeCalculator tc) {
    SymTypeExpression res = tc.typeOf(call.getExpression());
    return getMethodName(call).equals("navigate") &&
        call.getArguments().getExpressionList().size() == 1 &&
        call.getArguments().getExpressionList().get(0) instanceof ASTGUIComponent;
  }

  public boolean isNavigateComponent(ASTCallExpression call) {
    return getMethodName(call).equals("navigate") &&
        call.getArguments().getExpressionList().size() == 1 &&
        call.getArguments().getExpressionList().get(0) instanceof ASTGUIComponent;
  }

  public String getMethodName(ASTCallExpression callExpression) {
    if (callExpression.getExpression() instanceof ASTNameExpression) {
      return ((ASTNameExpression) callExpression.getExpression()).getName();
    }
    return "";
  }

}
