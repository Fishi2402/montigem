/* (c) https://github.com/MontiCore/monticore */
package gradle;

import com.github.gradle.node.npm.task.NpxTask;
import gradle.specific.FrontendSetup;
import gradle.specific.UMLPFrontendCIImageTask;
import gradle.specific.UMLPFrontendLocalImageTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPGui2SymTask;
import gradle.specific.UMLPJava2TypeScriptAdditionalsTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymLinkCreationTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
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
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Common UMLP frontend setup.
 * Specific tasks for JSweet or No-Jsweet are configured in the specific plugins
 */
public abstract class UMLPPluginFrontendBase implements Plugin<Project> {

  static final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  static final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  static final String UMLP_FRONTEND_SYMLINK_CREATION_TASK_NAME = "symlinkCreation";
  public static final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  static final String UMLP_FRONTEND_BUILD_IMAGE_LOCAL = "buildImageLocal";
  static final String UMLP_FRONTEND_DEPLOY_IMAGE_CI = "deployImageCI";
  static final String CREATE_DOMAIN_SYMBOLS_TASK_NAME = "createDomainSymbols";
  public static final String CREATE_GUI_SYMBOLS_TASK_NAME = "gui2sym";
  static final String UMLP_GROUP_NAME = "umlp";
  static final String GUI_TYPES_NAME = "guiTypes";
  static final String GUI_LIB_NAME = "guiLib";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply(com.github.gradle.node.NodePlugin.class);
    project.getPluginManager().apply("base");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    // Do NOT add tasks based if a j2ts property is present or not!

    // Create Dependencies
    addProjectDependencies(project, local);

    //Create Tasks
    TaskProvider<FrontendSetup> frontendSetupTask = createFrontendSetupTask(project);

    Provider<Directory> domainSymOutDir = project.getLayout().getBuildDirectory().dir("symbols_gui_domain__from_task_domainsymbol");
    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = createDomainSymbolGen(project, domainSymOutDir, local);
    TaskProvider<UMLPGui2SymTask> gui2sym = createGuiSymbolGen(project, domainSymOutDir, local);

    TaskProvider<UMLPGuiGenTask> frontendGuiTask = createFrontendGuiTask(project, local);

    TaskProvider<UMLPSymLinkCreationTask> symlinkCreationTask = createSymlinkCreationTask(project, local);

    // New lifecycle task to bundle dependencies for buildApp
    TaskProvider<Task> prepareBuildApp = project.getTasks().register("prepareBuildApp", task -> {
      task.setDescription("Bundles all dependencies for the buildApp task without executing it.");
      task.setGroup("build");
    });

    // Configure dependencies for the new prepareBuildApp task
    prepareBuildApp.configure(t -> {
      t.dependsOn(symlinkCreationTask);
      t.dependsOn("generate");
    });

    var buildAppTask = createNPXTask(project, "buildApp", "build");
    buildAppTask.configure(t -> {
      t.dependsOn(prepareBuildApp);
    });

    var testAppTask = createNPXTask(project, "testApp", "test");
    testAppTask.configure(t -> {
      t.dependsOn(symlinkCreationTask);
      t.dependsOn("generate");
    });
    var runAppTask = createNPXTask(project, "runApp", "serve");
    runAppTask.configure(t -> {
      t.dependsOn(symlinkCreationTask);
      t.dependsOn("generate");
    });

    if (project.getGradle().getStartParameter().isRefreshDependencies()) {
      TaskProvider<Task> npmUpdate = project.getTasks().named("npm_update", task -> {
        task.dependsOn("npmInstall");
      });
      prepareBuildApp.configure(t -> t.dependsOn(npmUpdate));
      runAppTask.configure(t -> t.dependsOn(npmUpdate));
      testAppTask.configure(t -> t.dependsOn(npmUpdate));
    }
    else {
      prepareBuildApp.configure(t -> t.dependsOn("npmInstall"));
      runAppTask.configure(t -> t.dependsOn("npmInstall"));
      testAppTask.configure(t -> t.dependsOn("npmInstall"));
    }

    project.getTasks().named("assemble").configure(t -> t.dependsOn(buildAppTask));

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<Sync> syncDomainTask = createSyncDomainTask(project, frontendSetupTask);
    TaskProvider<Sync> syncGuiTask = createSyncGuiTask(project, frontendSetupTask);

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

    if(local) {
      TaskProvider<UMLPSymLinkCreationTask> symlinkClientProdTask = createSymlinkClientProductionCreationTask(project);
      symlinkClientProdTask.configure(t -> t.dependsOn(symlinkCreationTask));
      prepareBuildApp.configure(t -> t.dependsOn(symlinkClientProdTask));
    }
    //

    // Lifecycle task "generate"
    TaskProvider generateTask = project.getTasks().register("generate", task -> task.dependsOn(syncDomainTask, syncGuiTask));

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    registerContinuousTask(project, generateTask);

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

