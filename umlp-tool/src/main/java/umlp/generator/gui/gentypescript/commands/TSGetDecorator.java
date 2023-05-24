/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.GetDecorator;

public class TSGetDecorator extends GetDecorator {

  public TSGetDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getGetNewlySubscribedGemIdsTemplate() {
    return "umlp.gui2ts.commands.get.TSGetNewlySubscribedGemIds";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.get.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get.TSConstructor";
  }

  @Override
  protected String getGet_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSGet_BuildJson";
  }

  @Override
  protected String getGet_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSGet_FromJsonObject";
  }
}
