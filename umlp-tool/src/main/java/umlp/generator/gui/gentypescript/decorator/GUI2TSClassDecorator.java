/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator;
import umlp.generator.gui.genjava.prettyprinter.Expression2SetterPrinter;
import umlp.generator.gui.gentypescript.domain.TSObserverDecorator;
import umlp.generator.gui.gentypescript.printer.TSUtilsPrinter;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

import java.util.function.Function;

/**
 * Turn a GUI AST into a CD AST containing one class. The class manages component state and
 * implements some logic, such as navigation to pages, creation of objects, etc.
 */
public class GUI2TSClassDecorator extends GUI2JavaClassDecorator {

  protected boolean extendedMixin = false;

  public GUI2TSClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  public void transform(ASTGUICompilationUnit gui, ASTCDCompilationUnit cd) {
    if (UMLPGeneratorConfig.getInstance().isGenerateTypescript()) {
      super.transform(gui, cd);
    }
  }

  /**
   * Replace CD2Java generator and GUI2JavaClassDecorator templates with GUI2TS templates. The
   * templates to be replaced here are independent of a specific AST node. Node-dependent templates
   * have getters of form "public String get<template_name>Template()"
   */
  @Override
  protected void replaceTemplates() {
    super.replaceTemplates();
    glex.replaceTemplate(
        "cd2java.Package", new StringHookPoint(""));
    glex.replaceTemplate(
        "cd2java.Class", new TemplateHookPoint("umlp.gui2ts.TSClass"));
  }

