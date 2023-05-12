/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.RemoveDecorator;

import java.util.Set;

public class TSRemoveDecorator extends RemoveDecorator {

  public TSRemoveDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.remove.TSConstructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.remove.TSDoRun";
  }

  @Override
  protected String getSet_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSSet_BuildJson";
  }

  @Override
  protected String getSet_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSSet_FromJsonObject";
  }

}
