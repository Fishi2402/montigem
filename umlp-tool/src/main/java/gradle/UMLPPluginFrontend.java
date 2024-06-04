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

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontend implements Plugin<Project> {

  static final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  static final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME = "translateJava2TSDomain";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME = "genJava2TSConfig";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME = "genJava2TSAdditionals";
  static final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  static final String UMLP_FRONTEND_GUI_J2TS_TASK_NAME = "translateJava2TSGUI";
  static final String UMLP_FRONTEND_BUILD_IMAGE_LOCAL = "buildImageLocal";
  static final String UMLP_FRONTEND_DEPLOY_IMAGE_CI = "deployImageCI";
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

    setUpJSweetClassPath(project, j2ts, local);

    // Create Dependencies
    addProjectDependencies(project, j2ts, local);

    //Create Tasks
    TaskProvider<FrontendSetup> frontendSetupTask = createFrontendSetupTask(project);

    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = createDomainSymbolGen(project);

    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptConfigTaskTask =
        createFrontendJ2TSConfigTask(project);
    TaskProvider<Sync> syncJsweetConfigTask = createSyncJsweetConfigTask(project);
    TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask =
        createFrontendDomainJ2TSTask(project);

    TaskProvider<UMLPGuiGenTask> frontendGuiTask = createFrontendGuiTask(project, j2ts, local);
    TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask = createFrontendGUIJ2TSTask(project);
    TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals =
        createFrontendJ2TSAdditionalsTask(project);

    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<Sync> syncDomainTask = createSyncDomainTask(project, frontendSetupTask);
    TaskProvider<Sync> syncGuiTask = createSyncGuiTask(project, frontendSetupTask);
    TaskProvider<Sync> syncTSHWC = createSyncTSHWC(project, frontendSetupTask);

    createInterTaskDependencies(project, frontendSetupTask, domainSymbolGenTask, frontendDomainTask,
        java2TypeScriptConfigTaskTask, syncJsweetConfigTask, frontendDomainJ2TSTask,
        frontendGuiTask, frontendGUIJ2TSTask, java2TypeScriptAdditionals,syncDomainTask,
        syncGuiTask, syncTSHWC, j2ts, local);
    disableUnusedTasks(project, frontendSetupTask, domainSymbolGenTask, frontendDomainTask,
        java2TypeScriptConfigTaskTask, syncJsweetConfigTask, frontendDomainJ2TSTask,
        frontendGuiTask, frontendGUIJ2TSTask, java2TypeScriptAdditionals, syncDomainTask,
        syncGuiTask, syncTSHWC, j2ts);


    // Lifecycle task "generate"
    Task generateTask = createGenerateTask(project, j2ts, frontendGUIJ2TSTask, syncDomainTask,
        syncGuiTask, syncJsweetConfigTask, syncTSHWC, frontendGuiTask);

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    createContinuousTask(project, generateTask);

    // Source Sets
    addSourceSets(project, j2ts);

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
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendDomainJ2TSTask(Project project) {
    TaskProvider<UMLPJava2TypeScriptTask> j2tsTask = project.getTasks()
        .register(UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);

          // // add domain-sources to the to-be-transpiled
          umlpTask.getInput().from(project.getConfigurations().getByName("domainSources"));
          umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName("jsweettranspiler"));

          // TODO: Fix ShellAPI imports
          // Required due to "imports cannot use * wildcards when using modules"
          umlpTask.getExcludeSource().add("glob:**/shellapi/*ShellAPI.java"); // via @Erased

//          umlpTask.getInput().from(frontendDomainTask.get().g);
          umlpTask.getOutputDir().set(
              getAppDomainPath(project).toFile());
        });

    return j2tsTask;
  }

  protected TaskProvider<UMLPJava2TypeScriptConfigTask> createFrontendJ2TSConfigTask(
      Project project) {
    TaskProvider<UMLPJava2TypeScriptConfigTask> j2tsTask = project.getTasks()
        .register(UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, UMLPJava2TypeScriptConfigTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          umlpTask.getJsweetConfigOut().set(project.getLayout().getBuildDirectory().dir("jsweetGen/"));
          setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
              project.getProjectDir().getAbsolutePath(), "jsweet"));
        });

    return j2tsTask;
  }

  private TaskProvider<FrontendSetup> createFrontendSetupTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      setIfPathExists(x -> umlpTask.getInput().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "config"));
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

  private TaskProvider<UMLPGuiGenTask> createFrontendGuiTask(final Project project,
      boolean j2ts, boolean local) {
    // Add Configuration for TypeSymbols.
    Configuration guiTypes = project.getConfigurations().getByName(GUI_TYPES_NAME);

    TaskProvider<UMLPGuiGenTask> guiGenTask = project.getTasks()
        .register(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);

          umlpTask.getInput().from(getModelsPath(project));
          umlpTask.getModelDir().set(getModelsPath(project).toFile());

          umlpTask.getSymbolDir().from(guiTypes);

          if (j2ts) {
            setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
                project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
            umlpTask.getGuiGenTSFuncionality().set(false);
            umlpTask.getGuiGenJsweet().set(true);
          }
          else {
            setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
                project.getProjectDir().getAbsolutePath(), "src", "app"));
          }

          // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
          // Because the frontend uses relative paths to include HWC Code
          umlpTask.getOutputDir().set(getAppGuiPath(project).toFile());
          umlpTask.getSymbolOutDir()
              .set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain__from_task_frontendgui").toFile());

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

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendGUIJ2TSTask(final Project project) {

    return project.getTasks()
        .register(UMLP_FRONTEND_GUI_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          umlpTask.getOutputDir().set(getAppGuiJ2TSPath(project).toFile());

          // set jsweet-runtime classpath
          umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName("jsweettranspiler"));
          // add src/main/java as an input
          umlpTask.getInput().from(project.getExtensions().getByType(JavaPluginExtension.class)
              .getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava());
          // Add genGUI-outputdir as an input
//            umlpTask.getInput().from(frontendGuiTask.get().getOutputDir());

          // TODO: Remove the following at some point
          // required with getExcludeTranspile - otherwise "import { Project } from '@src/dtcockpit/Project';" is missing in GUI ts files
          // Add the domain-sources as a java-source
          umlpTask.getInput().from(project.getConfigurations().getByName("domainSources"));
          // but exclude them from being transpiled (thus, only available as source files)
          umlpTask.getExcludeTranspileFiles().from(project.getConfigurations().getByName("domainSources").getFiles());
          // TODO: END OF REMOVAL

          // TODO: Fix ShellAPI imports
          // Required due to "imports cannot use * wildcards when using modules"
          umlpTask.getExcludeSource().add("glob:**/shellapi/*ShellAPI.java"); // via @Erased

        });
  }

  private TaskProvider<UMLPJava2TypeScriptAdditionalsTask> createFrontendJ2TSAdditionalsTask(
      Project project) {
    TaskProvider<UMLPJava2TypeScriptAdditionalsTask> j2tsTask = project.getTasks()
        .register(UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME, UMLPJava2TypeScriptAdditionalsTask.class, umlpTask -> {
          umlpTask.setGroup(UMLP_GROUP_NAME);
          umlpTask.getInput().from(getModelsPath(project));
          umlpTask.getOutputDir().set(getAppGuiJ2TSConfigPath(project).toFile());

          // Always create symbols & tag files
          umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

          setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
              project.getProjectDir().getAbsolutePath(), "src", "app"));
        });

    return j2tsTask;
  }

  @NotNull
  private static TaskProvider<Sync> createSyncDomainTask(Project project,
      TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks()
        .register("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class, syncTask -> {
          syncTask.into(
              Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(),
                  "build", "appdomain").toFile());
        });
  }

  @NotNull
  private static TaskProvider<Sync> createSyncGuiTask(Project project,
                                                      TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks()
        .register("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class, syncTask -> {
          syncTask.into(
              Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(),
                  "build", "appgui").toFile());
          syncTask.exclude("**/*.java");
        });
  }

  private TaskProvider<Sync> createSyncTSHWC(Project project,
      TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks()
        .register("SYNC_TSHWC", Sync.class, syncTask -> {
          syncTask.from(new File(project.getProjectDir(), "src/main/ts"));
          syncTask.into(
              Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(),
                  "build", "appgui").toFile());
          syncTask.exclude("**/*.java");
          syncTask.getPreserve().include("**");
        });
  }

  private TaskProvider<Sync> createSyncJsweetConfigTask(Project project) {
    return project.getTasks()
        .register("SYNC_" + UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
          syncTask.into(project.getLayout().getBuildDirectory().dir("jsweet/"));
          syncTask.from(new File(project.getProjectDir(), "jsweet"));
        });
  }

  private void disableUnusedTasks(Project project, TaskProvider<FrontendSetup> frontendSetupTask,
      TaskProvider<UMLPSymbolsTask> domainSymbolGenTask,
      TaskProvider<UMLPFrontendTask> frontendDomainTask,
      TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptConfigTaskTask,
      TaskProvider<Sync> syncJsweetConfigTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask,
      TaskProvider<UMLPGuiGenTask> frontendGuiTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask,
      TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals,
      TaskProvider<Sync> syncDomainTask, TaskProvider<Sync> syncGuiTask,
      TaskProvider<Sync> syncTSHWC, boolean j2ts) {
    if (j2ts) {
      frontendDomainTask.configure(umlpTask -> {
        umlpTask.setEnabled(false);
      });
    } else {
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

  private static void createInterTaskDependencies(Project project, TaskProvider<FrontendSetup> frontendSetupTask,
      TaskProvider<UMLPSymbolsTask> domainSymbolGenTask,
      TaskProvider<UMLPFrontendTask> frontendDomainTask,
      TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptConfigTaskTask,
      TaskProvider<Sync> syncJsweetConfigTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask,
      TaskProvider<UMLPGuiGenTask> frontendGuiTask,
      TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask,
      TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals,
      TaskProvider<Sync> syncDomainTask, TaskProvider<Sync> syncGuiTask,
      TaskProvider<Sync> syncTSHWC, boolean j2ts, boolean local) {
    frontendSetupTask.configure(umlpTask -> { });
    domainSymbolGenTask.configure(umlpTask -> {
      umlpTask.dependsOn(frontendSetupTask.get());
    });
    frontendDomainTask.configure(umlpTask -> { });
    java2TypeScriptConfigTaskTask.configure(umlpTask -> { });
    syncJsweetConfigTask.configure(umlpTask -> {
      umlpTask.from(java2TypeScriptConfigTaskTask.get().getJsweetConfigOut().getAsFile());
      umlpTask.dependsOn(java2TypeScriptConfigTaskTask);
    });
    frontendDomainJ2TSTask.configure(umlpTask -> {
      umlpTask.dependsOn(syncJsweetConfigTask.get());
      umlpTask.dependsOn(domainSymbolGenTask.get());
      if (local) {
        umlpTask.dependsOn(project.findProject(":umlp-rte").getTasks().getByName("jar"));
        umlpTask.dependsOn(project.findProject(":umlp-rte:umlp-jsweet").getTasks().getByName("jar"));
      }
//      umlpTask.dependsOn(project.findProject("..:backend").getTasks().getByName("generateUmlpBackend"));
    });
    frontendGuiTask.configure(umlpTask -> {
      umlpTask.getSymbolDir().from(domainSymbolGenTask.get().getSymbolOutDir());
      umlpTask.dependsOn(domainSymbolGenTask);
      if (!j2ts) {
        // Ensure our symbols exist
        umlpTask.dependsOn(frontendDomainTask.get());
      }
    });
    if (j2ts) {
      project.getTasks().getByName("compileJava").dependsOn(frontendGuiTask.get());
    }
    frontendGUIJ2TSTask.configure(umlpTask -> {
      umlpTask.dependsOn(frontendGuiTask);
      umlpTask.dependsOn(syncJsweetConfigTask);
    });
    java2TypeScriptAdditionals.configure(umlpTask -> { });
    syncDomainTask.configure(umlpTask -> {
      if (j2ts) {
        umlpTask.from(frontendDomainJ2TSTask.get().getOutputDir());
        umlpTask.dependsOn(frontendDomainJ2TSTask.get(), frontendSetupTask.get());
      }
      else {
        umlpTask.from(frontendDomainTask.get().getOutputDir());
        umlpTask.dependsOn(frontendDomainTask.get(), frontendSetupTask.get());
      }
    });
    syncGuiTask.configure(umlpTask -> {
      if (j2ts) {
        umlpTask.from(frontendGuiTask.get().getOutputDir());
        umlpTask.from(frontendGUIJ2TSTask.get().getOutputDir());
        umlpTask.from(java2TypeScriptAdditionals.get().getOutputDir());
        umlpTask.dependsOn(frontendGUIJ2TSTask, frontendSetupTask);
      }
      else {
        umlpTask.from(frontendGuiTask.get().getOutputDir());
        umlpTask.dependsOn(frontendGuiTask, frontendSetupTask);
      }
    });
    syncTSHWC.configure(umlpTask -> {
      umlpTask.dependsOn(syncGuiTask, frontendSetupTask);
    });
  }

  private static void createContinuousTask(Project project, Task generateTask) {
    Task continuousTask = project.getTasks().create("continuous");
    continuousTask.dependsOn(generateTask);
  }

  @NotNull
  private static Task createGenerateTask(Project project, boolean j2ts,
      TaskProvider<?> frontendGUIJ2TSTask, TaskProvider<?> syncDomainTask,
      TaskProvider<?> syncGuiTask, TaskProvider<?> syncJsweetConfigTask,
      TaskProvider<Sync> syncTSHWC, TaskProvider<UMLPGuiGenTask> frontendGuiTask) {
    Task generateTask = project.getTasks().create("generate");
    if (j2ts) {
      generateTask.dependsOn(frontendGUIJ2TSTask, syncDomainTask, syncGuiTask, syncTSHWC, syncJsweetConfigTask);
    } else {
      generateTask.dependsOn(frontendGuiTask, syncDomainTask, syncGuiTask, syncTSHWC);
    }
    return generateTask;
  }

  private static void setUpJSweetClassPath(Project project, boolean j2ts, boolean local) {
    if (j2ts) {
      Configuration domainSources = project.getConfigurations().create("domainSources");
      domainSources.setTransitive(false);
      domainSources.attributes(at -> {
        at.attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, project.getObjects().named(VerificationType.class, VerificationType.MAIN_SOURCES));
        at.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      });

      // Configuration containing the compile/transpile dependencies (for running jsweet)
      Configuration jsweettranspiler = project.getConfigurations().create("jsweettranspiler");
      jsweettranspiler.extendsFrom(project.getConfigurations().getByName("compileClasspath"));

      if (local)
        project.getDependencies().add("jsweettranspiler",
            project.getDependencies().project(Map.of("path", ":umlp-rte:umlp-jsweet")));
      else
        project.getDependencies().add("jsweettranspiler",
            project.getDependencies().create("umlp:umlp-jsweet:" + project.getVersion())
        );
    }
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
      MavenArtifactRepository snapshotRepo = project.getRepositories().maven(repo -> {
        repo.setUrl("https://repository.jsweet.org/artifactory/libs-snapshot-local");
      });
      MavenArtifactRepository releaseRepo = project.getRepositories().maven(repo -> {
        repo.setUrl("https://repository.jsweet.org/artifactory/libs-release-local");
      });

      // Only use jsweet repos for everything in org.jsweet*(excl jsweet-core which is in maven-central)
      // since they throw 409 errors for dependencies in other groups
      Action<MavenRepositoryContentDescriptor> filterContent = new Action<MavenRepositoryContentDescriptor>() {
        @Override
        public void execute(MavenRepositoryContentDescriptor it) {
          it.includeModuleByRegex("org[.]jsweet.*", "(?!jsweet-core).*");
        }
      };
      snapshotRepo.mavenContent(filterContent);
      releaseRepo.mavenContent(filterContent);

      project.getDependencies().add("implementation", "org.jsweet:jsweet-core:6.3.0");
      project.getDependencies().add("implementation", "de.rwth.se.umlp.jsweet:jsweet-transpiler:3.2.0-UMLP-SNAPSHOT");
      if (local) {
        project.getDependencies().add("implementation", project.findProject(":umlp-rte:umlp-jsweet"));
      } else {
        project.getDependencies().add("implementation",
            "umlp:umlp-jsweet:" + getStringProjectProperty(project, "version", ""));
      }

      // Add dependency to jsweet-core, to import the annotations (such as @Erased)
      // Only during compilation of the backend
      // TODO: Use constant for jsweet-core
      project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");
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
  private static Path getAppGuiJ2TSConfigPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate",
        "appguiJ2TSconfig");
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

  public static Optional<String> getProjectProperty(Project project, String propertyName) {
    Object projectProperty = project.findProperty(propertyName);
    if (projectProperty == null) {
      return Optional.empty();
    } else {
      return Optional.of(projectProperty.toString());
    }
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return getProjectProperty(project, propertyName).orElse(defaultValue);
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return getProjectProperty(project, propertyName).map(Boolean::parseBoolean).orElse(false);
  }
}