  private TaskProvider<UMLPSymbolsTask> createDomainSymbolGen(Project project, Provider<Directory> symOutDir, boolean local) {
    TaskProvider<UMLPSymbolsTask> createDomainSymbolsTask = project.getTasks().register(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLPSymbolsTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(symOutDir);
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_domainsymbol").toFile());

      umlpTask.getModelDir().from(project.getConfigurations().getByName(UMLPPluginBase.UMLP_MODULES_MODELS_CONFIG));
    });

    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        createDomainSymbolsTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        createDomainSymbolsTask.get().getSymbolPath().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      createDomainSymbolsTask.get().getSymbolPath().from(
          project.provider(() -> guiLib.getFiles())
      );
    }
    return createDomainSymbolsTask;
  }

  private TaskProvider<UMLPGui2SymTask> createGuiSymbolGen(Project project, Provider<Directory> domainSymDir, boolean local) {
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<UMLPGui2SymTask> createGuiSymbolsTask = project.getTasks().register(CREATE_GUI_SYMBOLS_TASK_NAME, UMLPGui2SymTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(getModelsPath(project));
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "gui2sym").toFile());
      umlpTask.getModelDir().set(getModelsPath(project).toFile());
      umlpTask.getSymbolPath().from(domainSymDir);
      umlpTask.getSymbolPath().from(guiTypes);
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "gui2sym").toFile());

      // umlpTask.getModelDir().from(project.getConfigurations().getByName(UMLPPluginBase.UMLP_MODULES_MODELS_CONFIG));
    });

    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        createGuiSymbolsTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        createGuiSymbolsTask.get().getSymbolPath().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      createGuiSymbolsTask.get().getSymbolPath().from(
          project.provider(() -> guiLib.getFiles())
      );
    }
    return createGuiSymbolsTask;
  }

  private TaskProvider<UMLPSymLinkCreationTask> createSymlinkCreationTask(final Project project, boolean local) {
    TaskProvider<UMLPSymLinkCreationTask> symlinkCreationTask = project.getTasks().register(UMLP_FRONTEND_SYMLINK_CREATION_TASK_NAME,
        UMLPSymLinkCreationTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      // No default configuration

      // TODO change this in the future, so we do not have to download all library's dependencies in every umlp project
      // Alternative: use pnpm or yarn or something like this
      if(local) {
        Project umlpRteClientProject = project.findProject(":umlp-rte:client");
        Object libsObj = umlpRteClientProject.getExtensions().getExtraProperties().get("libs");
        if (!(libsObj instanceof List)) {
          throw new IllegalArgumentException("Expected 'libs' to be a List");
        }
        @SuppressWarnings("unchecked")
        List<String> libs = (List<String>) libsObj;

        Path sourceLibPath = project.getBuildDir().toPath().resolve("frontendGen/libs/@umlp");
        Path libProjectsSourcePath = umlpRteClientProject.getProjectDir().toPath().resolve("projects");

        for (String lib : libs) {
          File umlpLibSourceLinkTarget = libProjectsSourcePath.resolve(lib).toFile();
          File umlpLibSourceLink = sourceLibPath.resolve(lib).toFile();
          umlpTask.getSymlinkMap().put(umlpLibSourceLink, umlpLibSourceLinkTarget);
        }
      }
    });
    return symlinkCreationTask;
  }

  // This is a Task only need in the case of local=true
  private TaskProvider<UMLPSymLinkCreationTask> createSymlinkClientProductionCreationTask(final Project project) {
    TaskProvider<UMLPSymLinkCreationTask> symlinkCreationTask = project.getTasks().register("symlinkProductionLibraries",
        UMLPSymLinkCreationTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          Project umlpRteClientProject = project.findProject(":umlp-rte:client");
          umlpTask.getDependsOn().add(umlpRteClientProject.getTasks().named("buildAll"));

          Object libsObj = umlpRteClientProject.getExtensions().getExtraProperties().get("libs");
          if (!(libsObj instanceof List)) {
            throw new IllegalArgumentException("Expected 'libs' to be a List");
          }
          @SuppressWarnings("unchecked")
          List<String> libs = (List<String>) libsObj;

          Path compiledLibPath = project.getBuildDir().toPath().resolve("frontendGen/prodlibs/@umlp");
          Path libProjectsCompiledPath = umlpRteClientProject.getBuildDir().toPath().resolve("dist");
          for (String lib : libs) {
            File umlpLibCompiledLinkTarget = libProjectsCompiledPath.resolve(lib).toFile();
            File umlpLibCompiledLink = compiledLibPath.resolve(lib).toFile();
            umlpTask.getSymlinkMap().put(umlpLibCompiledLink, umlpLibCompiledLinkTarget);
          }
        });
    return symlinkCreationTask;
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
      boolean symbolJarTaskPresent = !umlpRteClientProject.getTasksByName("symbolJar", false).isEmpty();
      if (symbolJarTaskPresent) {
        guiGenTask.get().dependsOn(umlpRteClientProject.getTasks().getByName("symbolJar"));

        TaskCollection<Task> rteGenTasks = umlpRteClientProject.getTasks().matching(t -> t.getName().startsWith("gen_"));
        TaskCollection<Task> rteCopyTasks = umlpRteClientProject.getTasks().matching(t -> t.getName().startsWith("copy_"));
        guiGenTask.get().dependsOn(rteGenTasks, rteCopyTasks);
      }
    }

    Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
    guiGenTask.get().getSymbolDir().from(guiLib.getFiles());

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
    project.getRepositories().mavenCentral();
    if (getBooleanProjectProperty(project, "useLocalRepo")) {
      project.getRepositories().mavenLocal();
    }

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
    return UMLPPluginFrontend.getProjectProperty(project, propertyName);
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return UMLPPluginFrontend.getStringProjectProperty(project, propertyName, defaultValue);
  }

  public static String getStringProjectPropertyStrip(Project project, String propertyName, String defaultValue) {
    String stringProjectProperty = UMLPPluginFrontend.getStringProjectProperty(project,
        propertyName, defaultValue);
    if (stringProjectProperty == null) {
      return null;
    } else {
      return stringProjectProperty
          .replace("'", "")
          .replace("\"", "")
          .strip();
    }
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return UMLPPluginFrontend.getBooleanProjectProperty(project, propertyName);
  }
}
