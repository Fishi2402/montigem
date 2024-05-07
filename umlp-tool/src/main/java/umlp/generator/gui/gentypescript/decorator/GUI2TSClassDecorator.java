/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator;
import umlp.generator.gui.gentypescript.printer.TypeScriptTypes;

/**
 * Turn a GUI AST into a CD AST containing one class. The class manages component state and
 * implements some logic, such as navigation to pages, creation of objects, etc.
 */
public class GUI2TSClassDecorator extends GUI2JavaClassDecorator {

  public GUI2TSClassDecorator(final GlobalExtensionManagement glex) {
    super(glex);
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
  protected void addDirectiveAnnotation() {
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
    ASTCDAttribute routerAttribute = getCDAttributeFacade().createAttribute(
        CDModifier.PROTECTED.build(), "Router", "router");
    clazz.addCDMember(routerAttribute);
  }

  @Override
  protected String getStaticBuilderInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticBuilderInit";
  }

  @Override
  protected String getAssocInitTemplate() {
    return "umlp.gui2ts.GUI2TSAssocInit";
  }

  @Override
  protected String getStaticListInitTemplate() {
    return "umlp.gui2ts.GUI2TSStaticListInit";
  }
}
