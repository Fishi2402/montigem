/* (c) https://github.com/MontiCore/monticore */
package guidslpaper.commands;

import de.se_rwth.commons.logging.Log;
import guidslpaper.GuidslpaperManager;
import guidslpaper.Konto;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Konto_create extends Konto_createTOP {
  public Konto_create(Konto konto) {
    super(konto);
  }

  public StatusResult doAction () {
    GuidslpaperManager.addWithScopeKonto(konto, NotificationScope.LOCAL);
    Log.info("Created new konto. Start extraction of PipelineAnalysisData","Konto_create");

    return StatusResult.ok(getId());
  }
}
