/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.generator.domain.UMLPGeneratorConfig;
import umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Turn a GUI AST into a CD AST containing one class. The class manages component state and
 * implements some logic, such as navigation to pages, creation of objects, etc.
 */
public class GUI2TSClassDecorator extends GUI2JavaClassDecorator {

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
        CDModifier.PROTECTED.build(), "Router", "router");
    clazz.addCDMember(attr);
  }

  @Override
  protected void extendDomainObserver() {
    glex.replaceTemplate("umlp.gui2ts.TSClassExtends",
        clazz, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
    super.extendDomainObserver();
  }

  @Override
  protected Function<String, String> date2StringConversion() {
    return s -> s + "?.toDateString()";
  }

  @Override
  protected Function<String, String> string2DateConversion() {
    getCD4C().addImport(clazz, "mc.fenix.common.Conversion");
    return s -> "Conversion.string2Date(" + s + "); if (val == null) return";
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
    return s -> type + "[" + s + "]";
  }

  @Override
  protected Function<String, String> string2EnumConversion(String type) {
    return s -> type + "[" + s + " as keyof typeof " + type + "]";
  }

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
  protected String getInitParamBuilderTemplate() {
    return "umlp.gui2ts.GUI2TSInitParamBuilder";
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
  protected String getStaticBuilderInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticBuilderInit";
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
  protected String getBuilderInitTemplate() {
    return "umlp.gui2ts.GUI2TSBuilderInit";
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
}
