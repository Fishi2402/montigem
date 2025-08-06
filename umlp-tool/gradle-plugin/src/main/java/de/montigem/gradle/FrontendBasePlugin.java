/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import com.github.gradle.node.NodePlugin;
import com.github.gradle.node.npm.task.NpmTask;
import com.github.gradle.node.npm.task.NpxTask;
import de.montigem.gradle.specific.CreateSymlinkTask;
import de.montigem.gradle.specific.FrontendCIImageTask;
import de.montigem.gradle.specific.FrontendLocalImageTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.SetupFrontendTask;
import de.montigem.gradle.specific.UMLP2SymTask;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG;
import static de.montigem.gradle.module.ModuleFrontendPlugin.BUILD_LIB_TASK;

/**
 * Common UMLP frontend setup.
 * Specific tasks for JSweet or No-Jsweet are configured in the specific plugins
 */
public abstract class FrontendBasePlugin implements Plugin<Project> {

  public static final String SETUP_FRONTEND_TASK = "setupFrontend";
  public static final String UMLP2TS_TASK = "umlp2ts";
  public static final String SYNC_UMLP2TS_TASK = "sync_umlp2ts";
  public static final String CREATE_SYMLINK_TASK = "createSymlink";
  public static final String GUI2JAVA_TASK = "gui2java";
  public static final String SYNC_GUI2JAVA_TASK = "sync_gui2java";
  public static final String UMLP_FRONTEND_BUILD_IMAGE_LOCAL_TASK = "buildImageLocal";
  public static final String UMLP_FRONTEND_DEPLOY_IMAGE_CI_TASK = "deployImageCI";
  public static final String UMLP2SYM_TASK = "umlp2sym";
  public static final String UMLP_GROUP_NAME = "umlp";
  public static final String GUI_TYPES_CONFIG = "guiTypes";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply("base");
    project.getPluginManager().apply(NodePlugin.class);
    project.getPluginManager().apply(MGBasePlugin.class);

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    // Create Dependencies
    addProjectDependencies(project, local);
    addClientRTEDependency(project);

    //Create Tasks
    TaskProvider<SetupFrontendTask> setupFrontendTask = createSetupFrontendTask(project);

    Provider<Directory> umlp2symOut = project.getLayout().getBuildDirectory().dir("umlp2sym");
    TaskProvider<UMLP2SymTask> umlp2sym = createUMLP2SymTask(project, umlp2symOut);
    TaskProvider<GUI2JavaTask> gui2java = createGUI2JavaTask(project);
    gui2java.configure(task -> {
      task.getSymbolDir().from(umlp2sym);
      task.dependsOn(umlp2sym);
    });

    TaskProvider<CreateSymlinkTask> createSymlinkTask = createCreateSymlinkTask(project, local);

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<Sync> syncUMLP2TSTask = createSyncUMLP2TSTask(project, setupFrontendTask);
    TaskProvider<Sync> syncGUI2JavaTask = createSyncGUI2JavaTask(project, setupFrontendTask);
    syncGUI2JavaTask.configure(t -> t.dependsOn(gui2java));


