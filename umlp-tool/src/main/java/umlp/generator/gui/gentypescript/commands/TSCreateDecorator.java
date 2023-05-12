/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.CreateDecorator;

public class TSCreateDecorator extends CreateDecorator {

  public TSCreateDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.create.TSDoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.TSConstructor";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSBuildJson";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSFromJsonObject";
  }
}
