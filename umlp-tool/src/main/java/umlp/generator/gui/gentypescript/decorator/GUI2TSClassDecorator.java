/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.decorator;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
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
   * Replace CD2Java generator templates with GUI2TypeScript templates
   */
  @Override
  protected void replaceTemplates() {
    super.replaceTemplates();
    glex.replaceTemplate("cd2java.Package", new StringHookPoint(""));
  }

}
