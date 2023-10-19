package dtcockpit.pipelines;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class PipelineInformationExtractor {

  private String gitApiAccessToken;

  public PipelineInformationExtractor() {
    /*
    * Get access token from the environment for now. Later we could think about sending it to the server
    * Current implication is that the environment defines which (not publicly available) GitLab instance
    * we can access.
    */
    this.gitApiAccessToken = System.getenv("gitApiAccessToken");
  }

  public void extractPipelineDataOf(String hostURL, Project projectID) {
    this.extractPipelineDataOf(hostURL, projectID, this.gitApiAccessToken);
  }

  /**
   *
   * @param hostURL
   * @param project the GitLab Api allows a path as String or the projectID as a long
   * @param apiToken
   */
  public void extractPipelineDataOf(String hostURL, Project project, String apiToken){
    GitLabApi api = new GitLabApi(hostURL, apiToken);

    try{
      List<Pipeline> pipelines = api.getPipelineApi().getPipelines(project.getGit());
      Log.info("Starting to extract Pipeline data ("+pipelines.size()+" pipelines) for "+hostURL+"/"+project.getGit(), "Info:");
      for (Pipeline pipeline: pipelines) {
        if(DtcockpitManager.pipelineStatisticBuilder()
            .time(LocalDate.from(pipeline.getStartedAt().toInstant().atZone(ZoneId.systemDefault())))
            .duration((long) pipeline.getDuration())
            .successful(pipeline.getStatus().equals(PipelineStatus.SUCCESS))
            .project(project)
            .build().isEmpty()) {
          Log.warn("Could not create pipeline stats for"+
              hostURL+"/"+project.getGit()+".");
        }
      }
      Log.info("Starting to extract Pipeline data for "+hostURL+"/"+project.getGit(), "Info:");
    } catch (GitLabApiException exceptione) {
      Log.warn("Could not extract Pipeline data for "+
          hostURL+"/"+project.getGit()+" with the given apiToken.");
    }
  }
}
