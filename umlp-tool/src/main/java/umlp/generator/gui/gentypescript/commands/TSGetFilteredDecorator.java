/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.GetFilteredDecorator;

public class TSGetFilteredDecorator extends GetFilteredDecorator {

  public TSGetFilteredDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  protected String getDoRunTemplate() {
    return "umlp.gui2ts.commands.get_filtered.TSDoRun";
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get_filtered.TSConstructor";
  }

  protected String getGetFiltered_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.TSGetFiltered_BuildJson";
  }

  protected String getGetFiltered_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.TSGetFiltered_FromJsonObject";
  }
}
