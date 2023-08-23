/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.method;

import de.monticore.cd.codegen.CDGenService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.methods.AssocMandatoryAccessorDecorator;

public class TSAssocMandatoryAccessorDecorator extends AssocMandatoryAccessorDecorator {

  public TSAssocMandatoryAccessorDecorator(
      GlobalExtensionManagement glex, CDGenService service) {
    super(glex, service);
  }

  @Override
  protected String getGetTemplate() {
    return "umlp.gui2ts.methods.TSGet";
  }

}
