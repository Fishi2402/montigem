// (c) https://github.com/MontiCore/monticore

// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.branches;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Group;
import dtcockpit.PipelineStatistic;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BranchesExtractor {

  private String gitApiAccessToken;

  public BranchesExtractor() {
    /*
    * Get access token from the environment for now. Later we could think about sending it to the server
    * Current implication is that the environment defines which (not publicly available) GitLab instance
    * we can access.
    */
    if(System.getenv("gitApiAccessToken") == null) {
      Log.warn("'gitApiAccessToken' environment variable not set. You will not be able to" +
          " access branches for MontiVerse.");
    }
    this.gitApiAccessToken = System.getenv("gitApiAccessToken");
  }

  public void extractBranches(String hostURL, Project project) {
    GitLabApi api = new GitLabApi(hostURL, this.gitApiAccessToken);
    System.out.println("Git " + project.getGit());
    System.out.println("GitName " + project.getName());
    try {
      for (Branch branch : api.getRepositoryApi().getBranches(project.getGit())) {
        project.addBranches(DtcockpitManager.branchBuilder().name(branch.getName()).git(branch.getWebUrl()).build().get());
      }
    } catch (GitLabApiException e) {
      Log.info("Problem when trying to retrieve branches of project:", e, this.getClass().getName());
    }
  }
}
