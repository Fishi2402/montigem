/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.GemListDomainDecorator;

public class TSGemListDomainDecorator extends GemListDomainDecorator {

  public TSGemListDomainDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getConstructorDefaultTemplate() {
    return "umlp.gemlist.ConstructorDefault";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gemlist.Constructor";
  }

}
