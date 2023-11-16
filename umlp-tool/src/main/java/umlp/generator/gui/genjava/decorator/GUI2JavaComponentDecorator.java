/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genjava.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUIArgValue;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUINamedArg;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

    String paramType = convertType(paramSTE);
    if (!TypeScriptTypes.isDefault(paramSTE.printFullName())) {
      // TODO AGe: all possible types
      if (util.isListType(paramSTE)) {
        SymTypeExpression innerType = util.getListInnerType(paramSTE).get();
        if (!TypeScriptTypes.isDefault(paramSTE.printFullName())) {
          if (!util.isDomainType(innerType)) {
            if (!TypeScriptTypes.isDefault(innerType.print())
              && !innerType.print().equals("java.util.List.E")) {
              getCD4C().addImport(decoratedClass, innerType.print());
            }
          } else {
            Optional<String> domainQName = util.getMainDomainModelQName();
            if (domainQName.isPresent()) {
              getCD4C().addImport(decoratedClass, domainQName.get().toLowerCase() + "."
                  + Names.getSimpleName(util.getListInnerType(paramSTE).get().print()));
            } else {
              Log.warn("0x0000A GUI Model " + this.guiAst.getComponentName()
                  + " uses type '" + paramSTE.print() + "' that could not be resolved.");
            }
          }
        }
      }
      else if (!TypeScriptTypes.isDefault(paramSTE.printFullName())
          && !util.isDomainType(paramSTE)
          && !util.isDomainListType(paramSTE)
          && !util.isBuiltIn(paramSTE.printFullName())
      ) {
        getCD4C().addImport(decoratedClass, paramSTE.printFullName());
      }
      else {
        Optional<String> domainQName = util.getMainDomainModelQName();
        if (domainQName.isPresent()) {
          if (util.isDomainType(paramSTE)) {
            getCD4C().addImport(decoratedClass, domainQName.get().toLowerCase() + "." + paramSTE.print());
          }
          else if (util.isDomainListType(paramSTE) && util.getListInnerType(paramSTE).isPresent()) {
            getCD4C().addImport(decoratedClass, domainQName.get().toLowerCase() + "."
                + Names.getSimpleName(util.getListInnerType(paramSTE).get().print()));
          }
          else {
            Log.warn("0x0000A GUI Model " + this.guiAst.getComponentName()
                + " uses type '" + paramSTE.print() + "' that could not be resolved.");
          }
        }
        else {
          Log.error("0x00000 GUI Model " + this.guiAst.getComponentName()
              + " is missing a connection to the domain model."
              + " Please specify the domain model using '-dm' flag.");
        }
      }
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
        String argValueHolder = "value";
        setterArgs.add(paramType + " " + argValueHolder);
        setterArgs.addAll(getArgs(it2Arg.getArgs()));
        addSetComponentMethod(getSetterTemplate(), decoratedClass, setterName, paramType,
            attr.getName(), setterArgs, argValueHolder, false, "");

        List<String> initValues = new ArrayList<>();
        for (ASTGUIArgValue av : valueList) {
          initValues.add(guiDSLPrinter.prettyprint(av.getExpression()));
        }
        TemplateHookPoint hp = new TemplateHookPoint(
            getInitListsTemplate(), attr.getName(), setterName, isPrimitive, initValues, getArgs(it2Arg.getArgs())
        );
//        initHPs.add(hp); // MH TODO: wird irgendwie in andere Dateien injected, in welche es nicht gehört (replace Template)
      }
    }
    else {
      String nullCheck = "";
      GUINullCheckVisitor nullCheckVisitor = new GUINullCheckVisitor();
      nullCheckVisitor.transform(visitor.getGetterExpression());
      if (nullCheckVisitor.getNullCheckExpression().isPresent()) {
        nullCheck = guiDSLPrinter.prettyprint(nullCheckVisitor.getNullCheckExpression().get());
      }
      String getterValue = guiDSLPrinter.prettyprint(visitor.getGetterExpression());
      boolean isLiteral = visitor.getGetterExpression() instanceof ASTLiteralExpression;

      Function<String, String> conversion = getConversion(decoratedClass,
          paramSTE, namedArg.getGUIArgValue().getExpression(), true);
      // TODO AGe: explicit gem lists when domain typed lists are used as an input
      if (!paramType.contains("GemList")) {
        getterValue = conversion.apply(getterValue);
      } else {
        getCD4C().addImport(decoratedClass, PathUtil.getBackendRTECommonPath("GemList"));
      }

      addGetComponentMethod(decoratedClass, getterName, paramType,
          getterValue, getArgs(it2Arg.getArgs()), isLiteral, nullCheck);
      if (paramSym.get().isIsReference() && visitor.getSetterExpression().isPresent()) {
        String setterValue = guiDSLPrinter.prettyprint(visitor.getSetterExpression().get());
        List<String> setterArgs = new ArrayList<>();
        // TODO AGe: clean up
        String argValueHolder = "value";
        setterArgs.add(paramType + " " + argValueHolder);
        setterArgs.addAll(getArgs(it2Arg.getArgs()));
        conversion = getConversion(
            decoratedClass, paramSTE, namedArg.getGUIArgValue().getExpression(), false
        );
        argValueHolder = conversion.apply(argValueHolder);
        addSetComponentMethod(getSetterReplacedTemplate(), decoratedClass, setterName, paramType,
            setterValue, setterArgs, argValueHolder, isLiteral, nullCheck);
      }
    }
  }

  protected String convertType(SymTypeExpression mcType) {
    // TODO MB Bessere Lösung
    if (util.isDomainListType(mcType)) {
      return "GemList" + "<"+Names.getSimpleName(((SymTypeOfGenerics) mcType).getArgument(0).getTypeInfo().getName())+">";
    }
    if (mcType instanceof SymTypeOfGenerics) {
      return ((SymTypeOfGenerics) mcType).getBaseName()+"<"+Names.getSimpleName(((SymTypeOfGenerics) mcType).getArgument(0).getTypeInfo().getName())+">";
    }
    return mcType.print();
  }

  protected String getInitListsTemplate() {
    return "umlp.gui2java.InitLists";
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
      List<String> arguments, String argValueHolder, boolean isLiteral, String condition
  ) {
    getCD4C().addMethod(decoratedClass, template, methodName, attrType, attrName,
        String.join(", ", arguments), argValueHolder, condition);
  }

  protected String getSetterTemplate() {
    return "umlp.gui2java.SetComponentReplaced";
  }

  protected String getEnum2CapStringTemplate() { return "umlp.gui2java.TSEnumToCapString"; }

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

  // TODO AGe: replace with RTE functions and specific components
  public Function<String, String> getConversion(ASTCDClass decoratedClass, SymTypeExpression leftType, ASTExpression rightExp, boolean forGetter) {
    // TODO AGe: cannot resolve type when @iterate is present
 /*   TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());
    SymTypeExpression rightType = tc.typeOf(rightExp);*/
    GUIExpressionTypeVisitor visitor = new GUIExpressionTypeVisitor();
    Optional<SymTypeExpression> rightTypeOpt = visitor.getType(rightExp);
    if (rightTypeOpt.isPresent()) {
      SymTypeExpression rightType = rightTypeOpt.get();
      // TODO AGe: still not nice
      boolean leftIsString = TypeScriptTypes.isString(leftType);
      boolean rightIsDate = rightType.print().contains("LocalDate");

      if (leftIsString && rightIsDate) {
        if (forGetter) {
          return dateToStringConversion();
        }
        else {
          return stringToDateConversion();
        }
      }
      boolean rightIsNumber = TypeScriptTypes.isNumber(rightType);
      if (leftIsString && rightIsNumber) {
        if (forGetter) {
          return numberToStringConversion();
        }
        else {
          return stringToNumberConversion(rightType.print());
        }
      }
      boolean rightIsEnum = util.isEnumType(rightType);
      if (leftIsString && rightIsEnum) {
        String type = rightType.print();
        addImport(decoratedClass, type);
        type = type.lastIndexOf('.') > 0 ? type.substring(type.lastIndexOf('.') + 1) : type;
        if (forGetter) {
          addEnum2CapStringMethod(decoratedClass, type);
          return enumToStringConversion(type);
        }
        else {
          return stringToEnumConversion(type);
        }
      }
      boolean rightIsOptionalEnum = util.isOptionalEnumType(rightType);
      if (leftIsString && rightIsOptionalEnum) {
        String innerType = ((SymTypeOfGenerics) rightType).getArgument(0).print();
        addImport(decoratedClass, innerType);
        innerType = innerType.lastIndexOf('.') > 0 ? innerType.substring(innerType.lastIndexOf('.') + 1) : innerType;
        if (forGetter) {
          addEnum2CapStringMethod(decoratedClass, innerType);
          return enumOptionalToStringConversion(innerType);
        }
        else {
          return stringToOptionalEnumConversion(innerType);
        }
      }
      boolean rightIsGemList = util.isDomainListType(rightType);
      if (rightIsGemList) {
        return gemlistToArrayConversion();
      }
      boolean rightIsString = rightType.print().endsWith("String");
      if (leftIsString && !rightIsString && forGetter) {
        return toStringConversion();
      }
    }
    return s -> s;
  }

  protected void addEnum2CapStringMethod(ASTCDClass decoratedClass, String type) {
    if (type.length() > 1 && decoratedClass.getCDMethodList().stream().filter(m ->
        m.getName().equals("enumToCapitalizedString")).findAny().isEmpty()) {
      getCD4C().addMethod(decoratedClass, getEnum2CapStringTemplate(), type);
      addEnum2CapStringImport(decoratedClass);
    }
  }

  protected void addEnum2CapStringImport(ASTCDClass decoratedClass) {
    getCD4C().addImport(decoratedClass, "de.se_rwth.commons.StringTransformations");
  }

  protected void addImport(ASTCDClass decoratedClass, String type) {
    getCD4C().addImport(decoratedClass, Joiners.DOT.join(Names.getQualifier(type).toLowerCase(),
        Names.getSimpleName(type)));
  }

  protected Function<String, String> enumOptionalToStringConversion(String type) {
    return s -> s + "!= null?" + s + " + \"\" : \"\"";
  }

  protected Function<String, String> stringToOptionalEnumConversion(String type) {
    return s -> "Optional.of(" + type + ".valueOf(" + s + "))";
  }

  protected Function<String, String> dateToStringConversion() {
    return s -> s + ".toString()";
  }

  protected Function<String, String> stringToDateConversion() {
    return s -> "LocalDate.parse(" + s + ")";
  }

  protected Function<String, String> numberToStringConversion() {
    return s -> "String.valueOf(" + s + ")";
  }

  protected Function<String, String> enumToStringConversion(String type) {
    return s -> s + " + \"\"";
  }

  protected Function<String, String> toStringConversion() {
    return s -> s + " + \"\"";
  }

  protected Function<String, String> stringToEnumConversion(String type) {
    return s -> type + ".valueOf(" + s + ")"; // TODO: check Import
  }

  protected Function<String, String> gemlistToArrayConversion() {
    return s -> s;
  }

  protected Function<String, String> stringToNumberConversion(String type) {
    if (TypeScriptTypes.INT.getMCType().equals(type)) {
      type = TypeScriptTypes.INT_BOXED.getMCType();
    }
    if (TypeScriptTypes.LONG.getMCType().equals(type)) {
      type = TypeScriptTypes.LONG_BOXED.getMCType();
    }
    if (TypeScriptTypes.DOUBLE.getMCType().equals(type)) {
      type = TypeScriptTypes.DOUBLE_BOXED.getMCType();
    }
    if (TypeScriptTypes.FLOAT.getMCType().equals(type)) {
      type = TypeScriptTypes.FLOAT_BOXED.getMCType();
    }
    String finalType = type;
    return s -> finalType + ".valueOf(" + s + ")";
  }

}
