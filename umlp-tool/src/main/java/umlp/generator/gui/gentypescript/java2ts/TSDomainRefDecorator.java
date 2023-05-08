/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.gui.genjava.decorator.DomainRefDecorator;

public class TSDomainRefDecorator extends DomainRefDecorator {

  public TSDomainRefDecorator(final GlobalExtensionManagement glex, boolean isInputGui) {
    super(glex, isInputGui);
  }

  @Override
  protected String getBuildTemplate() {
    return "umlp.gui2ts.Build";
  }

  @Override
  protected String getGetterParamTemplate() {
    return "umlp.gui2ts.GetParam";
  }

  protected String getConstructorWithBuilderTemplate() {
    return "umlp.gui2ts.ConstructorWithBuilder";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.ConstructorTS";
  }

  protected String getParam(String type, String name) {
    return type + " " + name;
  }

}
