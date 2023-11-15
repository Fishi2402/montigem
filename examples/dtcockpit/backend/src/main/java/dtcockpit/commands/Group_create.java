/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Group;
import dtcockpit.pipelines.PipelineInformationExtractor;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Group_create extends Group_createTOP {
  public Group_create(Group group) {
    super(group);
  }

  public StatusResult doAction () {
    DtcockpitManager.addGroup(group, NotificationScope.LOCAL);
    Log.info("Created new group. Start extraction of PipelineAnalysisData","Group_create");
    PipelineInformationExtractor extractor = new PipelineInformationExtractor();
    // This mehtod creates a dtcockpit.Project for each project in the group
    extractor.extractPipelineDataOfAllProjectsInGroup("https://git.rwth-aachen.de",
        DtcockpitManager.getGroupSure(group.getGemId()));

    return StatusResult.ok(getId());
  }
}
