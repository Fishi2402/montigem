/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIDSLNode;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIComponentAccessVisitor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static umlp.generator.gui.GUIGeneratorUtil.isEventArgument;
import static umlp.generator.gui.GUIGeneratorUtil.isStringLiteral;

public class GUIComponentDecorator extends GUIAbstractDecorator {

  public final static String VALUE = "Value";
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

  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    generateGetMethodsForComponentArguments(decoratedClass, guiComponent, componentName);
  }

  protected void generateGetMethodsForComponentArguments(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);

    viewComponents.getNamedArgsFieldAccess().entrySet().forEach(e -> {
      generateGetMethodForComponentArgument(decoratedClass, guiComponent, componentName, e.getKey(), e.getValue());
    });
  }

  protected void generateGetMethodForComponentArgument(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      ASTGUINamedArg namedArg, Set<ASTFieldAccessExpression> zuweisungen) {

    String argValue = replaceZuweisungen(namedArg, zuweisungen);

    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());

    if (guiComponentSymbol.isPresent() && !isEventArgument(guiComponentSymbol.get(), namedArg)) {
      Optional<GUIParamSymbol> argSymbol = guiComponentSymbol.get().getSpannedScope().resolveGUIParam(namedArg.getName());
      if (argSymbol.isPresent()) {
        String returnType = argSymbol.get().getType().getTypeInfo().getName();

        boolean convertToString = mustBeConvertedToString(returnType, namedArg);
        if (convertToString) {
          argValue = "String.valueOf(" + argValue + ")";
        }

        String methodName = "get" + guiComponent.getName() + StringTransformations.capitalize(componentName)
            + StringTransformations.capitalize(namedArg.getName());
        addGetComponentMethod(decoratedClass, methodName, returnType, argValue);
      }
    }
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "ValueError";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponent",
        "get" + guiComponent.getName() + StringTransformations.capitalize(componentName) + VALUE, returnType, variableName, attrName);
  }

  protected void addGetComponentMethod(ASTCDClass decoratedClass, String methodName, String returnType, String replaced) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.GetComponentReplaced", methodName, returnType, replaced);
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String attrType, String attrName, String setExpr) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", "set" + guiComponent.getName(),
        attrType, attrName, setExpr);
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
        new GUIDSLFullPrettyPrinter(new IndentPrinter()).prettyprint(astNode);
  }

  protected Optional<ASTGUINamedArg> getComponentArgOptional(ASTGUIComponent guiComponent, Predicate<ASTGUINamedArg> predicate) {
    List<ASTGUINamedArg> args = filterArgWithPredicate(guiComponent.getGUINamedArgs().getGUINamedArgList(), predicate);
    return args.size() == 1 ? Optional.of(args.get(0)) : Optional.empty();
  }

  public Optional<VariableSymbol> resolveAttribute(Optional<GUIParamSymbol> guiVariableSymbol, String name) {
    if (guiVariableSymbol.isEmpty()) {
      return Optional.empty();
    }
    TypeSymbol typeInfo = guiVariableSymbol.get().getType().getTypeInfo();
    Optional<VariableSymbol> res = typeInfo.getSpannedScope().resolveVariable(name);
    if (res.isEmpty() && typeInfo.isPresentSuperClass()) {
      res = typeInfo.getSuperClass().getTypeInfo().getSpannedScope().resolveVariable(name);
    }
    if (res.isEmpty()) { // TODO: remove, debugonly
      res = typeInfo.getEnclosingScope().resolveVariable(name);
    }
    return res;
  }

  public Optional<String> handleZuweisung(ASTFieldAccessExpression zuweisung, ASTCDClass decoratedClass, ASTGUIComponent guiComponent,
      String componentName, boolean mustBeString) {
    if (zuweisung.getExpression() instanceof ASTNameExpression) {
      String variableName = ((ASTNameExpression) zuweisung.getExpression()).getName();
      Optional<GUIParamSymbol> symbol = ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(variableName);

      Optional<VariableSymbol> attr = resolveAttribute(symbol, zuweisung.getName());

      if (attr.isPresent()) {
        String returnType = attr.get().getType().getTypeInfo().getName();
        String attrName = attr.get().getName();
        String variableType = symbol.get().getType().getTypeInfo().getName();
        boolean isCollection = attr.get().getType().isGenericType() || attr.get().getType().isArrayType();

        generateMethodsForInputValueComponent(decoratedClass, guiComponent, componentName, returnType, variableName,
            variableType, attrName);

        return Optional.of(getFieldAccess(variableName, attrName, returnType, (mustBeString && !isCollection)));
      }
    }
    return Optional.empty();
  }

  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String variableType, String attrName) {
  }

  public String getFieldAccess(String variableName, String attrName, String variableType, boolean mustBeString) {
    String fieldAccess = variableName + ".get" + StringTransformations.capitalize(attrName) + "()";
    return (!mustBeString || variableType.endsWith("String")) ? fieldAccess : "String.valueOf(" + fieldAccess + ")";
  }

  public String getFieldAccessReplacement(String attrName, String variableType, boolean mustBeString) {
    String fieldAccess = "get" + StringTransformations.capitalize(attrName) + "()";
    return (!mustBeString || variableType.endsWith("String")) ? fieldAccess : "String.valueOf(" + fieldAccess + ")";
  }

  public String getFieldAccessReplacement(String variableName, String attrName) {
    return variableName + ".get" + StringTransformations.capitalize(attrName) + "()";
  }

  protected String replaceZuweisungen(ASTGUINamedArg namedArg, Set<ASTFieldAccessExpression> zuweisungen) {
    Map<ASTFieldAccessExpression, String> replacements = getAllReplacementsForZuwesungen(zuweisungen);

    String argValue = guiDSLPrinter.prettyprint(namedArg.getGUIArgValue());
    for (Map.Entry<ASTFieldAccessExpression, String> entry : replacements.entrySet()) {
      String fieldAccess = guiDSLPrinter.prettyprint(entry.getKey());
      argValue = argValue.replace(fieldAccess, entry.getValue());
    }
    return argValue;
  }

  protected Map<ASTFieldAccessExpression, String> getAllReplacementsForZuwesungen(Set<ASTFieldAccessExpression> value) {
    Map<ASTFieldAccessExpression, String> replacement = new HashMap<>();
    value.forEach(f -> handleZuweisung(f).ifPresent(r -> replacement.put(f, r)));
    return replacement;
  }

  protected Optional<String> handleZuweisung(ASTFieldAccessExpression zuweisung) {
    if (zuweisung.getExpression() instanceof ASTNameExpression) {
      String variableName = ((ASTNameExpression) zuweisung.getExpression()).getName();
      Optional<GUIParamSymbol> symbol = ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(variableName);

      Optional<VariableSymbol> attr = resolveAttribute(symbol, zuweisung.getName());
      if (attr.isPresent()) {
        return Optional.of(getFieldAccessReplacement(variableName, attr.get().getName()));
      }
    }
    return Optional.empty();
  }

  protected Optional<String> getTypeOf(ASTFieldAccessExpression zuweisung) {
    if (zuweisung.getExpression() instanceof ASTNameExpression) {
      String variableName = ((ASTNameExpression) zuweisung.getExpression()).getName();
      Optional<GUIParamSymbol> symbol = ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(variableName);

      Optional<VariableSymbol> attr = resolveAttribute(symbol, zuweisung.getName());
      if (attr.isPresent()) {
        String returnType = attr.get().getType().getTypeInfo().getName();
        return Optional.of(returnType);
      }
    }
    return Optional.empty();
  }

  protected boolean mustBeConvertedToString(String returnType, ASTGUINamedArg namedArg) {
    boolean convertToString = returnType.endsWith("String") && !isStringLiteral(namedArg.getGUIArgValue());
    if (convertToString && namedArg.getGUIArgValue().getExpression() instanceof ASTFieldAccessExpression) {
      Optional<String> zuweisungType = getTypeOf((ASTFieldAccessExpression) namedArg.getGUIArgValue().getExpression());
      convertToString = !zuweisungType.isPresent() || !zuweisungType.get().endsWith("String");
    }
    return convertToString;
  }


}
