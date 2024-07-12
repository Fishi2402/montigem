/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.UMLPFrontendCIImageTask;
import gradle.specific.UMLPFrontendLocalImageTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPJava2TypeScriptAdditionalsTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.VerificationType;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Common UMLP frontend setup.
 * Specific tasks for JSweet or No-Jsweet are configured in the specific plugins
 */
public abstract class UMLPPluginFrontendBase implements Plugin<Project> {

  static final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  static final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  static final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  static final String UMLP_FRONTEND_BUILD_IMAGE_LOCAL = "buildImageLocal";
  static final String UMLP_FRONTEND_DEPLOY_IMAGE_CI = "deployImageCI";
  static final String CREATE_DOMAIN_SYMBOLS_TASK_NAME = "createDomainSymbols";
  static final String UMLP_GROUP_NAME = "umlp";
  static final String GUI_TYPES_NAME = "guiTypes";
  static final String GUI_LIB_NAME = "guiLib";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("base");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    // Do NOT add tasks based if a j2ts property is present or not!

    // Create Dependencies
    addProjectDependencies(project, local);

    //Create Tasks
    TaskProvider<FrontendSetup> frontendSetupTask = createFrontendSetupTask(project);

    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = createDomainSymbolGen(project);

    TaskProvider<UMLPGuiGenTask> frontendGuiTask = createFrontendGuiTask(project, local);

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<Sync> syncDomainTask = createSyncDomainTask(project, frontendSetupTask);
    TaskProvider<Sync> syncGuiTask = createSyncGuiTask(project, frontendSetupTask);

    domainSymbolGenTask.configure(umlpTask -> {
      umlpTask.dependsOn(frontendSetupTask.get());
    });
    frontendGuiTask.configure(umlpTask -> {
      umlpTask.getSymbolDir().from(domainSymbolGenTask.get().getSymbolOutDir());
      umlpTask.dependsOn(domainSymbolGenTask);
    });
    //

    // Lifecycle task "generate"
    Task generateTask = project.getTasks().create("generate");
    generateTask.dependsOn(syncDomainTask, syncGuiTask);

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    createContinuousTask(project, generateTask);

    // Docker images
    createBuildImageLocalTask(project);
    createDeployImageCITask(project);
  }

  protected TaskProvider<?> createBuildImageLocalTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_BUILD_IMAGE_LOCAL, UMLPFrontendLocalImageTask.class);
  }

  protected TaskProvider<?> createDeployImageCITask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DEPLOY_IMAGE_CI, UMLPFrontendCIImageTask.class);
  }

  private TaskProvider<FrontendSetup> createFrontendSetupTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      setIfPathExists(x -> umlpTask.getInput().set(x), Path.of(project.getProjectDir().getAbsolutePath(), "config"));
      umlpTask.getOutput().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen").toFile());
    });

  }

  private TaskProvider<UMLPSymbolsTask> createDomainSymbolGen(Project project) {
    return project.getTasks().register(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLPSymbolsTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_domainsymbol").toFile());
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_domainsymbol").toFile());
    });
  }

  private TaskProvider<UMLPGuiGenTask> createFrontendGuiTask(final Project project, boolean local) {
    // Add Configuration for TypeSymbols.
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<UMLPGuiGenTask> guiGenTask = project.getTasks().register(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getModelDir().set(getModelsPath(project).toFile());

      umlpTask.getSymbolDir().from(guiTypes);

      // Further configuration is done in gradle.UMLPPluginFrontendWithJSweet and gradle.UMLPPluginFrontendNonJSweet

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(getAppGuiPath(project).toFile());
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_frontendgui").toFile());

    });

    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        guiGenTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        guiGenTask.get().getSymbolDir().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      guiGenTask.get().getSymbolDir().from(guiLib.getFiles());
    }

    return guiGenTask;
  }

  @NotNull
  private static TaskProvider<Sync> createSyncDomainTask(Project project, TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks().register("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class, syncTask -> {
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appdomain").toFile());
    });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncGuiTask(Project project, TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks().register("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class, syncTask -> {
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appgui").toFile());
      syncTask.exclude("**/*.java");
    });
  }

  //  private TaskProvider<Sync> createSyncJsweetConfigTask(Project project) {
  //    return project.getTasks()
  //        .register("SYNC_" + UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
  //          syncTask.into(project.getLayout().getBuildDirectory().dir("jsweet/"));
  //          syncTask.from(new File(project.getProjectDir(), "jsweet"));
  //        });
  //  }

  private void disableUnusedTasks(Project project, TaskProvider<FrontendSetup> frontendSetupTask, TaskProvider<UMLPSymbolsTask> domainSymbolGenTask, TaskProvider<UMLPFrontendTask> frontendDomainTask, TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptConfigTaskTask, TaskProvider<Sync> syncJsweetConfigTask, TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask, TaskProvider<UMLPGuiGenTask> frontendGuiTask, TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask, TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals, TaskProvider<Sync> syncDomainTask, TaskProvider<Sync> syncGuiTask, TaskProvider<Sync> syncTSHWC, boolean j2ts) {
    if (j2ts) {
      frontendDomainTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
    }
    else {
      frontendDomainJ2TSTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
      java2TypeScriptConfigTaskTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
      frontendGUIJ2TSTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
      java2TypeScriptAdditionals.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
      syncJsweetConfigTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
    }
  }

  private static void createContinuousTask(Project project, Task generateTask) {
    Task continuousTask = project.getTasks().create("continuous");
    continuousTask.dependsOn(generateTask);
  }

  private void addProjectDependencies(Project project, boolean local) {
    // repositories
    project.getRepositories().maven(repo -> {
      String mavenUser = getStringProjectProperty(project, "mavenUser", "");
      String mavenPassword = getStringProjectProperty(project, "mavenPassword", "");
      String repoURL = getStringProjectProperty(project, "repo", "");
      repo.getCredentials().setUsername(mavenUser);
      repo.getCredentials().setPassword(mavenPassword);
      repo.setUrl(repoURL);
    });
    project.getRepositories().mavenCentral();
    if (getBooleanProjectProperty(project, "useLocalRepo")) {
      project.getRepositories().mavenLocal();
    }

    // Configurations
    project.getConfigurations().create(GUI_TYPES_NAME);
    project.getDependencies().add(GUI_TYPES_NAME, "de.monticore.lang:guidsl:" + getStringProjectProperty(project, "mc_version", "") + ":default-types");

    if (!local) {
      project.getConfigurations().create(GUI_LIB_NAME);
      project.getDependencies().add(GUI_LIB_NAME, "umlp.umlp-rte:client:" + getStringProjectProperty(project, "mc_version", ""));
    }
  }

  @NotNull
  static Path getModelsPath(Project project) {
    return Path.of(project.getProjectDir().getAbsolutePath(), "..", "models");
  }

  @NotNull
  static Path getOCLPath(Project project) {
    return Path.of(project.getProjectDir().getAbsolutePath(), "..", "models", "ocl");
  }

  @NotNull
  static Path getAppGuiPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate", "appgui");
  }

  @NotNull
  static Path getAppDomainPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate", "appdomain");
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }

  public static Optional<String> getProjectProperty(Project project, String propertyName) {
    return UMLPPluginFrontend.getProjectProperty(project, propertyName);
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return UMLPPluginFrontend.getStringProjectProperty(project, propertyName, defaultValue);
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return UMLPPluginFrontend.getBooleanProjectProperty(project, propertyName);
  }
}
