/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.montiverse;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MontiVerseLite {

  protected static final String GIT_API_ACCESS_TOKEN = "gitApiAccessToken";
  protected GitLabApi api;
  protected static final String HOST_URL = "https://git.rwth-aachen.de/";

  protected Map<String, String> projectMap = Stream.of(new String[][] {
      { "monticore/se-commons/se-commons", "$SECOMMONS_BRANCH" },
      { "monticore/tools/class2mc", "$CLASS2MC_BRANCH" },
      { "monticore/cd4analysis/cd4analysis", "CD4A_BRANCH" },
      { "monticore/languages/mlc", "$MLC_BRANCH" },
      { "monticore/artifacts/artifacts", "$ARTIFACTS_BRANCH" },
      { "monticore/languages/xml", "$XML_BRANCH" },
      { "monticore/languages/json", "$JSON_BRANCH" },
      { "monticore/languages/siunits", "$SIUNITES_BRANCH" },
      { "monticore/langexperiments", "$LANGEXPERIMENTS_BRANCH" },
      { "monticore/languages/ucd", "$UCD_BRANCH" },
      { "monticore/languages/OCL", "$OCL_BRANCH" },
      { "monticore/languages/od", "$OD_BRANCH" },
      { "monticore/languages/gui-dsl", "$GUIDSL_BRANCH" },
      { "monticore/statechart/sc-language", "$SC_BRANCH" },
      { "monticore/languages/automaton", "$AUTOMATON_BRANCH" },
      { "monticore/languages/feature-diagram", "$FD_BRANCH" },
      { "monticore/languages/montifun", "$MONTIFUN_BRANCH" },
      { "monticore/statechart/sd-language", "$SD_BRANCH" }
  }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

  public MontiVerseLite() {
    /*
    * Get access token from the environment for now. Later we could think about sending it to the server
    * Current implication is that the environment defines which (not publicly available) GitLab instance
    * we can access.
    */
    if(System.getenv(GIT_API_ACCESS_TOKEN) == null) {
      Log.warn("'gitApiAccessToken' environment variable not set. You will not be able to" +
          " access branches for MontiVerse.");
    }
    this.api = new GitLabApi(HOST_URL, System.getenv(GIT_API_ACCESS_TOKEN));
  }
  public Project createProjects() {
    Project montiVerse = DtcockpitManager.projectBuilder().name("MontiVerse").git("monticore/montiverseci").build().get();
    projectMap.entrySet().forEach(s -> {
      Project p = (DtcockpitManager.projectBuilder()
        .name(getSimpleName(s.getKey()))
        .git(s.getKey())
        .build().get());
      montiVerse.addDependentProjects(p);
      p.setCiInfos(DtcockpitManager.cIInformationBuilder().variableName(s.getValue()).build().get());
    });
    return montiVerse;
  }
  public void createBranches(Project project) {
    try {
      for (Branch branch : this.api.getRepositoryApi().getBranches(project.getGit())) {
        project.addBranches(DtcockpitManager.branchBuilder().name(branch.getName()).git(branch.getWebUrl()).build().get());
      }
    } catch (GitLabApiException e) {
      Log.info("Problem when trying to retrieve branches of project:", e, this.getClass().getName());
    }
  }
  protected String getSimpleName(final String qualifiedName) {
    String[] splitted = qualifiedName.split("/");
    return splitted[splitted.length-1];
  }

}
