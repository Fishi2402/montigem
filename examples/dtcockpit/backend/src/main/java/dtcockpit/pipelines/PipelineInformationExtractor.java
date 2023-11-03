package dtcockpit.pipelines;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Group;
import dtcockpit.PipelineStatistic;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PipelineInformationExtractor {

  private String gitApiAccessToken;

  public PipelineInformationExtractor() {
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

  public void extractPipelineDataOf(String hostURL, Project project) {
    GitLabApi api = new GitLabApi(hostURL, this.gitApiAccessToken);
    this.extractPipelineDataOf(api, project);
  }

  public void extractPipelineDataOfAllProjectsInGroup(String hostURL, Group group) {

    GitLabApi api = new GitLabApi(hostURL, this.gitApiAccessToken);
    try{
      Pager<org.gitlab4j.api.models.Project> projectPager = api.getGroupApi().getProjects(group.getGit(), 100);
      while(projectPager.hasNext()) {
        List<org.gitlab4j.api.models.Project> projects = projectPager.next();
        for (org.gitlab4j.api.models.Project project : projects) {
          Optional<Project> newProject =
              DtcockpitManager.projectBuilder().name(group.getName()+"/"+project.getName()).git(project.getId().toString()).build();
          extractPipelineDataOf(api, newProject.get());
          group.addSubProjects(newProject.get());
        }
      }
      createAggregatedPipelineStatistic(group);
    } catch (GitLabApiException e) {
      Log.info("Problem when trying to retrieve projects of group:", e, this.getClass().getName());
    }
  }

  private void createAggregatedPipelineStatistic(Group g) {
    Map<LocalDate, int[]> dateToRuns = new HashMap<>();
    for (Project p :g.getSubProjects()) {
      for (PipelineStatistic stat :  p.getPStatistics()) {
        if(!dateToRuns.containsKey(stat.getTime())) {
          dateToRuns.put(stat.getTime(), new int[]{0,0});
        }
        if(stat.isSuccessful()) {
          dateToRuns.get(stat.getTime())[0] +=1;
        } else {
          dateToRuns.get(stat.getTime())[1] +=1;
        }
      }
    }

    dateToRuns.keySet().forEach(date ->{
          Log.info("Add aggregated statistic to group: "+ date,this.getClass().getName());
          g.addStatistics(DtcockpitManager.aggregatedPipelineStatisticsOverDateBuilder()
          .time(date)
          .numberOfSuccessfulRuns(dateToRuns.get(date)[0])
          .numberOfUnsuccessfulRuns(dateToRuns.get(date)[1]).build().get());
        })
        ;

  }

  /**
   *
   * @param project the GitLab Api allows a path as String or the projectID as a long
   * @param api with valid token
   */
  public void extractPipelineDataOf(GitLabApi api, Project project){
    try{
      Pager<Pipeline> pipelinePager = api.getPipelineApi().getPipelines(project.getGit(), 100);

      // TODO for now maximum at 100 pipelines if all pipelines wanted replace if with while
      if(pipelinePager.hasNext()) {
        List<Pipeline> pipelines = pipelinePager.next();
        Log.info("Starting to extract Pipeline data ("+pipelines.size()+" pipelines) for "+api.getGitLabServerUrl()+"/"+project.getGit(), "GitAPI Extraction Debugger");
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
                api.getGitLabServerUrl()+"/"+project.getGit()+".");
          } else {
            project.addPStatistics(opt.get());
          }
        }
      }
      Log.info("Finished to extract Pipeline data for "+api.getGitLabServerUrl()+"/"+project.getGit()+
          ". Number of extracted Pipelines is:"+project.getPStatistics().size(), "GitAPI Extraction Debugger");
    } catch (GitLabApiException exceptione) {
      Log.warn("Could not extract Pipeline data for "+
          api.getGitLabServerUrl()+"/"+project.getGit()+" with the given apiToken.");
    }
  }
}
