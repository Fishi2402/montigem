package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.montiverse.MontiVerseLite;
import umlp.backendrte.command.StatusResult;

public class StartMontiVerseLitePipeline extends StartMontiVerseLitePipelineTOP {

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
