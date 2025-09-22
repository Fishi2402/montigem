package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.montiverse.MontiVerseLite;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class StartMontiVerseLitePipeline extends StartMontiVerseLitePipelineTOP {

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public StatusResult doAction() {
    for (Project project : DtcockpitManager.getProjectList()) {
      if("MontiVerse".equals(project.getName())){
        MontiVerseLite.createPipeline(project);
        return StatusResult.ok(getId());
      }
    }

    return StatusResult.error(getId());
  }
}
