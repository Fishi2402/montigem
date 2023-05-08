/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.decorator.GUI2JavaComponentDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2TypeScriptComponentDecorator extends GUI2JavaComponentDecorator {

  public GUI2TypeScriptComponentDecorator(final GlobalExtensionManagement glex,
      ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex, guiAst, viewComponent);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  // TODO AGe: error concept is not ready, leave that out for now
  /*
  protected void addComponentErrorAttribute(ASTCDClass decoratedClass, ASTGUIComponent guiComponent, String componentName, Optional<ASTGUIIterate> iterate) {
    String componentAttributeName = StringTransformations.uncapitalize(guiComponent.getName()) + "ValueError";
    getCD4C().addAttribute(decoratedClass, true, false, "protected String " + componentAttributeName + " = \"\";");
  }
  /**/

  @Override
  protected String unknownCollectionType() {
    return "any";
  }

  @Override
  protected void setDefaultListValue(ASTCDAttribute attr) {
    glex.replaceTemplate("cd2java.Value", attr,
        new StringHookPoint(" = []"));
  }

  @Override
  public List<String> getArgs(Map<String, String> args) {
    List<String> res = new ArrayList<>();
    for (Map.Entry<String, String> arg : args.entrySet()) {
      res.add(arg.getValue() + " " + arg.getKey());
    }
    return res;
  }

  @Override
  protected String getGetterTemplate() {
    return "umlp.gui2ts.GetComponentReplaced";
  }

  @Override
  protected String getSetterTemplate() {
    return "umlp.gui2ts.SetComponent";
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue,
      List<String> arguments
  ) {
    getCD4C().addMethod(
        decoratedClass, "umlp.gui2java.GetComponentReplaced", methodName, attrType,
        returnValue, String.join(", ", arguments)
    );
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue) {
    addGetComponentMethod(
        decoratedClass, methodName, attrType, returnValue, Collections.emptyList());
  }

  protected void addSetComponentMethod(ASTCDClass decoratedClass, String methodName, String attrType, String attrName) {
    getCD4C().addMethod(decoratedClass, "umlp.gui2ts.SetComponent", methodName, attrType, attrName);
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
    boolean convertToString = returnType.endsWith("String") && !util.isStringLiteral(namedArg.getGUIArgValue());
    if (convertToString && namedArg.getGUIArgValue().getExpression() instanceof ASTFieldAccessExpression) {
      Optional<String> zuweisungType = getTypeOf((ASTFieldAccessExpression) namedArg.getGUIArgValue().getExpression());
      convertToString = !zuweisungType.isPresent() || !zuweisungType.get().endsWith("String");
    }
    return convertToString;
  }

}
