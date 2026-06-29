/* (c) https://github.com/MontiCore/monticore */
package loadingtests;

import de.se_rwth.commons.logging.Log;
import loadingtests.commands.Animal_get_filtered;

import loadingtests.gemlists.AnimalGemList;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.common.GemList;
import umlp.backendrte.common.GemListBuilder;
import umlp.backendrte.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LoadingTestsManagerClientImpl extends LoadingTestsManagerClientImplTOP {

  @Override
  protected void _doReset() {
    this.knownAnimals = new HashMap<>();
    this.loadedAnimalList = java.util.Optional.empty();
  }

}
