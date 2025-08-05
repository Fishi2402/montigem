/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import com.github.gradle.node.npm.task.NpmTask;
import com.github.gradle.node.npm.task.NpxTask;
import de.montigem.gradle.specific.CreateSymLinkTask;
import de.montigem.gradle.specific.SetupFrontendTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.GUI2SymTask;
import de.montigem.gradle.specific.Model2JsweetConfigTask;
import de.montigem.gradle.specific.UMLP2SymTask;
import de.montigem.gradle.specific.FrontendCIImageTask;
import de.montigem.gradle.specific.FrontendLocalImageTask;
import de.montigem.gradle.specific.UMLP2TSTask;
import de.montigem.gradle.specific.UMLP2JsweetObserverTask;
import de.montigem.gradle.specific.Java2TSTask;
import de.montigem.gradle.specific.serialize.CD2SymTask;
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
import java.util.Optional;
import java.util.function.Consumer;

import static de.montigem.gradle.MGBasePlugin.GUI_LIB_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.module.ModuleFrontendPlugin.BUILD_LIB_TASK;

/**
 * Common UMLP frontend setup.
 * Specific tasks for JSweet or No-Jsweet are configured in the specific plugins
 */
public abstract class FrontendBasePlugin implements Plugin<Project> {

  public static final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  public static final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  public static final String UMLP_FRONTEND_SYMLINK_CREATION_TASK_NAME = "symlinkCreation";
  public static final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  public static final String UMLP_FRONTEND_BUILD_IMAGE_LOCAL = "buildImageLocal";
  public static final String UMLP_FRONTEND_DEPLOY_IMAGE_CI = "deployImageCI";
  public static final String CREATE_DOMAIN_SYMBOLS_TASK_NAME = "createDomainSymbols";
  public static final String CREATE_GUI_SYMBOLS_TASK_NAME = "gui2sym";
  public static final String UMLP_GROUP_NAME = "umlp";
  public static final String GUI_TYPES_NAME = "guiTypes";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(MGBasePlugin.class);
    project.getPluginManager().apply(com.github.gradle.node.NodePlugin.class);
    project.getPluginManager().apply("base");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    // Do NOT add tasks based if a j2ts property is present or not!

    // Create Dependencies
    addProjectDependencies(project, local);

    //Create Tasks
    TaskProvider<SetupFrontendTask> frontendSetupTask = createFrontendSetupTask(project);

    Provider<Directory> domainSymOutDir = project.getLayout().getBuildDirectory().dir("symbols_gui_domain__from_task_domainsymbol");
    TaskProvider<UMLP2SymTask> domainSymbolGenTask = createDomainSymbolGen(project, domainSymOutDir, local);
    TaskProvider<GUI2SymTask> gui2sym = createGuiSymbolGen(project, domainSymOutDir, local);

    TaskProvider<GUI2JavaTask> frontendGuiTask = createFrontendGuiTask(project, local);

    TaskProvider<CreateSymLinkTask> symlinkCreationTask = createSymlinkCreationTask(project, local);

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<Sync> syncDomainTask = createSyncDomainTask(project, frontendSetupTask);
    TaskProvider<Sync> syncGuiTask = createSyncGuiTask(project, frontendSetupTask);

    // Lifecycle task "generate"
    TaskProvider<Task> generate = project.getTasks().register("generate", task -> task.dependsOn(syncDomainTask, syncGuiTask));

