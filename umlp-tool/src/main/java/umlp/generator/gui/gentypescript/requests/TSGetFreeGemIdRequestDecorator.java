/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.requests;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.requests.GetFreeGemIdRequestDecorator;

public class TSGetFreeGemIdRequestDecorator extends GetFreeGemIdRequestDecorator {

  public TSGetFreeGemIdRequestDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.requests.getfreegemid.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.requests.getfreegemid.TSConstructor";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.requests.getfreegemid.TSFromJsonObject";
  }
}
