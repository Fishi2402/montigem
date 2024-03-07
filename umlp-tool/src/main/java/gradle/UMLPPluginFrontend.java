/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontend implements Plugin<Project> {

  static final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  static final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME = "translateJava2TSDomain";
  static final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  static final String UMLP_FRONTEND_GUI_J2TS_TASK_NAME = "translateJava2TSGUI";
  private static final String CREATE_DOMAIN_SYMBOLS_TASK_NAME = "createDomainSymbols";
  static final String UMLP_GROUP_NAME = "umlp";
  private static final String GUI_TYPES_NAME = "guiTypes";
  private static final String GUI_LIB_NAME = "guiLib";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("base");

    boolean j2ts = getBooleanProjectProperty(project, "j2ts");
    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    // Create Dependencies
    addProjectDependencies(project, j2ts, local);

    //Create Tasks
    TaskProvider<FrontendSetup> frontendSetupTask = createFrontendSetupTask(project);

    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask = createFrontendDomainJ2TSTask(project, local);

    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = createDomainSymbolGen(project, frontendSetupTask);
    TaskProvider<UMLPGuiGenTask> frontendGuiTask = createFrontendGuiTask(project, j2ts, local, domainSymbolGenTask);
    TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask = createFrontendGUIJ2TSTask(project, frontendDomainJ2TSTask, frontendGuiTask);

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<?> syncDomainTask = createSyncDomainTask(project, j2ts, frontendDomainJ2TSTask,
        frontendGUIJ2TSTask, frontendSetupTask, frontendDomainTask, frontendGuiTask);
    TaskProvider<?> syncGuiTask = createSyncGuiTask(project, frontendSetupTask, j2ts, frontendGUIJ2TSTask,
        frontendDomainJ2TSTask, syncDomainTask, frontendGuiTask, frontendDomainTask);


    // Lifecycle task "generate"
    Task generateTask = createGenerateTask(project, j2ts, frontendGUIJ2TSTask, syncDomainTask,
        syncGuiTask, frontendGuiTask);

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    createContinuousTask(project, generateTask);

    // Other dependencies
    if (j2ts) {
      project.getTasks().getByName("compileJava").dependsOn(frontendGuiTask.get());
    }

    // Source Sets
    addSourceSets(project, j2ts);
  }

  protected TaskProvider<UMLPFrontendTask> createFrontendDomainTask(final Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(getModelsPath(project));

      umlpTask.getOcl().from(getOCLPath(project));

      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "app"));

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(getAppDomainPath(project).toFile());

      // Always create symbols & tag files
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain").toFile());

    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendDomainJ2TSTask(final Project project,
      boolean local) {
    TaskProvider<UMLPJava2TypeScriptTask> j2tsTask = project.getTasks()
        .register(UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          umlpTask.getOutputDir().set(
              getAppDomainPath(project).toFile());
          umlpTask.getGui().set(false);
          umlpTask.getDomain().set(true);
        });

    if (local) {
      j2tsTask.get().dependsOn(project.findProject(":umlp-rte").getTasks().getByName("jar"));
      j2tsTask.get().dependsOn(project.findProject(":umlp-rte:umlp-jsweet").getTasks().getByName("jar"));
    }

    return j2tsTask;
  }

  private TaskProvider<FrontendSetup> createFrontendSetupTask(final Project project) {
    return project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      setIfPathExists(x -> umlpTask.getInput().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "config"));

      umlpTask.getOutput().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen").toFile());
    });

  }

  private TaskProvider<UMLPSymbolsTask> createDomainSymbolGen(Project project,
      TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks().register(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLPSymbolsTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.dependsOn(frontendSetupTask.get());
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain").toFile());
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain").toFile());
    });
  }

  private TaskProvider<UMLPGuiGenTask> createFrontendGuiTask(final Project project,
      boolean j2ts, boolean local, TaskProvider<UMLPSymbolsTask> domainSymbolGenTask) {

    // Add Configuration for TypeSymbols.
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<UMLPGuiGenTask> guiGenTask = project.getTasks()
        .register(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);

          umlpTask.getInput().from(getModelsPath(project));
          umlpTask.getModelDir().set(getModelsPath(project).toFile());

          umlpTask.getSymbolDir().from(guiTypes);
          umlpTask.getSymbolDir().from(domainSymbolGenTask.get().getSymbolOutDir());

          if (j2ts) {
            setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
                project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
            umlpTask.getGuiGenTSFuncionality().set(false);
            umlpTask.getGuiGenJsweet().set(true);
          }
          else {
            setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
                project.getProjectDir().getAbsolutePath(), "src", "app"));
          }

          // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
          // Because the frontend uses relative paths to include HWC Code
          umlpTask.getOutputDir().set(getAppGuiPath(project).toFile());
          umlpTask.getSymbolOutDir()
              .set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui").toFile());

          umlpTask.getGuiGenJsweet().set(j2ts);
        });

    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks()
            .getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks()
            .getByName("createCDSymbols");
        guiGenTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        guiGenTask.get().getSymbolDir()
            .from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      guiGenTask.get().getSymbolDir().from(guiLib.getFiles());
    }

    return guiGenTask;
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendGUIJ2TSTask(final Project project,
      TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask,
      TaskProvider<UMLPGuiGenTask> frontendGuiTask) {
    return project.getTasks()
        .register(UMLP_FRONTEND_GUI_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          umlpTask.getOutputDir().set(getAppGuiJ2TSPath(project).toFile());
          umlpTask.getGui().set(true);
          umlpTask.getDomain().set(false);

          umlpTask.dependsOn(frontendGuiTask.get(), frontendDomainJ2TSTask.get());
        });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncDomainTask(Project project, boolean j2ts,
      TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask,
      TaskProvider<FrontendSetup> frontendSetupTask,
      TaskProvider<UMLPFrontendTask> frontendDomainTask,
      TaskProvider<UMLPGuiGenTask> frontendGuiTask) {
    return project.getTasks()
        .register("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class, syncTask -> {
          if (j2ts) {
            syncTask.from(frontendDomainJ2TSTask.get().getOutputDir());
            syncTask.dependsOn(frontendGUIJ2TSTask, frontendDomainJ2TSTask,
                frontendSetupTask); // I want that the sync tasks runs after all generate tasks
          }
          else {
            syncTask.from(frontendDomainTask.get().getOutputDir());
            syncTask.dependsOn(frontendGuiTask, frontendDomainTask,
                frontendSetupTask); // I want that the sync tasks runs after all generate tasks
          }
          syncTask.into(
              Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(),
                  "build", "appdomain").toFile());
        });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncGuiTask(Project project,
      TaskProvider<FrontendSetup> frontendSetupTask, boolean j2ts,
      TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask, TaskProvider<?> syncDomainTask,
      TaskProvider<UMLPGuiGenTask> frontendGuiTask,
      TaskProvider<UMLPFrontendTask> frontendDomainTask) {
    return project.getTasks()
        .register("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class, syncTask -> {
          syncTask.into(
              Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(),
                  "build", "appgui").toFile());
          if (j2ts) {
            syncTask.from(frontendGuiTask.get().getOutputDir());
            syncTask.from(frontendGUIJ2TSTask.get().getOutputDir());
            syncTask.dependsOn(frontendGUIJ2TSTask, frontendDomainJ2TSTask, syncDomainTask,
                frontendSetupTask); // I want that the sync tasks runs after all generate tasks
          }
          else {
            syncTask.from(frontendGuiTask.get().getOutputDir());
            syncTask.dependsOn(frontendGuiTask, frontendDomainTask, syncDomainTask,
                frontendSetupTask); // I want that the sync tasks runs after all generate tasks
          }
        });
  }

  private static void createContinuousTask(Project project, Task generateTask) {
    Task continuousTask = project.getTasks().create("continuous");
    continuousTask.dependsOn(generateTask);
  }

  @NotNull
  private static Task createGenerateTask(Project project, boolean j2ts,
      TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask, TaskProvider<?> syncDomainTask,
      TaskProvider<?> syncGuiTask, TaskProvider<UMLPGuiGenTask> frontendGuiTask) {
    Task generateTask = project.getTasks().create("generate");
    if (j2ts) {
      generateTask.dependsOn(frontendGUIJ2TSTask, syncDomainTask, syncGuiTask);
    } else {
      generateTask.dependsOn(frontendGuiTask, syncDomainTask, syncGuiTask);
    }
    return generateTask;
  }

  private static void addSourceSets(Project project, boolean j2ts) {
    if (j2ts) {
      SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
      SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
      main.getJava().srcDir(getAppGuiPath(project).toFile());
    }
  }

  private void addProjectDependencies(Project project, boolean j2ts, boolean local) {
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

    // JSweet
    if (j2ts) {
      project.getRepositories().maven(repo -> {
        repo.setUrl("https://repository.jsweet.org/artifactory/libs-snapshot-local");
      });
      project.getRepositories().maven(repo -> {
        repo.setUrl("https://repository.jsweet.org/artifactory/libs-release-local");
      });
      project.getDependencies().add("implementation", "org.jsweet:jsweet-core:6.3.0");
      project.getDependencies().add("implementation", "de.rwth.se.umlp.jsweet:jsweet-transpiler:3.2.0-UMLP-SNAPSHOT");
      if (local) {
        project.getDependencies().add("implementation", project.findProject(":umlp-rte:umlp-jsweet"));
      } else {
        project.getDependencies().add("implementation",
            "umlp:umlp-jsweet:" + getStringProjectProperty(project, "version", ""));
      }
    }

    // Configurations
    project.getConfigurations().create(GUI_TYPES_NAME);
    project.getDependencies().add(GUI_TYPES_NAME,
        "de.monticore.lang:guidsl:" + getStringProjectProperty(project, "mc_version", "") + ":default-types");

    if (!local) {
      project.getConfigurations().create(GUI_LIB_NAME);
      project.getDependencies().add(GUI_LIB_NAME,
          "umlp.umlp-rte:client:" + getStringProjectProperty(project, "mc_version", ""));
    }
  }

  @NotNull
  private static Path getModelsPath(Project project) {
    return Path.of(project.getProjectDir().getAbsolutePath(), "..", "models");
  }

  @NotNull
  private static Path getOCLPath(Project project) {
    return Path.of(project.getProjectDir().getAbsolutePath(), "..", "models", "ocl");
  }

  @NotNull
  private static Path getAppGuiPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate",
        "appgui");
  }

  @NotNull
  private static Path getAppGuiJ2TSPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate",
        "appguiJ2TS");
  }

  @NotNull
  private static Path getAppDomainPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate",
        "appdomain");
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }

  private static Optional<String> getProjectProperty(Project project, String propertyName) {
    Object projectProperty = project.findProperty(propertyName);
    if (projectProperty == null) {
      return Optional.empty();
    } else {
      return Optional.of(projectProperty.toString());
    }
  }

  private static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return getProjectProperty(project, propertyName).orElse(defaultValue);
  }

  @NotNull
  private static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return getProjectProperty(project, propertyName).map(Boolean::parseBoolean).orElse(false);
  }
}