    var buildAppTask = createNPXTask(project, "buildApp", "build");
    buildAppTask.configure(t -> {
      t.dependsOn(symlinkCreationTask, generate);
    });
    var testAppTask = createNPXTask(project, "testApp", "test");
    testAppTask.configure(t -> {
      t.dependsOn(symlinkCreationTask, generate);
    });
    var runAppTask = createNPXTask(project, "runApp", "serve");
    runAppTask.configure(t -> {
      t.dependsOn(symlinkCreationTask, generate);
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

    // TODO: document why the dependency, or change
    domainSymbolGenTask.configure(umlpTask -> {
      umlpTask.dependsOn(frontendSetupTask.get());
    });
    gui2sym.configure(umlpTask -> {
      umlpTask.getSymbolPath().from(domainSymbolGenTask.get().getSymbolOutDir());
      umlpTask.dependsOn(domainSymbolGenTask);
    });
    frontendGuiTask.configure(umlpTask -> {
      umlpTask.getSymbolDir().from(domainSymbolGenTask);
      umlpTask.dependsOn(domainSymbolGenTask);
    });

    symlinkCreationTask.configure(umlpTask -> {
      // frontendGen and umlp_frontend Directories need to exist
      umlpTask.dependsOn(frontendSetupTask, frontendGuiTask);
    });

    if (local) {
      TaskProvider<CreateSymLinkTask> symlinkClientProdTask = createSymlinkClientProductionCreationTask(project);
      symlinkClientProdTask.configure(t -> t.dependsOn(symlinkCreationTask));
      project.getTasks().configureEach(t -> {
        if (t.getName().equals("buildApp")) {
          t.dependsOn(symlinkClientProdTask);
        }
      });
    }

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    registerContinuousTask(project, generate);

    // Docker images
    createBuildImageLocalTask(project);
    createDeployImageCITask(project);
  }

  protected TaskProvider<?> createBuildImageLocalTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_BUILD_IMAGE_LOCAL, FrontendLocalImageTask.class);
  }

  protected TaskProvider<?> createDeployImageCITask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DEPLOY_IMAGE_CI, FrontendCIImageTask.class);
  }

  private TaskProvider<SetupFrontendTask> createFrontendSetupTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, SetupFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      setIfPathExists(x -> umlpTask.getInput().set(x), Path.of(project.getProjectDir().getAbsolutePath(), "config"));
      umlpTask.getOutput().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen").toFile());
    });

  }

  private TaskProvider<UMLP2SymTask> createDomainSymbolGen(Project project, Provider<Directory> symOutDir, boolean local) {
    TaskProvider<UMLP2SymTask> createDomainSymbolsTask = project.getTasks().register(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLP2SymTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(symOutDir);
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_domainsymbol").toFile());

      umlpTask.getModelDir().from(project.getConfigurations().getByName(MGBasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG));
    });

    if (local) {
      Project rteClient = project.project(":umlp-rte:client");
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, rteClient);
      createDomainSymbolsTask.configure(task -> {
        task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
      });
    }
    else {
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
          "umlp:client:" + getStringProjectProperty(project, "mc_version", "")
      );
    }
    return createDomainSymbolsTask;
  }

  private TaskProvider<GUI2SymTask> createGuiSymbolGen(Project project, Provider<Directory> domainSymDir, boolean local) {
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<GUI2SymTask> createGuiSymbolsTask = project.getTasks().register(CREATE_GUI_SYMBOLS_TASK_NAME, GUI2SymTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "gui2sym").toFile());
      umlpTask.getModelDir().set(getModelsPath(project).toFile());
      umlpTask.getSymbolPath().from(domainSymDir);
      umlpTask.getSymbolPath().from(guiTypes);
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "gui2sym").toFile());

      // umlpTask.getModelDir().from(project.getConfigurations().getByName(MGBasePlugin.UMLP_MODULES_MODELS_CONFIG));
    });

    if (local) {
      Project umlpRteClientProject = project.project(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        de.montigem.gradle.specific.serialize.GUI2SymTask createGuiSymbols = (de.montigem.gradle.specific.serialize.GUI2SymTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        CD2SymTask createCDSymbols = (CD2SymTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        createGuiSymbolsTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        createGuiSymbolsTask.get().getSymbolPath().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_CONFIG);
      createGuiSymbolsTask.get().getSymbolPath().from(
          project.provider(() -> guiLib.getFiles())
      );
    }
    return createGuiSymbolsTask;
  }

  private TaskProvider<CreateSymLinkTask> createSymlinkCreationTask(final Project project, boolean local) {
    TaskProvider<CreateSymLinkTask> symlinkCreationTask = project.getTasks().register(UMLP_FRONTEND_SYMLINK_CREATION_TASK_NAME,
        CreateSymLinkTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);
          // No default configuration

          // TODO change this in the future, so we do not have to download all library's dependencies in every umlp project
          // Alternative: use pnpm or yarn or something like this
          Path sourceLibPath = project.getBuildDir().toPath().resolve("frontendGen/libs/@umlp");
          Project umlpRteClient = project.project(":umlp-rte:client");
          for (Project subproject : umlpRteClient.getSubprojects()) {
            TaskProvider<NpmTask> buildLib = subproject.getTasks().named(BUILD_LIB_TASK, NpmTask.class);
            task.dependsOn(buildLib);
            task.getSymlinkMap().put(
                sourceLibPath.resolve(subproject.getName()).toFile(),
                buildLib.map(t -> t.getOutputs().getFiles().getSingleFile()));
          }
        });
    return symlinkCreationTask;
  }

  // This is a Task only need in the case of local=true
  private TaskProvider<CreateSymLinkTask> createSymlinkClientProductionCreationTask(final Project project) {
    return project.getTasks().register("symlinkProductionLibraries", CreateSymLinkTask.class,
        task -> {
          task.setGroup(UMLP_GROUP_NAME);
          Path compiledLibPath = project.getBuildDir().toPath().resolve("frontendGen/prodlibs/@umlp");

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

  private TaskProvider<GUI2JavaTask> createFrontendGuiTask(final Project project, boolean local) {
    // Add Configuration for TypeSymbols.
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<GUI2JavaTask> guiGenTask = project.getTasks().register(UMLP_FRONTEND_GUI_TASK_NAME, GUI2JavaTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getModelDir().set(getModelsPath(project).toFile());

      umlpTask.getSymbolDir().from(guiTypes);

      // Further configuration is done in gradle.FrontendWithJSweetPlugin and gradle.FrontendNonJSweetPlugin

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(getAppGuiPath(project).toFile());
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_frontendgui").toFile());

      // load module symbols
      umlpTask.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG));
      umlpTask.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
      umlpTask.getSymbolDir().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
    });

    if (local) {
      Project rteClient = project.project(":umlp-rte:client");
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, rteClient);
      project.getDependencies().add(MONTIGEM_MODULE_IMPORT_MAP_CONFIG, rteClient);
      // guiGenTask.configure(task -> task.dependsOn(rteClient.getTasks().named(GUI2SYM_JAR_TASK)));
    }
    else {
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
          "umlp:client:" + getStringProjectProperty(project, "mc_version", "")
      );
      project.getDependencies().add(MONTIGEM_MODULE_IMPORT_MAP_CONFIG,
          "umlp:client:" + getStringProjectProperty(project, "mc_version", "")
      );
    }

    return guiGenTask;
  }

  @NotNull
  private static TaskProvider<Sync> createSyncDomainTask(Project project, TaskProvider<SetupFrontendTask> frontendSetupTask) {
    return project.getTasks().register("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class, syncTask -> {
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appdomain").toFile());
    });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncGuiTask(Project project, TaskProvider<SetupFrontendTask> frontendSetupTask) {
    return project.getTasks().register("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class, syncTask -> {
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appgui").toFile());
      syncTask.exclude("**/*.java");
    });
  }

  @NotNull
  TaskProvider<NpxTask> createNPXTask(Project project, String name, String arg) {
    return project.getTasks().register(name, NpxTask.class, task -> {
      task.getCommand().set("ng");
      task.getArgs().set(List.of(arg));
    });
  }

  //  private TaskProvider<Sync> createSyncJsweetConfigTask(Project project) {
  //    return project.getTasks()
  //        .register("SYNC_" + UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
  //          syncTask.into(project.getLayout().getBuildDirectory().dir("jsweet/"));
  //          syncTask.from(new File(project.getProjectDir(), "jsweet"));
  //        });
  //  }

  private void disableUnusedTasks(Project project, TaskProvider<SetupFrontendTask> frontendSetupTask, TaskProvider<UMLP2SymTask> domainSymbolGenTask, TaskProvider<UMLP2TSTask> frontendDomainTask, TaskProvider<Model2JsweetConfigTask> java2TypeScriptConfigTaskTask, TaskProvider<Sync> syncJsweetConfigTask, TaskProvider<Java2TSTask> frontendDomainJ2TSTask, TaskProvider<GUI2JavaTask> frontendGuiTask, TaskProvider<Java2TSTask> frontendGUIJ2TSTask, TaskProvider<UMLP2JsweetObserverTask> java2TypeScriptAdditionals, TaskProvider<Sync> syncDomainTask, TaskProvider<Sync> syncGuiTask, TaskProvider<Sync> syncTSHWC, boolean j2ts) {
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
    project.getConfigurations().create(GUI_TYPES_NAME, config -> {
      config.setCanBeConsumed(false);
      config.setCanBeResolved(true);
      config.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "guidsl-types"));
      config.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, "library"));
      config.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, "jar"));
    });
    project.getDependencies().add(GUI_TYPES_NAME, "de.monticore.lang:guidsl:" + getStringProjectProperty(project, "mc_version", ""));
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
    return FrontendPlugin.getProjectProperty(project, propertyName);
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return FrontendPlugin.getStringProjectProperty(project, propertyName, defaultValue);
  }

  public static String getStringProjectPropertyStrip(Project project, String propertyName, String defaultValue) {
    String stringProjectProperty = FrontendPlugin.getStringProjectProperty(project,
        propertyName, defaultValue);
    if (stringProjectProperty == null) {
      return null;
    }
    else {
      return stringProjectProperty
          .replace("'", "")
          .replace("\"", "")
          .strip();
    }
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return FrontendPlugin.getBooleanProjectProperty(project, propertyName);
  }
}
