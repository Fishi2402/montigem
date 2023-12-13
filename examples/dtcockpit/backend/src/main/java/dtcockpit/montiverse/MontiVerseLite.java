/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.montiverse;

import com.google.common.collect.Maps;
import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.PipelineApi;
import org.gitlab4j.api.models.Branch;

import java.util.Map;

public class MontiVerseLite {

  protected static final String GIT_API_ACCESS_TOKEN = "gitApiAccessToken";
  protected static GitLabApi api;
  protected static final String HOST_URL = "https://git.rwth-aachen.de/";

  protected static Map<String, String[]> projectMap = Maps.newHashMap();

  protected static MontiVerseLite instance;

  protected MontiVerseLite() {
    projectMap.put("monticore/se-commons/se-commons",  new String[] {"SECOMMONS_BRANCH", "dev"});
    projectMap.put("monticore/tools/class2mc",  new String[] {"CLASS2MC_BRANCH", "dev"});
    projectMap.put("monticore/monticore",  new String[] {"MONTICORE_BRANCH", "dev"});
    projectMap.put("monticore/cd4analysis/cd4analysis",  new String[] {"CD4A_BRANCH", "develop"});
    projectMap.put("monticore/languages/mlc",  new String[] {"MLC_BRANCH", "develop"});
    projectMap.put("monticore/artifacts/artifacts",  new String[] {"ARTIFACTS_BRANCH", "dev"});
    projectMap.put("monticore/languages/xml",  new String[] {"XML_BRANCH", "develop"});
    projectMap.put("monticore/languages/json",  new String[] {"JSON_BRANCH", "develop"});
    projectMap.put("monticore/languages/siunits",  new String[] {"SIUNITES_BRANCH", "dev"});
    projectMap.put("monticore/langexperiments",  new String[] {"LANGEXPERIMENTS_BRANCH", "dev"});
    projectMap.put("monticore/languages/ucd",  new String[] {"UCD_BRANCH", "dev"});
    projectMap.put("monticore/languages/OCL",  new String[] {"OCL_BRANCH", "develop"});
    projectMap.put("monticore/languages/od",  new String[] {"OD_BRANCH", "dev"});
    projectMap.put("monticore/languages/gui-dsl",  new String[] {"GUIDSL_BRANCH", "dev"});
    projectMap.put("monticore/statechart/sc-language",  new String[] {"SC_BRANCH", "dev"});
    projectMap.put("monticore/languages/automaton",  new String[] {"AUTOMATON_BRANCH", "master"});
    projectMap.put("monticore/languages/feature-diagram",  new String[] {"FD_BRANCH", "develop"});
    projectMap.put("monticore/languages/montifun",  new String[] {"MONTIFUN_BRANCH", "dev"});
    projectMap.put("monticore/statechart/sd-language",  new String[] {"SD_BRANCH", "dev"});

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

  public static MontiVerseLite getInstance() {
    if (instance==null) {
      instance = new MontiVerseLite();
    }
    return instance;
  }
  public static Project createProjects() {
    Project montiVerse = DtcockpitManager.projectBuilder().name("MontiVerse").git("monticore/montiverseci").build().get();
    Map<String, Project> allProjects = Maps.newHashMap();
    DtcockpitManager.getProjectList().forEach(p -> allProjects.put(p.getName(), p));
    projectMap.entrySet().forEach(s -> {
      String simpleName = getSimpleName(s.getKey());
      Project project;
      if (!allProjects.containsKey(simpleName)) {
        project = (DtcockpitManager.projectBuilder()
            .name(simpleName)
            .git(s.getKey())
            .build().get());
      } else {
        project = allProjects.get(simpleName);
      }
      montiVerse.addDependentProjects(project);
      project.setCiInfos(DtcockpitManager.cIInformationBuilder().variableName(s.getValue()[0]).branchName(s.getValue()[1]).build().get());
    });
    return montiVerse;
  }
  public static void createBranches(Project project) {
    Log.info("Branches of " + project.getName(), "MontiVerseLite");
    try {
      for (Branch branch : api.getRepositoryApi().getBranches(project.getGit())) {
        project.addBranches(DtcockpitManager.branchBuilder().name(branch.getName()).git(branch.getWebUrl()).build().get());
      }
    } catch (GitLabApiException e) {
      Log.info("Problem when trying to retrieve branches of project:", e, "MontiVerseLite");
    }
  }

  public static void createPipeline(Project project) {
    Map<String,  String> variableMap = Maps.newHashMap();
    project.getDependentProjects().forEach(element ->
        variableMap.put(element.getCiInfos().getVariableName(), element.getCiInfos().getBranchName()));
    PipelineApi pipelineApi = new PipelineApi(api);
    try {
      Log.info("Starting the pipeline",  "MontiVerseLite");
      pipelineApi.createPipeline(project.getGit(),"main", variableMap);
    } catch (GitLabApiException e) {
      Log.info("Problem when starting the pipeline", e, "MontiVerseLite");
    }

  }

  protected static String getSimpleName(final String qualifiedName) {
    String[] splitted = qualifiedName.split("/");
    return splitted[splitted.length-1];
  }

}
