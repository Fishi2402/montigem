/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.AddDecorator;

import java.util.Set;

public class TSAddDecorator extends AddDecorator {

  public TSAddDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.add.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.add.TSDoRun";
  }

  @Override
  protected String getAdd_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_BuildJson";
  }

  @Override
  protected String getAdd_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSAdd_FromJsonObject";
  }

}