  @Override
  protected void addClassAnnotation() {
    if (TypeScriptTypes.DIRECTIVE.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.DIRECTIVE.getJavaQName().get());
    }
    glex.replaceTemplate("umlp.gui2ts.TSAnnotations", clazz,
        new TemplateHookPoint("umlp.gui2ts.TSAnnotationDirective"));
  }

  @Override
  protected void addRouter() {
    if (TypeScriptTypes.ROUTER.getJavaQName().isPresent()) {
      getCD4C().addImport(clazz, TypeScriptTypes.ROUTER.getJavaQName().get());
    }
    ASTCDAttribute attr = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Router", "_router");
    clazz.addCDMember(attr);
  }

  @Override
  protected void extendDomainObserver() {
    if (!extendedMixin) {
      extendMixin();
      extendedMixin = true;
    }
    glex.addBeforeTemplate("umlp.gui2ts.TSEmptyMixin",
        clazz, new StringHookPoint(getAndImportDomainObserver() + "("));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        clazz, new StringHookPoint(")"));
  }

  @Override
  protected void implementAssocObserver(SymTypeExpression ownerType, ASTFieldAccessExpression assoc) {
    if (!extendedMixin) {
      extendMixin();
      extendedMixin = true;
    }
    String qObserverMixin = ownerType.printFullName() + ObserverDecorator.SUFFIX + "Mixin";
    String observerMixin = ownerType.print() + ObserverDecorator.SUFFIX + "Mixin";
    getCD4C().addImport(clazz, qObserverMixin);

    glex.addBeforeTemplate("umlp.gui2ts.TSEmptyMixin",
        clazz, new StringHookPoint(observerMixin + "("));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        clazz, new StringHookPoint(")"));
  }

  protected void extendMixin() {
    glex.addAfterTemplate("umlp.gui2ts.TSClassExtends",
        clazz, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
  }

  @Override
  protected String getConvertedValueAssignment(String type, String varName, String value) {
    return "const" + " " + varName + ": " + type + " = " + value;
  }

  @Override
  protected Function<String, String> date2StringConversion() {
    return dateTime2StringConversion();
  }

  @Override
  protected Function<String, String> string2DateConversion() {
    return string2DateTimeConversion();
  }

  @Override
  protected Function<String, String> dateTime2StringConversion() {
    return s -> s + "?.toDateString()";
  }

  @Override
  protected Function<String, String> string2DateTimeConversion() {
    getCD4C().addImport(clazz, "mc.fenix.common.Conversion");
    return s -> "Conversion.string2Date(" + s + ");"
        + " if (" + Expression2SetterPrinter.SETTER_PARAM + "== null) return";
  }

  @Override
  protected Function<String, String> number2StringConversion() {
    return s -> s + "+ ''";
  }

  @Override
  protected Function<String, String> string2NumberConversion(String type) {
    return s -> "+" + s;
  }

  @Override
  protected Function<String, String> enum2StringConversion(String type) {
    return s -> "(" + type + "[" + s + "]"
        + " ? " + type + "[" + s + "]" + ".charAt(0).toUpperCase()"
        + " + " + type + "[" + s + "]" + ".slice(1).toLowerCase()"
        + " : ''" + ")";
  }

  @Override
  protected Function<String, String> string2EnumConversion(String type) {
    return s -> type + "[" + s + ".toUpperCase() as keyof typeof " + type + "]";
  }

  /*
  @Override
  protected Function<String, String> optionalNumber2StringConversion() {
    return s -> s + " != null ? " + s + "+ ''" + " : ''";
  }

  @Override
  protected Function<String, String> string2OptionalNumberConversion(String type) {
    return s -> "!!" + s + " ? " + "+" + s + " : null";
  }

  @Override
  protected Function<String, String> optionalEnum2StringConversion(String type) {
    return s -> s + " != null ? " + type + "[" + s + "]" + " : ''";
  }

  @Override
  protected Function<String, String> string2OptionalEnumConversion(String type) {
    return s -> s + " in " + type + "? " + type + "[" + s + ".toUpperCase()] : null";
  }
  /**/

  protected Function<String, String> optional2XConversion(Function<String, String> conv) {
    return s -> s + " != null ? " + conv.apply(s) + " : null";
  }

  protected Function<String, String> x2OptionalConversion(Function<String, String> conv) {
    return s -> s + " != null ? " + conv.apply(s) + " : null";
  }

  @Override
  protected String printType(ASTMCType type) {
    return new TSUtilsPrinter().printType(type);
  }

  @Override
  protected String getInitDefaultParamTemplate() {
    return "umlp.gui2ts.GUI2TSInitDefaultParam";
  }

  @Override
  protected String getInitDomainListTemplate() {
    return "umlp.gui2ts.GUI2TSInitDomainList";
  }

  @Override
  protected String getRemoveDomainListTemplate() {
    return "umlp.gui2ts.GUI2TSRemoveDomainList";
  }

  @Override
  protected String getAddDomainListTemplate() {
    return "umlp.gui2ts.GUI2TSAddDomainList";
  }

  @Override
  protected String getInitParamSingletonTemplate() {
    return "umlp.gui2ts.GUI2TSInitParamSingleton";
  }

  @Override
  protected String getInitDerivedDomainParamTemplate() {
    return "umlp.gui2ts.GUI2TSInitDerivedDomainParam";
  }

  @Override
  protected String getInitDomainParamTemplate() {
    return "umlp.gui2ts.GUI2TSInitDomainParam";
  }

  @Override
  protected String getListInitTemplate() {
    return "umlp.gui2ts.GUI2TSListInit";
  }

  @Override
  protected String getIterateInitTemplate() {
    return "umlp.gui2ts.GUI2TSIterateInit";
  }

  @Override
  protected String getListAssocInitTemplate() {
    return "umlp.gui2ts.GUI2TSListAssocInit";
  }

  @Override
  protected String getObjectAssocInitTemplate() {
    return "umlp.gui2ts.GUI2TSObjectAssocInit";
  }

  @Override
  protected String getStaticObjectGetterTemplate() {
    return "umlp.gui2ts.GUI2TSStaticObjectGetter";
  }

  @Override
  protected String getStaticListInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticListInit";
  }

  @Override
  protected String getStaticBuilderInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticBuilderInit";
  }

  @Override
  protected String getStaticConstructorInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticConstructorInit";
  }

  @Override
  protected String getBuilderInitTemplate() {
    return "umlp.gui2ts.GUI2TSBuilderInit";
  }

  @Override
  protected String getConstructorInitTemplate() {
    return "umlp.gui2ts.GUI2TSConstructorInit";
  }

  @Override
  protected String getEventCallerTemplate() {
    return "umlp.gui2ts.GUI2TSEventCaller";
  }

  @Override
  protected String getCreateTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSCreate";
  }

  @Override
  protected String getDeleteTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSDelete";
  }

  @Override
  protected String getAssocAddTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSAssocAdd";
  }

  @Override
  protected String getAssocRemoveTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSAssocRemove";
  }

  @Override
  protected String getAssocSetTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSAssocAdd";
  }

  @Override
  protected String getAssocSetAbsentTemplate() {
    return "umlp.gui2ts.predefined.GUI2TSAssocRemove";
  }
}
