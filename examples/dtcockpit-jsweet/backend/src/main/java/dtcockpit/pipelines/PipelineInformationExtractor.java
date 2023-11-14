package dtcockpit.pipelines;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.PipelineStatistic;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class PipelineInformationExtractor {

  private String gitApiAccessToken;

  public PipelineInformationExtractor() {
    init();
  }

  private void init() {
    /*
    * Get access token from the environment for now. Later we could think about sending it to the server
    * Current implication is that the environment defines which (not publicly available) GitLab instance
    * we can access.
    */
    if(System.getenv("gitApiAccessToken") == null) {
      Log.warn("'gitApiAccessToken' environment variable not set. You will not be able to" +
          " extract PipelineAnalysis data for the PipelineDashboard.");
    }
    this.gitApiAccessToken = System.getenv("gitApiAccessToken");
  }

  /**
   *
   * @param hostURL
   * @param project the GitLab Api allows a path as String or the projectID as a long
   * @param apiToken
   */
  public void extractPipelineDataOf(String hostURL, Project project, String apiToken){
    if (apiToken == null || apiToken.isEmpty()) {
      apiToken = this.gitApiAccessToken;
    }
    GitLabApi api = new GitLabApi(hostURL, apiToken);

    try{
      Pager<Pipeline> pipelinePager = api.getPipelineApi().getPipelines(project.getGit(), 100);

      // TODO for now maximum at 100 pipelines if all pipelines wanted replace if with while
      if(pipelinePager.hasNext()) {
        List<Pipeline> pipelines = pipelinePager.next();
        Log.info("Starting to extract Pipeline data ("+pipelines.size()+" pipelines) for "+hostURL+"/"+project.getGit(), "GitAPI Extraction Debugger");
        for (Pipeline pipeline: pipelines) {
          if(pipeline.getUpdatedAt() == null) {
            Log.warn("Skipped a Pipeline, because it does not have a value at UpdatedAt.");
          }
          Optional<PipelineStatistic> opt = DtcockpitManager.pipelineStatisticBuilder()
              .time(LocalDate.from(pipeline.getCreatedAt().toInstant().atZone(ZoneId.systemDefault())))
              // TODO duration ist ne schwierige Sachen in GitLab warum auch immer
              .duration((long) (pipeline.getUpdatedAt().getTime()- pipeline.getCreatedAt().getTime()))
              .successful(pipeline.getStatus().equals(PipelineStatus.SUCCESS))
              //.project(project) // TODO bidirectional assocs still broken?
              .build();
          if(opt.isEmpty()) {
            Log.warn("Could not create pipeline stats for"+
                hostURL+"/"+project.getGit()+".");
          } else {
            project.addPStatistics(opt.get());
          }
        }
      }
      Log.info("Finished to extract Pipeline data for "+hostURL+"/"+project.getGit()+
          ". Number of extracted Pipelines is:"+project.getPStatistics().size(), "GitAPI Extraction Debugger");
    } catch (GitLabApiException exceptione) {
      Log.warn("Could not extract Pipeline data for "+
          hostURL+"/"+project.getGit()+" with the given apiToken.");
    }
  }
}
