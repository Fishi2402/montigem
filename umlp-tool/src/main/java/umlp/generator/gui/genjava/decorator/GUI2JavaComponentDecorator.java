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
import de.monticore.guidsl._symboltable.GUIComponentDeclarationSymbol;
import de.monticore.guidsl._symboltable.GUIParamSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.gui.GUIAbstractDecorator;
import umlp.generator.gui.GUIComponentsVisitor;
import umlp.generator.gui.genjava.prettyprinter.Expression2AccessorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2BuilderPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2MutatorPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2NullCheckPrinter;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.genjava.prettyprinter.IterationItemsFinder;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

    SymTypeExpression paramSTE = paramSym.get().getType();

    if (!util.hasGetter(paramSTE)) {
      return;
    }

    if (!TypeScriptTypes.isDefault(paramSTE.printFullName())) {
      // TODO AGe: all possible types
      if (util.isListType(paramSTE)) {
        SymTypeExpression innerType = util.getListInnerType(paramSTE).get();
        if (!util.isDomainType(innerType)) {
          if (!TypeScriptTypes.isDefault(innerType.print())
            && !innerType.printFullName().equals("java.util.List.E")) {
            getCD4C().addImport(decoratedClass, innerType.printFullName());
          }
        } else {
          Optional<String> domainQName = util.getMainDomainModelQName();
          if (domainQName.isPresent()) {
            getCD4C().addImport(decoratedClass, domainQName.get().toLowerCase() + "."
                + Names.getSimpleName(util.getListInnerType(paramSTE).get().print()));
          } else {
            Log.warn("0x0000A GUI Model " + this.guiAst.getComponentName()
                + " uses type '" + paramSTE.printFullName() + "' that could not be resolved.");
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

    String paramType = convertType(paramSTE);

    Optional<GUIComponentDeclarationSymbol> declSym = namedArg.getEnclosingScope()
        .resolveGUIComponentDeclaration(guiComponent.getName());
    boolean isBuilderArg = util.isDomainType(paramSTE)
        && declSym.isPresent()
        && util.isCreateModel(declSym.get().getFullName());
    if (isBuilderArg) {
      paramType += BuilderDecorator.BUILDER_SUFFIX;
    }

    List<VariableSymbol> iterItemSymbols = util.findIterationItemSymbols(namedArg.getEnclosingScope());
    List<String> iterItemArgs = getArgs(iterItemSymbols);

    if (namedArg.getGUIArgValue().isPresentGUIList()) {
      getCD4C().addImport(decoratedClass, "java.util.ArrayList");
      // TODO AGe: how to do that better?
      List<ASTGUIArgValue> valueList = namedArg.getGUIArgValue().getGUIList().getGUIArgValueList();

      boolean isPrimitive = false;
      if (!valueList.isEmpty()) {
        if (valueList.get(0).isPresentExpression()) {
          if (valueList.get(0).getExpression() instanceof ASTLiteralExpression) {
            isPrimitive = true;
          }
        }
        else {
          Log.error("0x00002 Cannot handle " + namedArg.getName()
              + " of component " + guiComponent.getNameSymbol().getFullName()
              + " yet: nested objects initialization");
        }
      }
      ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
          CDModifier.PROTECTED.build(),
          MCTypeFacade.getInstance().createListTypeOf(unknownCollectionType()),
          util.gui2cdAttrName(guiComponent.getId().getName(), namedArg.getName()));
      setDefaultListValue(attr);
      decoratedClass.addCDMember(attr);

      // TODO AGe: add this automatically
      addGetComponentMethod(decoratedClass, getterName, paramType,
          "this." + attr.getName(), iterItemArgs, "");
      List<String> setterArgs = new ArrayList<>();
      String argValueHolder = Expression2SetterPrinter.SETTER_PARAM;
      setterArgs.add(paramType + " " + argValueHolder);
      setterArgs.addAll(iterItemArgs);
      addSetComponentMethod(getSetterTemplate(), decoratedClass, setterName, paramType,
          attr.getName(), setterArgs, argValueHolder, "");

      List<String> initValues = new ArrayList<>();
      for (ASTGUIArgValue av : valueList) {
        initValues.add(util.print(av.getExpression()));
      }
      TemplateHookPoint hp = new TemplateHookPoint(
          getInitListsTemplate(), attr.getName(), setterName, isPrimitive, initValues, iterItemArgs
      );
      initHPs.add(hp); // MH TODO: wird irgendwie in andere Dateien injected, in welche es nicht gehört (replace Template)
    }
    else {
      IterationItemsFinder finder = new IterationItemsFinder();
      Collection<ASTExpression> iterateItems = new HashSet<>(finder.getItems(guiAst));

      // replace field access with getters
      Expression2AccessorPrinter getterPrinter = new Expression2AccessorPrinter(
          new IndentPrinter(), false, iterateItems, iterateItems
      );
      Expression2SetterPrinter setterPrinter;

      if (util.isCreateClass(guiAst)) {
        setterPrinter = new Expression2BuilderPrinter(
            new IndentPrinter(), false, iterateItems, iterateItems
        );
      } else {
        setterPrinter = new Expression2MutatorPrinter(
            new IndentPrinter(), false, iterateItems, iterateItems
        );
      }
      Expression2NullCheckPrinter nullCheckPrinter = new Expression2NullCheckPrinter(getterPrinter);

      String nullCheck = nullCheckPrinter.prettyprint(namedArg.getGUIArgValue());
      String getterValue = getterPrinter.prettyprint(namedArg.getGUIArgValue());

      Function<String, String> conversion = getConversion(decoratedClass,
          paramSTE, namedArg.getGUIArgValue().getExpression(), true);
      // TODO AGe: explicit gem lists when domain typed lists are used as an input
      if (!paramType.contains("GemList")) {
        getterValue = conversion.apply(getterValue);
      } else {
        getCD4C().addImport(decoratedClass, PathUtil.getBackendRTECommonPath("GemList"));
      }

      addGetComponentMethod(decoratedClass, getterName, paramType,
          getterValue, iterItemArgs, nullCheck);
      if (paramSym.get().isIsReference()) {
        String setterValue = setterPrinter.prettyprint(namedArg.getGUIArgValue());
        List<String> setterArgs = new ArrayList<>();
        // TODO AGe: clean up
        String valueConversion = Expression2SetterPrinter.SETTER_PARAM + "4Conv";
        setterArgs.add(paramType + " " + valueConversion);
        setterArgs.addAll(iterItemArgs);
        conversion = getConversion(
            decoratedClass, paramSTE, namedArg.getGUIArgValue().getExpression(), false
        );
        valueConversion = "var " + Expression2SetterPrinter.SETTER_PARAM + " = " + conversion.apply(valueConversion);
        addSetComponentMethod(getSetterReplacedTemplate(), decoratedClass, setterName, paramType,
            setterValue, setterArgs, valueConversion, nullCheck);
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

  public List<String> getArgs(List<VariableSymbol> symbols) {
    List<String> res = new ArrayList<>();
    for (VariableSymbol sym : symbols) {
      res.add(sym.getType().getTypeInfo().getName() + " " + sym.getName());
    }
    return res;
  }

  protected void addGetComponentMethod(
      ASTCDClass decoratedClass, String methodName, String attrType, String returnValue,
      List<String> arguments, String condition
  ) {
    getCD4C().addMethod(
        decoratedClass, getGetterTemplate(), methodName, attrType,
        returnValue, String.join(", ", arguments), condition
    );
  }

  protected String getGetterTemplate() {
    return "umlp.gui2java.GetComponentReplaced";
  }

  protected void addSetComponentMethod(
      String template, ASTCDClass decoratedClass, String methodName, String attrType, String attrName,
      List<String> arguments, String argValueHolder, String condition
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
        String type = printFullNameOfTypeHotfix(rightType);
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
        String innerType = printFullNameOfTypeHotfix(((SymTypeOfGenerics) rightType).getArgument(0));
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
      boolean rightIsOptionalNumber = util.isOptionalNumberType(rightType);
      if (leftIsString && rightIsOptionalNumber) {
        if (forGetter) {
          return numberToStringConversion();
        }
        else {
          return optionalStringToNumberConversion(util.getOptionalTypeArgument(rightType).print());
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

  // todo why is this required? Something might be VERY broken somewhere.
  protected String printFullNameOfTypeHotfix(SymTypeExpression type) {
    String name = type.getTypeInfo().getName();
    if (!Names.getQualifier(name).isEmpty()) {
      Log.warn("getName() on Symbol incorrect, expecting incorrect Symbol table," +
          "using workaround for: " + name);
      return name;
    }
    // TODO: Why is this import broken now? https://se-rwth.slack.com/archives/CNJLC0AD9/p1712748721166509
    return Joiners.DOT.join(util.getMainDomainModelQName().get(), name);
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
    List<String> toJoin = new ArrayList<>();
    if (!Names.getQualifier(type).isEmpty()) // in case the import is without a dot (otherwise CD4C fails ugly)
      toJoin.add(Names.getQualifier(type).toLowerCase());
    toJoin.add(Names.getSimpleName(type));
    getCD4C().addImport(decoratedClass, Joiners.DOT.join(toJoin));
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
    String finalType = util.getBoxedType(type);
    return s -> finalType + ".valueOf(" + s + ")";
  }

  protected Function<String, String> optionalStringToNumberConversion(String type) {
    return s -> s + " != null? " + type + ".valueOf(" + s + ") : null";
  }

}
