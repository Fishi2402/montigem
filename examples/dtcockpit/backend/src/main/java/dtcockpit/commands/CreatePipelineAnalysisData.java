package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.pipelines.PipelineInformationExtractor;
import umlp.backendrte.command.StatusResult;

public class CreatePipelineAnalysisData extends CreatePipelineAnalysisDataTOP {

  public CreatePipelineAnalysisData(long projectGemId) {
    super(projectGemId);
  }

  @Override
  public StatusResult doAction() {
    PipelineInformationExtractor extractor = new PipelineInformationExtractor();
    Project p = DtcockpitManager.getProjectSure(this.getProjectGemId());
    extractor.extractPipelineDataOf("https://git.rwth-aachen.de", p);
    return StatusResult.ok(this.getProjectGemId());
  }
}
