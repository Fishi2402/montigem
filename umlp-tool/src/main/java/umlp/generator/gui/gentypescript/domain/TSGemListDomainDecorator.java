/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GemListDomainDecorator;

public class TSGemListDomainDecorator extends GemListDomainDecorator {

  public TSGemListDomainDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.gemlist.assocs.TSConstructor";
  }

  @Override
  protected String getLoadObjectsTemplate() {
    return "umlp.gui2ts.gemlist.TSLoadObjects";
  }

}
