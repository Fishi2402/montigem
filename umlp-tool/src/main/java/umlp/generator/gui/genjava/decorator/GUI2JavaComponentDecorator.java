/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIDSLNode;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._ast.ASTGUIShortIterateControl;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentAccessVisitor;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static umlp.generator.gui.GUIGeneratorUtil.isEventArgument;
import static umlp.generator.gui.GUIGeneratorUtil.isStringLiteral;

public class GUI2JavaComponentDecorator extends GUIAbstractDecorator {

  public final static String VALUE = "Value";
  protected final String guiName;
  private final ASTGUICompilationUnit guiAst;
  protected final GUIComponentsVisitor viewComponent;

  public GUI2JavaComponentDecorator(final GlobalExtensionManagement glex, ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.guiName = guiAst.getGUIComponentDeclaration().getName();
    this.guiAst = guiAst;
    this.viewComponent = viewComponent;
  }

  protected static List<ASTGUINamedArg> filterArgWithPredicate(List<ASTGUINamedArg> components,
      Predicate<ASTGUINamedArg> predicate) {
    return components.stream().filter(predicate).collect(Collectors.toList());
  }

  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    String componentName = viewComponent.getComponentName(guiComponent);
    Optional<ASTGUIIterate> iterate = viewComponent.getIterateForComponent(guiComponent);
    generateGetMethodsForComponentArguments(decoratedClass, guiComponent, componentName, iterate);
  }

  protected void generateGetMethodsForComponentArguments(
      ASTCDClass decoratedClass, ASTGUIComponent guiComponent,
      String componentName, Optional<ASTGUIIterate> iterate
  ) {
    GUIComponentAccessVisitor viewComponents = new GUIComponentAccessVisitor(guiComponent);

    viewComponents.getNamedArgsFieldAccess().entrySet().forEach(e -> {
      generateGetMethodForComponentArgument(decoratedClass, guiComponent, componentName, iterate, e.getKey(), e.getValue());
    });
  }

  protected void generateGetMethodForComponentArgument(
      ASTCDClass decoratedClass, ASTGUIComponent guiComponent,
      String componentName, Optional<ASTGUIIterate> iterate,
      ASTGUINamedArg namedArg, Set<ASTFieldAccessExpression> zuweisungen
  ) {
    String argValue = replaceZuweisungen(namedArg, zuweisungen);

    Optional<GUIComponentDeclarationSymbol> guiComponentSymbol = guiComponent.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getQualifiedName());

    if (guiComponentSymbol.isPresent() && !isEventArgument(guiComponentSymbol.get(), namedArg)) {
      Optional<GUIParamSymbol> paramSym = guiComponentSymbol.get().getSpannedScope().resolveGUIParam(namedArg.getName());
      /*
      if (!paramSym.isPresent() && iterate.isPresent()) {
        paramSym = iterate.get().getSpannedScope().resolveGUIParam(namedArg.getName());
      }
      /**/
      if (paramSym.isPresent()) {
        if (GUIGeneratorUtil.isViewElement(paramSym.get().getType())
            || GUIGeneratorUtil.isViewElementsCollection(paramSym.get().getType())) {
          return;
        }
        String paramType = paramSym.get().getType().getTypeInfo().getName();
        boolean convertToString = mustBeConvertedToString(paramType, namedArg);
        if (convertToString) {
          argValue = "String.valueOf(" + argValue + ")";
        }

        String methodName = "get"
            + guiComponent.getName()
            + StringTransformations.capitalize(componentName)
            + StringTransformations.capitalize(namedArg.getName());
        addGetComponentMethod(decoratedClass, methodName, paramType, argValue);
      }
    }
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
  }

  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, Optional<ASTGUIIterate> iterate) {
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


  public Optional<VariableSymbol> resolveAttribute(SymTypeExpression type, String name) {
    TypeSymbol typeInfo = type.getTypeInfo();
    Optional<VariableSymbol> res = typeInfo.getSpannedScope().resolveVariable(name);
    if (res.isEmpty() && typeInfo.isPresentSuperClass()) {
      res = typeInfo.getSuperClass().getTypeInfo().getSpannedScope().resolveVariable(name);
    }
    if (res.isEmpty()) { // TODO: remove, debugonly
      res = typeInfo.getEnclosingScope().resolveVariable(name);
    }
    return res;
  }

  public Optional<VariableSymbol> resolveAttribute(Optional<GUIParamSymbol> guiParamSym, String name) {
    if (guiParamSym.isEmpty()) {
      return Optional.empty();
    }
    TypeSymbol typeInfo = guiParamSym.get().getType().getTypeInfo();
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
      String componentName, boolean mustBeString, Optional<ASTGUIIterate> iterate) {

    if (zuweisung.getExpression() instanceof ASTNameExpression) {
      String variableName = ((ASTNameExpression) zuweisung.getExpression()).getName();
      Optional<GUIParamSymbol> symbol = ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(variableName);

      Optional<VariableSymbol> attr = Optional.empty();
      String variableType = null;
      if (symbol.isPresent()) {
        attr = resolveAttribute(symbol, zuweisung.getName());
      }
      else if (iterate.isPresent()) {
        symbol = getVariableSymbol(iterate.get(), zuweisung, variableName);
        if (symbol.isEmpty()) {
          return Optional.empty();
        }
        Optional<SymTypeExpression> iterateType = resolveAttributeFromIterate(iterate.get(), zuweisung, symbol, variableName);
        if (iterateType.isEmpty()) {
          return Optional.empty();
        }
        attr = resolveAttribute(iterateType.get(), zuweisung.getName());
        // variableType = GUIGeneratorUtil.removeDiagramName(iterateType.get().getTypeInfo().getName());
        variableType = iterateType.get().getTypeInfo().getName();
      }

      if (attr.isPresent()) {
        String returnType = attr.get().getType().getTypeInfo().getName();
        String attrName = attr.get().getName();
        if (variableType == null) {
          variableType = symbol.get().getType().getTypeInfo().getName();
        }
        boolean isCollection = attr.get().getType().isGenericType() || attr.get().getType().isArrayType();

        generateMethodsForInputValueComponent(decoratedClass, guiComponent, componentName, returnType, variableName,
            variableType, attrName, iterate);

        return Optional.of(getFieldAccess(variableName, attrName, returnType, (mustBeString && !isCollection)));
      }
    }

    return Optional.empty();
  }

  // TODO AGe: limited conversion
  protected Optional<SymTypeExpression> resolveAttributeFromIterate(
      ASTGUIIterate iterate, ASTFieldAccessExpression zuweisung,
      Optional<GUIParamSymbol> paramSym, String variableName
  ) {
    Optional<VariableSymbol> iterateVariable = iterate.getSpannedScope().resolveVariable(variableName);
    if (iterateVariable.isPresent() && iterate.getIterateControl() instanceof ASTGUIShortIterateControl) {
      ASTGUIShortIterateControl it = (ASTGUIShortIterateControl) iterate.getIterateControl();
      if (it.getIterable() instanceof ASTFieldAccessExpression) {
        String listName = ((ASTFieldAccessExpression) it.getIterable()).getName();
        Optional<VariableSymbol> iterateAttribute = resolveAttribute(paramSym, listName);
        return iterateAttribute.map(VariableSymbolTOP::getType);
        //return resolveAttribute(iterateAttribute.get().getType(), zuweisung.getName());
      }
      else if (it.getIterable() instanceof ASTNameExpression) {
        if (paramSym.isEmpty() || !GenService.isList(paramSym.get().getType())) {
          return Optional.empty();
        }
        SymTypeExpression listType = ((SymTypeOfGenerics) paramSym.get().getType()).getArgument(0);
        //  GenService.getInnerType(paramSym.get().getAstNode().getMCType());
        return Optional.of(listType);
        //return resolveAttribute(listType, zuweisung.getName());
      }
    }
    return Optional.empty();
  }

  // TODO AGe: limited conversion
  protected Optional<GUIParamSymbol> getVariableSymbol(ASTGUIIterate iterate, ASTFieldAccessExpression zuweisung, String variableName) {
    Optional<VariableSymbol> iterateVariable = iterate.getSpannedScope().resolveVariable(variableName);
    if (iterateVariable.isPresent() && iterate.getIterateControl() instanceof ASTGUIShortIterateControl) {
      ASTGUIShortIterateControl it = (ASTGUIShortIterateControl) iterate.getIterateControl();
      if (it.getIterable() instanceof ASTFieldAccessExpression) {
        String p = ((ASTNameExpression) ((ASTFieldAccessExpression) it.getIterable()).getExpression()).getName();
        return ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(p);
      }
      else if (it.getIterable() instanceof ASTNameExpression) {
        String listName = ((ASTNameExpression) it.getIterable()).getName();
        return ((GUIDSLScope) zuweisung.getEnclosingScope()).resolveGUIParam(listName);
      }
    }
    return Optional.empty();
  }

  protected void generateMethodsForInputValueComponent(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName,
      String returnType, String variableName, String variableType, String attrName, Optional<ASTGUIIterate> iterate) {
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

  // TODO AGe: limited conversion
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

  // TODO AGe: not nice
  protected boolean mustBeConvertedToString(String returnType, ASTGUINamedArg namedArg) {
    boolean convertToString = returnType.endsWith("String") && !isStringLiteral(namedArg.getGUIArgValue());
    if (convertToString && namedArg.getGUIArgValue().getExpression() instanceof ASTFieldAccessExpression) {
      Optional<String> zuweisungType = getTypeOf((ASTFieldAccessExpression) namedArg.getGUIArgValue().getExpression());
      convertToString = !zuweisungType.isPresent() || !zuweisungType.get().endsWith("String");
    }
    return convertToString;
  }


}
