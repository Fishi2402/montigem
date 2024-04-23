/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator;

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
    glex.replaceTemplate(
        "umlp.gui2java.GUI2JavaBuilder", new TemplateHookPoint("umlp.gui2ts.GUI2TSBuilder"));
    glex.replaceTemplate(
        "umlp.gui2java.GUI2JavaList", new TemplateHookPoint("umlp.gui2ts.GUI2TSList"));
  }

  @Override
  protected void addDirectiveAnnotation() {
    getCD4C().addImport(clazz, "umlp.jsweet.extension.annotation.Directive");
    glex.replaceTemplate("umlp.gui2ts.TSAnnotations", clazz,
        new TemplateHookPoint("umlp.gui2ts.TSAnnotationDirective"));
  }

  @Override
  protected String getStaticBuilderSyncTemplate() {
    return "umlp.gui2ts.GUI2TSStaticBuilderSync";
  }

  @Override
  protected String getStaticListSyncTemplate() {
    return "umlp.gui2ts.GUI2TSStaticListSync";
  }
}
