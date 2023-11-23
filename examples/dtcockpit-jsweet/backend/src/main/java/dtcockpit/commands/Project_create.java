package dtcockpit.commands;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.pipelines.PipelineInformationExtractor;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Project_create extends Project_createTOP {
  public Project_create(Project project) {
    super(project);
  }

  public StatusResult doAction () {
    DtcockpitManager.addProject(project, NotificationScope.LOCAL);
    Log.info("Created new project. Start extraction of PipelineAnalysisData","Project_create");
    PipelineInformationExtractor extractor = new PipelineInformationExtractor();
    extractor.extractPipelineDataOf("https://git.rwth-aachen.de",
        DtcockpitManager.getProjectSure(project.getGemId()), null);

    return StatusResult.ok(getId());
  }
}
