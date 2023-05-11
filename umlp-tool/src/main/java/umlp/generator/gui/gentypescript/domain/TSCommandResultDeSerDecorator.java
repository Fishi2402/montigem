/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.CommandResultDeSerDecorator;

public class TSCommandResultDeSerDecorator extends CommandResultDeSerDecorator {

  public TSCommandResultDeSerDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getSerSingleValueTemplate() {
    return "umlp.gui2ts.commandresultdeser.SerSingleValue";
  }

  @Override
  protected String getDeserSingleValueTemplate() {
    return "umlp.gui2ts.commandresultdeser.DeserSingleValue";
  }

  @Override
  protected String getFromJsonTemplate() {
    return "umlp.gui2ts.commandresultdeser.FromJson";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commandresultdeser.BuildJson";
  }

}
