/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.commands;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.AssocCommandDecorator;

public class TSAssocCommandDecorator extends AssocCommandDecorator {

  public TSAssocCommandDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getAdd_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSAdd_DoRun";
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.assocs.TSConstructor";
  }

  @Override
  protected String getRemove_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSRemove_DoRun";
  }

  @Override
  protected String getSet_ConstructorTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_Constructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.TSGetEffectedGemIds";
  }

  @Override
  protected String getSet_Source_ExchangeTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_Source_Exchange";
  }

  @Override
  protected String getSet_DoRunTemplate() {
    return "umlp.gui2ts.commands.assocs.TSSet_DoRun";
  }

  @Override
  protected String getBuildJsonTemplate() {
    return "umlp.gui2ts.commands.assocs.TSBuildJson";
  }

  @Override
  protected String getFromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.assocs.TSFromJsonObject";
  }

}
