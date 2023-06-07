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
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUI2JavaComponentDecorator extends GUIAbstractDecorator {

  protected final GUIComponentsVisitor viewComponent;
  protected final ASTGUICompilationUnit guiAst;
  protected List<TemplateHookPoint> initHPs = new ArrayList<>();

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

  public List<TemplateHookPoint> getInitHPs() {
    return initHPs;
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

    // collect args from iterate statement
    GUIIterate2ArgsVisitor it2Arg = new GUIIterate2ArgsVisitor();
    it2Arg.transform(guiAst, guiComponent);

    // replace field access with getters, except iterator args
    GUIArgValueReplaceVisitor visitor = new GUIArgValueReplaceVisitor();
    visitor.setIgnore(it2Arg.getArgs().keySet());
    if (util.isCreateClass(guiAst)) {
      visitor.setUsesBuilder(true);
    }
    SymTypeExpression paramSTE = paramSym.get().getType();
    visitor.transform(paramSTE, namedArg);
    if (!visitor.argNeedsGetter()) {
      return;
    }

    String paramType = TypeScriptTypes.toTSType(paramSym.get().getType().print());
    if (!TypeScriptTypes.isDefault(paramSym.get().getType().printFullName())) {
      getCD4C().addImport(decoratedClass, paramSym.get().getType().printFullName());
    }
    // TODO AGe: detect underspecified types
    if (paramType.equals("Array")) {
      paramType = "Array<any>";
    }
    String getterName = util.gui2cdAttrGetterName(
        guiComponent.getId().getName(), namedArg.getName());
    String setterName = util.gui2cdAttrSetterName(
        guiComponent.getId().getName(), namedArg.getName());
    if (visitor.argNeedsOwnParamWithInit()) {
      // TODO AGe: how to do that better?
      // if List
      if (namedArg.getGUIArgValue().isPresentGUIList()) {
        List<ASTGUIArgValue> valueList = namedArg.getGUIArgValue().getGUIList().getGUIArgValueList();

        String typeArg = unknownCollectionType();
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
        setDefaultListValue(attr);
        decoratedClass.addCDMember(attr);

        // TODO AGe: add this automatically
        addGetComponentMethod(decoratedClass, getterName, paramType,
            "this." + attr.getName(), getArgs(it2Arg.getArgs()), false, "");
        List<String> setterArgs = new ArrayList<>();
        setterArgs.add(paramType + " value");
        setterArgs.addAll(getArgs(it2Arg.getArgs()));
        addSetComponentMethod(getSetterTemplate(), decoratedClass, setterName, paramType,
            attr.getName(), setterArgs, false, "");

        List<String> initValues = new ArrayList<>();
        for (ASTGUIArgValue av : valueList) {
          initValues.add(guiDSLPrinter.prettyprint(av.getExpression()));
        }
        TemplateHookPoint hp = new TemplateHookPoint(
            getInitListsTemplate(), attr.getName(), setterName, isPrimitive, initValues
        );
        initHPs.add(hp);
      }
    }
    else {
      String nullCheck = "";
      if (visitor.getNullCheckExpression().isPresent()) {
        nullCheck = guiDSLPrinter.prettyprint(visitor.getNullCheckExpression().get());
      }
      String getterValue = guiDSLPrinter.prettyprint(visitor.getGetterExpression());
      boolean isLiteral = visitor.getGetterExpression() instanceof ASTLiteralExpression;
      boolean convertToString = mustBeConvertedToString(paramType, namedArg);
      if (convertToString) {
        getterValue = numberToStringCall(getterValue);
      }

      addGetComponentMethod(decoratedClass, getterName, paramType,
          getterValue, getArgs(it2Arg.getArgs()), isLiteral, nullCheck);
      if (paramSym.get().isIsReference() && visitor.getSetterExpression().isPresent()) {
        String setterValue = guiDSLPrinter.prettyprint(visitor.getSetterExpression().get());
        setterValue = convertSetter(setterValue, convertToString);
        List<String> setterArgs = new ArrayList<>();
        // TODO AGe: clean up
        setterArgs.add(paramType + " value");
        setterArgs.addAll(getArgs(it2Arg.getArgs()));
        addSetComponentMethod(getSetterReplacedTemplate(), decoratedClass, setterName, paramType,
            setterValue, setterArgs, isLiteral, nullCheck);
      }
    }
  }

  protected String getInitListsTemplate() {
    return "umlp.gui2java.InitLists";
  }

  protected String numberToStringCall(String value) {
    return "String.valueOf(" + value + ")";
  }

  protected String unknownCollectionType() {
    return "? extends Object";
  }

  protected void setDefaultListValue(ASTCDAttribute attr) {
    glex.replaceTemplate("cd2java.Value", attr,
        new StringHookPoint(" = new ArrayList<>()"));
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

  public List<String> getArgs(Map<String, String> args) {
    List<String> res = new ArrayList<>();
    for (Map.Entry arg : args.entrySet()) {
      res.add(arg.getValue() + " " + arg.getKey());
    }
    return res;
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue,
      List<String> arguments, boolean isLiteral, String condition
  ) {
    getCD4C().addMethod(
        decoratedClass, getGetterTemplate(), methodName, attrType,
        returnValue, String.join(", ", arguments), isLiteral, condition
    );
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GetComponentReplaced";
  }

  protected void addSetComponentMethod(
      String template, ASTCDClass decoratedClass, String methodName, String attrType, String attrName,
      List<String> arguments, boolean isLiteral, String condition
  ) {
    getCD4C().addMethod(decoratedClass, template, methodName, attrType, attrName,
        String.join(", ", arguments), condition);
  }

  protected String getSetterTemplate() {
    return "umlp.gui2java.SetComponent";
  }

  protected String getSetterReplacedTemplate() {
    return "umlp.gui2java.SetComponent";
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

  // TODO AGe: result of not nice above
  protected String convertSetter(String setterValue, boolean convertToString) {
    return setterValue;
  }

}
