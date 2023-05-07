/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIDSLScope;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GUI2JavaComponentDecorator extends GUIAbstractDecorator {

  protected final GUIComponentsVisitor viewComponent;
  protected final ASTGUICompilationUnit guiAst;

  public GUI2JavaComponentDecorator(final GlobalExtensionManagement glex,
      ASTGUICompilationUnit guiAst, GUIComponentsVisitor viewComponent) {
    super(glex);
    this.guiAst = guiAst;
    this.viewComponent = viewComponent;
  }

  public void transform(ASTCDClass decoratedClass, ASTGUIComponent guiComponent) {
    for (ASTGUINamedArg arg : guiComponent.getGUINamedArgs().getGUINamedArgList()) {
      generatePropAndGetterForArg(decoratedClass, guiComponent, arg);
    }
  }

  protected void generatePropAndGetterForArg(
      ASTCDClass decoratedClass, ASTGUIComponent guiComponent, ASTGUINamedArg namedArg
  ) {
    Optional<GUIParamSymbol> paramSym = util.resolveGUIParamSym(
        namedArg.getEnclosingScope(), guiComponent.getName(), namedArg.getName());
    if (paramSym.isEmpty()) {
      Log.error("0x00001 Argument " + namedArg.getName()
          + " of component " + guiComponent.getName()
          + " cannot be resolved");
    }

    GUIArgValueReplaceVisitor visitor = new GUIArgValueReplaceVisitor();
    SymTypeExpression paramSTE = paramSym.get().getType();
    visitor.transform(paramSTE, namedArg);
    if (!visitor.argNeedsGetter()) {
      return;
    }

    String paramType = paramSym.get().getType().print();
    String getterName = util.gui2cdAttrGetterName(
        guiComponent.getId().getName(), namedArg.getName());
    if (visitor.argNeedsOwnParamWithInit()) {
      // TODO AGe: how to do that better?
      // if List
      if (namedArg.getGUIArgValue().isPresentGUIList()) {
        List<ASTGUIArgValue> valueList = namedArg.getGUIArgValue().getGUIList().getGUIArgValueList();

        String typeArg = "? extends Object";
        boolean isPrimitive = false;
        if (!valueList.isEmpty()) {
          if (valueList.get(0).isPresentExpression()) {
            if (valueList.get(0).getExpression() instanceof ASTLiteralExpression) {
              isPrimitive = true;
            }
          }
          else {
            Log.error("0x00002 Cannot handle " + namedArg.getName()
                + " of component " + guiComponent.getQualifiedName()
                + " yet: nested objects initialization");
          }
        }
        ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
            CDModifier.PROTECTED.build(),
            MCTypeFacade.getInstance().createListTypeOf(typeArg),
            util.gui2cdAttrName(guiComponent.getId().getName(), namedArg.getName()));
        glex.replaceTemplate("cd2java.Value", attr, new StringHookPoint(" = new ArrayList<>()"));
        decoratedClass.addCDMember(attr);

        GUIIterate2ArgsVisitor it2Arg = new GUIIterate2ArgsVisitor();
        it2Arg.transform(guiAst, guiComponent);
        addGetComponentMethod(decoratedClass, getterName, paramType, attr.getName(), it2Arg.getArgs());

        String setterName = util.gui2cdAttrSetterName(
            guiComponent.getId().getName(), namedArg.getName());
        addSetComponentMethod(decoratedClass, setterName, paramType, attr.getName());

        List<String> initValues = new ArrayList<>();
        for (ASTGUIArgValue av : valueList) {
          initValues.add(guiDSLPrinter.prettyprint(av.getExpression()));
        }
        TemplateHookPoint hp = new TemplateHookPoint(
            "umlp.gui2java.InitLists", attr.getName(), setterName, isPrimitive, initValues
        );
        glex.setAfterTemplate("umlp.gui2java.InitBody", hp);
      }
    }
    else {
      String argValue = guiDSLPrinter.prettyprint(visitor.getResult());
      boolean convertToString = mustBeConvertedToString(paramType, namedArg);
      if (convertToString) {
        argValue = "String.valueOf(" + argValue + ")";
      }

      GUIIterate2ArgsVisitor it2Arg = new GUIIterate2ArgsVisitor();
      it2Arg.transform(guiAst, guiComponent);
      addGetComponentMethod(decoratedClass, getterName, paramType, argValue, it2Arg.getArgs());
    }
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
    getCD4C().addMethod(decoratedClass, "umlp.gui2java.SetComponent", methodName, attrType, attrName);
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