    // Lifecycle task "generate"
    TaskProvider<Task> generate = project.getTasks().register("generate", task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.dependsOn(syncUMLP2TSTask, syncGUI2JavaTask);
    });

    var buildAppTask = createNPXTask(project, "buildApp", "build");
    buildAppTask.configure(t -> {
      t.dependsOn(createSymlinkTask, generate);
    });
    var testAppTask = createNPXTask(project, "testApp", "test");
    testAppTask.configure(t -> {
      t.dependsOn(createSymlinkTask, generate);
    });
    var runAppTask = createNPXTask(project, "runApp", "serve");
    runAppTask.configure(t -> {
      t.dependsOn(createSymlinkTask, generate);
    });
    TaskProvider<Task> npmInstall = project.getTasks().named("npmInstall");
    if (project.getGradle().getStartParameter().isRefreshDependencies()) {
      TaskProvider<Task> npmUpdate = project.getTasks().named("npm_update", task -> {
        task.dependsOn(npmInstall);
      });
      buildAppTask.configure(t -> t.dependsOn(npmUpdate));
      runAppTask.configure(t -> t.dependsOn(npmUpdate));
      testAppTask.configure(t -> t.dependsOn(npmUpdate));
    }
    else {
      buildAppTask.configure(t -> t.dependsOn(npmInstall));
      runAppTask.configure(t -> t.dependsOn(npmInstall));
      testAppTask.configure(t -> t.dependsOn(npmInstall));
    }

    project.getTasks().named("assemble", t -> t.dependsOn(buildAppTask));

    createSymlinkTask.configure(umlpTask -> {
      // app and umlp_frontend Directories need to exist
      umlpTask.dependsOn(setupFrontendTask, gui2java);
    });

    if (local) {
      TaskProvider<CreateSymlinkTask> symlinkClientProdTask = createSymlinkClientProductionCreationTask(project);
      symlinkClientProdTask.configure(t -> t.dependsOn(createSymlinkTask));
      project.getTasks().configureEach(t -> {
        if (t.getName().equals("buildApp")) {
          t.dependsOn(symlinkClientProdTask);
        }
      });
    }

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    registerContinuousTask(project, generate);

    // Docker images
    project.getTasks().register(UMLP_FRONTEND_BUILD_IMAGE_LOCAL_TASK, FrontendLocalImageTask.class);
    project.getTasks().register(UMLP_FRONTEND_DEPLOY_IMAGE_CI_TASK, FrontendCIImageTask.class);
  }

  private TaskProvider<SetupFrontendTask> createSetupFrontendTask(Project project) {
    Provider<Directory> config = project.provider(() -> {
      File f = project.file("config");
      return f.exists() ? project.getLayout().getProjectDirectory().dir("config") : null;
    });
    return project.getTasks().register(SETUP_FRONTEND_TASK, SetupFrontendTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().set(config);
      task.getOutput().set(project.getLayout().getBuildDirectory().dir("app"));
    });
  }

  private TaskProvider<UMLP2SymTask> createUMLP2SymTask(Project project, Provider<Directory> umlp2symOut) {
    return project.getTasks().register(UMLP2SYM_TASK, UMLP2SymTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(getModels(project));
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));

      task.getSymbolOutDir().set(umlp2symOut);
      task.getOutputDir().set(umlp2symOut);
      task.getModelDir().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_MODELS_CONFIG));
    });
  }

  protected static TaskProvider<GUI2JavaTask> createGUI2JavaTask(Project project) {
    // Add Configuration for TypeSymbols.
    NamedDomainObjectProvider<Configuration> guiTypes = project.getConfigurations().named(GUI_TYPES_CONFIG);

    return project.getTasks().register(GUI2JAVA_TASK, GUI2JavaTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(getModels(project));
      task.getModelDir().set(getModels(project));
      task.getSymbolDir().from(guiTypes);

      // Further configuration is done in gradle.FrontendWithJSweetPlugin and gradle.FrontendNonJSweetPlugin
      // It is important that the directory-depth of the intermediate-output is the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      task.getOutputDir().set(getIntermediateGUI2Java(project));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("gui2sym"));

      // load module symbols
      task.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG));
      task.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
      task.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
    });
  }

  private TaskProvider<CreateSymlinkTask> createCreateSymlinkTask(final Project project, boolean local) {
    return project.getTasks().register(CREATE_SYMLINK_TASK, CreateSymlinkTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      // No default configuration

      // TODO change this in the future, so we do not have to download all library's dependencies in every umlp project
      // Alternative: use pnpm or yarn or something like this
      Path sourceLibPath = project.getBuildDir().toPath().resolve("app/libs/@umlp");
      Project rteClient = project.project(":umlp-rte:client");
      for (Project subproject : rteClient.getSubprojects()) {
        TaskProvider<NpmTask> buildLib = subproject.getTasks().named(BUILD_LIB_TASK, NpmTask.class);
        task.dependsOn(buildLib);
        task.getSymlinkMap().put(
            sourceLibPath.resolve(subproject.getName()).toFile(),
            buildLib.map(t -> t.getOutputs().getFiles().getSingleFile()));
      }
    });
  }

  // This is a Task only need in the case of local=true
  private TaskProvider<CreateSymlinkTask> createSymlinkClientProductionCreationTask(final Project project) {
    return project.getTasks().register("symlinkProductionLibraries", CreateSymlinkTask.class,
        task -> {
          task.setGroup(UMLP_GROUP_NAME);
          Path compiledLibPath = project.getBuildDir().toPath().resolve("app/prodlibs/@umlp");

          Project umlpRteClient = project.project(":umlp-rte:client");
          for (Project subproject : umlpRteClient.getSubprojects()) {
            TaskProvider<NpmTask> buildLib = subproject.getTasks().named(BUILD_LIB_TASK, NpmTask.class);
            task.dependsOn(buildLib);
            task.getSymlinkMap().put(
                compiledLibPath.resolve(subproject.getName()).toFile(),
                buildLib.map(t -> t.getOutputs().getFiles().getSingleFile()));
          }
        });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncUMLP2TSTask(Project project, TaskProvider<SetupFrontendTask> setupFrontendTask) {
    Provider<Directory> syncOut = setupFrontendTask.flatMap(
        t -> t.getOutput().map(dir -> dir.dir("build/umlp2ts"))
    );
    return project.getTasks().register(SYNC_UMLP2TS_TASK, Sync.class, sync -> sync.into(syncOut));
  }

  @NotNull
  private static TaskProvider<Sync> createSyncGUI2JavaTask(Project project, TaskProvider<SetupFrontendTask> setupFrontendTask) {
    Provider<Directory> syncOut = setupFrontendTask.flatMap(
        t -> t.getOutput().map(dir -> dir.dir("build/gui2java"))
    );
    return project.getTasks().register(SYNC_GUI2JAVA_TASK, Sync.class, sync -> {
      sync.into(syncOut);
      sync.exclude("**/*.java");
    });
  }

  @NotNull
  TaskProvider<NpxTask> createNPXTask(Project project, String name, String arg) {
    return project.getTasks().register(name, NpxTask.class, task -> {
      task.getCommand().set("ng");
      task.getArgs().set(List.of(arg));
    });
  }

  private static void registerContinuousTask(Project project, Object generateTask) {
    project.getTasks().register("continuous", task -> task.dependsOn(generateTask));
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
    if (getBooleanProjectProperty(project, "useLocalRepo")) {
      project.getRepositories().mavenLocal();
    }
    project.getRepositories().mavenCentral();

    // Configurations
    project.getConfigurations().create(GUI_TYPES_CONFIG, config -> {
      config.setCanBeConsumed(false);
      config.setCanBeResolved(true);
      config.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "guidsl-types"));
      config.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, "library"));
      config.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, "jar"));
    });
    project.getDependencies().add(GUI_TYPES_CONFIG, "de.monticore.lang:guidsl:" + mcVersion(project));
  }

  @NotNull
  static Directory getModels(Project project) {
    return project.getLayout().getProjectDirectory().dir("../models");
  }

  @NotNull
  static Directory getOCLPath(Project project) {
    return getModels(project).dir("ocl");
  }

  @NotNull
  static Provider<Directory> getIntermediateGUI2Java(Project project) {
    return project.getLayout().getBuildDirectory().dir("intermediate/app/gui2java");
  }

  @NotNull
  static Provider<Directory> getIntermediateUMLP2TS(Project project) {
    return project.getLayout().getBuildDirectory().dir("intermediate/app/umlp2ts");
  }

  public static String mcVersion(Project project) {
    String value = (String) project.findProperty("mc_version");
    if (value == null || value.isEmpty()) {
      return DependencyPlugin.version;
    }
    return value;
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return FrontendPlugin.getStringProjectProperty(project, propertyName, defaultValue);
  }

  public static String getStringProjectPropertyStrip(Project project, String propertyName, String defaultValue) {
    String stringProjectProperty = FrontendPlugin.getStringProjectProperty(project,
        propertyName, defaultValue);
    return stringProjectProperty
        .replace("'", "")
        .replace("\"", "")
        .strip();
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return FrontendPlugin.getBooleanProjectProperty(project, propertyName);
  }

  public static void addClientRTEDependency(Project project) {
    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");
    if (!inRTE(project)) {
      if (local) {
        Project rteClient = project.project(":umlp-rte:client");
        project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
            project.getDependencies().project(Map.of("path", rteClient.getPath()))
        );
        project.getDependencies().add(MONTIGEM_MODULE_IMPORT_MAP_CONFIG,
            project.getDependencies().project(Map.of("path", rteClient.getPath()))
        );
      }
      else {
        project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
            "umlp:umlp-rte:" + getStringProjectProperty(project, "mc_version", "")
        );
        project.getDependencies().add(MONTIGEM_MODULE_IMPORT_MAP_CONFIG,
            "umlp:umlp-rte:" + getStringProjectProperty(project, "mc_version", "")
        );
      }
    }
  }

  public static boolean inRTE(Project project) {
    Project rte = project.findProject(":umlp-rte");

    boolean inRTE = false;
    if (rte != null) {
      inRTE = project.getPath().startsWith(rte.getPath() + ":")
          || project.getPath().equals(rte.getPath());
    }
    return inRTE;
  }
}
