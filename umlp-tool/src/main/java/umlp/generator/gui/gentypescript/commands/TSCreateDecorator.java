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
    return "umlp.gui2ts.commands.GetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.create.DoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.Constructor";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.BuildJson";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.FromJsonObject";
  }
}
