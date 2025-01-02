/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.se_rwth.commons.logging.Log;
import gradle.specific.FrontendSetup;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPJava2TypeScriptAdditionalsTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymbolsTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.VerificationType;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

import static gradle.UMLPPluginFrontendBase.CREATE_DOMAIN_SYMBOLS_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_DOMAIN_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_SETUP_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_GROUP_NAME;
import static gradle.UMLPPluginFrontendBase.getStringProjectProperty;
import static gradle.UMLPPluginFrontendBase.getStringProjectPropertyStrip;

/**
 * The JSweet-specific gradle setup
 */
public abstract class UMLPPluginFrontendWithJSweet implements Plugin<Project> {

  static final String UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME = "translateJava2TSDomain";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME = "genJava2TSDomainConfig";
  static final String UMLP_FRONTEND_GUI_J2TS_CONFIG_TASK_NAME = "genJava2TSGUIConfig";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME = "genJava2TSAdditionals";
  static final String UMLP_FRONTEND_GUI_J2TS_TASK_NAME = "translateJava2TSGUI";
  private static final String SYNC_PREFIX = "SYNC_";

  private static final String DOMAIN_MODEL_PROPERTY = "domainModel";
  private static final String DOMAIN_SOURCES_PROPERTY = "domainSources";

  private static final String JSWEETTRANSPILER = "jsweettranspiler";

  @Override
  public void apply(Project project) {
    setDirectories(project);

    project.getPluginManager().apply(UMLPPluginFrontendBase.class);

    boolean local = UMLPPluginFrontendBase.getBooleanProjectProperty(project, "useLocalDependencies");
    String domainModel = getStringProjectPropertyStrip(project, DOMAIN_MODEL_PROPERTY, "");
    if (domainModel.equals("")) {
      Log.warn("0JSB001 Could not identify the domain model. Please set the `domainModel` property in your `gradle.poperties` file.");
    }
    setUpJSweetClassPath(project, local);

    TaskProvider<FrontendSetup> frontendSetupTask = project.getTasks().named(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class);

    TaskProvider<UMLPGuiGenTask> frontendGuiTask = project.getTasks().named(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class);
    TaskProvider<Sync> syncDomainTask = project.getTasks().named(SYNC_PREFIX + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class);
    TaskProvider<Sync> syncGuiTask = project.getTasks().named(SYNC_PREFIX + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class);
    TaskProvider<Sync> syncTSGuiHWC = createSyncTSGUIHWC(project, frontendSetupTask);
    TaskProvider<Sync> syncTSDomainHWC = createSyncTSDomainHWC(project, frontendSetupTask);

    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = project.getTasks().named(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLPSymbolsTask.class);

    TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask = createFrontendGUIJ2TSTask(project);
    TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals = createFrontendJ2TSAdditionalsTask(project);

    TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptDomainConfigTask = createFrontendJ2TSDomainConfigTask(project, domainModel);
    TaskProvider<Sync> syncJsweetDomainConfigTask = createSyncJsweetDomainConfigTask(project);

    TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptGUIConfigTask = createFrontendJ2TSGUIConfigTask(project, domainModel);
    TaskProvider<Sync> syncJsweetGUIConfigTask = createSyncJsweetGUIConfigTask(project);
    TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask = createFrontendDomainJ2TSTask(project);
    this.addSourceSets(project);
    this.addProjectDependencies(project, local);

    // dependencies
    frontendGuiTask.configure(umlpTask -> {
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), this.jsweetJavaHWCDir);
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), this.jsweetTSGUIHWCDir);
      umlpTask.getImportsDir().from(frontendDomainJ2TSTask.get().getOutputDir());
      umlpTask.dependsOn(frontendDomainJ2TSTask);

      umlpTask.getGuiGenTSFuncionality().set(false);
      umlpTask.getGuiGenJsweet().set(true);
    });
    syncJsweetDomainConfigTask.configure(umlpTask -> {
      umlpTask.from(java2TypeScriptDomainConfigTask.get().getJsweetConfigOut().getAsFile());
      umlpTask.dependsOn(java2TypeScriptDomainConfigTask);
      umlpTask.into(this.jsweetBuildDomainDir);
      if (java2TypeScriptDomainConfigTask.get().getHwcDir().isPresent()) {
        umlpTask.from(java2TypeScriptDomainConfigTask.get().getHwcDir());
      }
    });
    syncJsweetGUIConfigTask.configure(umlpTask -> {
      umlpTask.from(java2TypeScriptGUIConfigTask.get().getJsweetConfigOut().getAsFile());
      umlpTask.dependsOn(java2TypeScriptGUIConfigTask);
      umlpTask.into(this.jsweetBuildGUIDir);
      if (java2TypeScriptGUIConfigTask.get().getHwcDir().isPresent()) {
        umlpTask.from(java2TypeScriptGUIConfigTask.get().getHwcDir());
      }
    });
    frontendDomainJ2TSTask.configure(umlpTask -> {
      umlpTask.getJsweetDir().set(syncJsweetDomainConfigTask.get().getDestinationDir());
      umlpTask.dependsOn(syncJsweetDomainConfigTask.get());
      umlpTask.dependsOn(domainSymbolGenTask);
      if (local) {
        umlpTask.dependsOn(project.findProject(":umlp-rte").getTasks().getByName("jar"));
        umlpTask.dependsOn(project.findProject(":umlp-test-rte").getTasks().getByName("jar"));
        umlpTask.dependsOn(project.findProject(":umlp-rte:umlp-jsweet").getTasks().getByName("jar"));
      }
    });

    project.getTasks().getByName("compileJava").dependsOn(frontendGuiTask.get());

    frontendGUIJ2TSTask.configure(umlpTask -> {
      umlpTask.getJsweetDir().set(syncJsweetGUIConfigTask.get().getDestinationDir());
      umlpTask.dependsOn(frontendGuiTask);
      umlpTask.dependsOn(syncJsweetGUIConfigTask);
    });

    syncDomainTask.configure(umlpTask -> {
      umlpTask.from(frontendDomainJ2TSTask.get().getOutputDir());
      umlpTask.dependsOn(frontendDomainJ2TSTask.get(), frontendSetupTask.get());
    });

    syncGuiTask.configure(umlpTask -> {
      umlpTask.from(frontendGuiTask.get().getOutputDir());
      umlpTask.from(frontendGUIJ2TSTask.get().getOutputDir());
      umlpTask.from(java2TypeScriptAdditionals.get().getOutputDir());
      umlpTask.dependsOn(frontendGUIJ2TSTask, frontendSetupTask);
    });

    syncTSGuiHWC.configure(umlpTask -> {
      umlpTask.dependsOn(syncGuiTask, frontendSetupTask);
    });

    syncTSDomainHWC.configure(umlpTask -> {
      umlpTask.dependsOn(syncDomainTask, frontendSetupTask);
    });

    project.getTasks().named("generate").configure(generateTask -> {
      generateTask.dependsOn(frontendGUIJ2TSTask, syncJsweetDomainConfigTask, syncTSGuiHWC, syncTSDomainHWC);
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendDomainJ2TSTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getJsweetDir().set(jsweetBuildDomainDir.toFile());
      umlpTask.getJsweetWorkingDir().set(jsweetWorkingDir.resolve(umlpTask.getName()).toFile());
      umlpTask.getCandiesJsOutDir().set(umlpTask.getJsweetWorkingDir().dir("candies"));
      umlpTask.getOutputDir().set(jsweetDomainOutDir.toFile());

      // // add domain-sources to the to-be-transpiled
      umlpTask.getInput().from(project.getConfigurations().getByName(DOMAIN_SOURCES_PROPERTY));
      umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName(JSWEETTRANSPILER));

      // TODO: Fix ShellAPI imports
      // Required due to "imports cannot use * wildcards when using modules"
      umlpTask.getExcludeSource().add("glob:**/shellapi/*ShellAPI.java"); // via @Erased
      umlpTask.getExcludeSource().add("glob:**/Abstract*E2ETest*.java"); // via @Erased
      umlpTask.getExcludeSource().add("glob:**/test_infrastructure/*.java"); // via @Erased
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptConfigTask> createFrontendJ2TSDomainConfigTask(Project project,
      String domainModel) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, UMLPJava2TypeScriptConfigTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getDomainModel().set(domainModel);
      umlpTask.getGenerateForDomain().set(true);
      umlpTask.getJsweetConfigOut().set(jsweetDomainConfigOutDir.toFile());
      setIfPathExists(x -> umlpTask.getHwcDir().set(x), jsweetDomainConfigHWCDir);
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptConfigTask> createFrontendJ2TSGUIConfigTask(Project project,
      String domainModel) {
    return project.getTasks().register(UMLP_FRONTEND_GUI_J2TS_CONFIG_TASK_NAME, UMLPJava2TypeScriptConfigTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getDomainModel().set(domainModel);
      umlpTask.getGenerateForGui().set(true);
      umlpTask.getJsweetConfigOut().set(jsweetGuiConfigOutDir.toFile());
      setIfPathExists(x -> umlpTask.getHwcDir().set(x), jsweetGUIConfigHWCDir);
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendGUIJ2TSTask(final Project project) {

    return project.getTasks().register(UMLP_FRONTEND_GUI_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getJsweetDir().set(jsweetBuildGUIDir.toFile());
      umlpTask.getJsweetWorkingDir().set(jsweetWorkingDir.resolve(umlpTask.getName()).toFile());
      umlpTask.getCandiesJsOutDir().set(umlpTask.getJsweetWorkingDir().dir("candies"));
      umlpTask.getOutputDir().set(jsweetGUIOutDir.toFile());

      // set jsweet-runtime classpath
      umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName(JSWEETTRANSPILER));
      // add src/main/java as an input
      umlpTask.getInput().from(project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava());
      // Add genGUI-outputdir as an input
      //            umlpTask.getInput().from(frontendGuiTask.get().getOutputDir());

      // TODO: Remove the following at some point
      // required with getExcludeTranspile - otherwise "import { Project } from '@src/dtcockpit/Project';" is missing in GUI ts files
      // Add the domain-sources as a java-source
      umlpTask.getInput().from(project.getConfigurations().getByName(DOMAIN_SOURCES_PROPERTY));
      // but exclude them from being transpiled (thus, only available as source files)
      umlpTask.getExcludeTranspileFiles().from(project.getConfigurations().getByName(
          DOMAIN_SOURCES_PROPERTY).getFiles());
      // TODO: END OF REMOVAL

      // TODO: Fix ShellAPI imports
      // Required due to "imports cannot use * wildcards when using modules"
      umlpTask.getExcludeSource().add("glob:**/shellapi/*ShellAPI.java"); // via @Erased
      umlpTask.getExcludeSource().add("glob:**/Abstract*E2ETest*.java"); // via @Erased
      umlpTask.getExcludeSource().add("glob:**/test_infrastructure/*.java"); // via @Erased

    });
  }

  private TaskProvider<UMLPJava2TypeScriptAdditionalsTask> createFrontendJ2TSAdditionalsTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME, UMLPJava2TypeScriptAdditionalsTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(UMLPPluginFrontendBase.getModelsPath(project));
      umlpTask.getOutputDir().set(jsweetGUIAdditionalsOutDir.toFile());

      // Always create symbols & tag files
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

      setIfPathExists(x -> umlpTask.getHwcDir().from(x), this.jsweetTSGUIHWCDir);
    });
  }

  private TaskProvider<Sync> createSyncJsweetDomainConfigTask(Project project) {
    return project.getTasks().register(SYNC_PREFIX + UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
    });
  }

  private TaskProvider<Sync> createSyncJsweetGUIConfigTask(Project project) {
    return project.getTasks().register(SYNC_PREFIX + UMLP_FRONTEND_GUI_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
    });
  }

  private TaskProvider<Sync> createSyncTSDomainHWC(Project project, TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks().register(SYNC_PREFIX + "TSDomainHWC", Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.from(jsweetTSDomainHWCDir);
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appdomain").toFile());
      syncTask.exclude("**/*.java");
      syncTask.getPreserve().include("**");
    });
  }

  private TaskProvider<Sync> createSyncTSGUIHWC(Project project, TaskProvider<FrontendSetup> frontendSetupTask) {
    return project.getTasks().register(SYNC_PREFIX + "TSGuiHWC", Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.from(jsweetTSGUIHWCDir);
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appgui").toFile());
      syncTask.exclude("**/*.java");
      syncTask.getPreserve().include("**");
    });
  }

  void setUpJSweetClassPath(Project project, boolean local) {
    project.getConfigurations().register(DOMAIN_SOURCES_PROPERTY, configuration -> {
      configuration.setTransitive(false);
      configuration.attributes(at -> {
        at.attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
                     project.getObjects()
                         .named(VerificationType.class, VerificationType.MAIN_SOURCES));
        at.attribute(Bundling.BUNDLING_ATTRIBUTE,
                     project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      });
    });

    // Configuration containing the compile/transpile dependencies (for running jsweet)
    project.getConfigurations().register(JSWEETTRANSPILER, configuration -> {
      configuration.extendsFrom(project.getConfigurations().getByName("compileClasspath"));
    });

    if (local) {
      project.getDependencies().add(
          JSWEETTRANSPILER, project.getDependencies().project(Map.of("path", ":umlp-rte:umlp-jsweet")));
    } else {
      String mc_version = getStringProjectProperty(project, "mc_version", "")
          .replace("'", "").replace("\"", "").strip();
      if ("".equals(mc_version)) {
        project.getDependencies().add(JSWEETTRANSPILER,
            project.getDependencies().create("umlp:umlp-jsweet"));
      } else {
        project.getDependencies().add(JSWEETTRANSPILER,
            project.getDependencies().create("umlp:umlp-jsweet:" + mc_version));
      }
    }
  }

  void addSourceSets(Project project) {
    SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
    SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(UMLPPluginFrontendBase.getAppGuiPath(project).toFile());
  }

  void addProjectDependencies(Project project, boolean local) {
    // JSweet
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
    }
    else {
      project.getDependencies().add("implementation", "umlp:umlp-jsweet:" + getStringProjectProperty(project, "version", ""));
    }

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    // TODO: Use constant for jsweet-core
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");
  }



  private static final String JSWEET_JAVA_HWC_PATH_PROPERTY = "jsweetjavaHWCPath";
  private static final String JSWEET_JAVA_HWC_PATH_DEFAULT = "src/main/java";
  private static final String JSWEET_TS_DOMAIN_HWC_PATH_PROPERTY = "jsweetTSDomainHWCPath";
  private static final String JSWEET_TS_DOMAIN_HWC_PATH_DEFAULT = "src/main/ts/domain";
  private static final String JSWEET_TS_GUI_HWC_PATH_PROPERTY = "jsweetTSGuiHWCPath";
  private static final String JSWEET_TS_GUI_HWC_PATH_DEFAULT = "src/main/ts/gui";
  private static final String JSWEET_BUILD_DIR_PROPERTY = "jsweetBuildDirBase";
  private static final String JSWEET_BUILD_DIR_DEFAULT = "jsweet";
  private static final String JSWEET_DOMAIN_BUILD_DIR_DEFAULT = "domainBuild";
  private static final String JSWEET_GUI_BUILD_DIR_DEFAULT = "guiBuild";
  private static final String JSWEET_WORKING_DIR_DEFAULT = "workingDir";
  private static final String JSWEET_DOMAIN_OUT_DIR_PROPERTY = "jsweetDomainOutDir";
  private static final String JSWEET_DOMAIN_OUT_DIR_DEFAULT = "umlp_frontend/intermediate/appdomain";
  private static final String JSWEET_GUI_OUT_DIR_PROPERTY = "jsweetGuiOutDir";
  private static final String JSWEET_GUI_OUT_DIR_DEFAULT = "umlp_frontend/intermediate/appguiJ2TS";
  private static final String JSWEET_GUI_ADDITIONALS_OUT_DIR_PROPERTY = "jsweetGuiAdditionalsOutDir";
  private static final String JSWEET_GUI_ADDITIONALS_OUT_DIR_DEFAULT = "umlp_frontend/intermediate/appguiJ2TSconfig";
  private static final String JSWEET_DOMAIN_CONFIG_HWC_DIR_PROPERTY = "jsweetDomainConfigHWCDir";
  private static final String JSWEET_DOMAIN_CONFIG_HWC_DIR_DEFAULT = "jsweet/domain";
  private static final String JSWEET_GUI_CONFIG_HWC_DIR_PROPERTY = "jsweetGuiConfigHWCDir";
  private static final String JSWEET_GUI_CONFIG_HWC_DIR_DEFAULT = "jsweet/gui";
  private static final String JSWEET_DOMAIN_CONFIG_OUT_DIR_PROPERTY = "jsweetDomainConfigOutDir";
  private static final String JSWEET_DOMAIN_CONFIG_OUT_DIR_DEFAULT = "configGen/domain";
  private static final String JSWEET_GUI_CONFIG_OUT_DIR_PROPERTY = "jsweetGuiConfigOutDir";
  private static final String JSWEET_GUI_CONFIG_OUT_DIR_DEFAULT = "configGen/gui";

  private Path jsweetBuildDirBase;
  private Path jsweetBuildDomainDir;
  private Path jsweetBuildGUIDir;
  private Path jsweetJavaHWCDir;
  private Path jsweetTSDomainHWCDir;
  private Path jsweetTSGUIHWCDir;
  private Path jsweetWorkingDir;
  private Path jsweetDomainOutDir;;
  private Path jsweetGUIOutDir;
  private Path jsweetGUIAdditionalsOutDir;
  private Path jsweetDomainConfigHWCDir;
  private Path jsweetGUIConfigHWCDir;
  private Path jsweetDomainConfigOutDir;
  private Path jsweetGuiConfigOutDir;

  private void setDirectories(Project project) {
    // JSweet Build / Working Dir
    String jsweetBuildDirBaseProperty = getStringProjectPropertyStrip(project,
        JSWEET_BUILD_DIR_PROPERTY, JSWEET_BUILD_DIR_DEFAULT);
    if (Path.of(jsweetBuildDirBaseProperty).isAbsolute()) {
      this.jsweetBuildDirBase = Path.of(jsweetBuildDirBaseProperty);
    } else {
      this.jsweetBuildDirBase = Path.of(project.getBuildDir().getAbsolutePath(), jsweetBuildDirBaseProperty);
    }
    this.jsweetBuildDomainDir = this.jsweetBuildDirBase.resolve(JSWEET_DOMAIN_BUILD_DIR_DEFAULT);
    this.jsweetBuildGUIDir = this.jsweetBuildDirBase.resolve(JSWEET_GUI_BUILD_DIR_DEFAULT);
    this.jsweetWorkingDir = this.jsweetBuildDirBase.resolve(JSWEET_WORKING_DIR_DEFAULT);

    // JSweet Config Gen
    String jsweetDomainConfigOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_DOMAIN_CONFIG_OUT_DIR_PROPERTY, JSWEET_DOMAIN_CONFIG_OUT_DIR_DEFAULT);
    if (Path.of(jsweetDomainConfigOutDirProperty).isAbsolute()) {
      this.jsweetDomainConfigOutDir = Path.of(jsweetDomainConfigOutDirProperty);
    } else {
      this.jsweetDomainConfigOutDir = this.jsweetBuildDirBase.resolve(jsweetDomainConfigOutDirProperty);
    }
    String jsweetGuiConfigOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_CONFIG_OUT_DIR_PROPERTY, JSWEET_GUI_CONFIG_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGuiConfigOutDirProperty).isAbsolute()) {
      this.jsweetGuiConfigOutDir = Path.of(jsweetGuiConfigOutDirProperty);
    } else {
      this.jsweetGuiConfigOutDir = this.jsweetBuildDirBase.resolve(jsweetGuiConfigOutDirProperty);
    }

    // Java HWC
    String jsweetJavaHWCPathProperty = getStringProjectPropertyStrip(project,
        JSWEET_JAVA_HWC_PATH_PROPERTY, JSWEET_JAVA_HWC_PATH_DEFAULT);
    if (Path.of(jsweetJavaHWCPathProperty).isAbsolute()) {
      this.jsweetJavaHWCDir = Path.of(jsweetJavaHWCPathProperty);
    } else {
      this.jsweetJavaHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetJavaHWCPathProperty);
    }

    // TS HWC
    String jsweetTSDomainHWCPathProperty = getStringProjectPropertyStrip(project,
        JSWEET_TS_DOMAIN_HWC_PATH_PROPERTY, JSWEET_TS_DOMAIN_HWC_PATH_DEFAULT);
    if (Path.of(jsweetTSDomainHWCPathProperty).isAbsolute()) {
      this.jsweetTSDomainHWCDir = Path.of(jsweetTSDomainHWCPathProperty);
    } else {
      this.jsweetTSDomainHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetTSDomainHWCPathProperty);
    }
    String jsweetTSGUIHWCPathProperty = getStringProjectPropertyStrip(project,
        JSWEET_TS_GUI_HWC_PATH_PROPERTY, JSWEET_TS_GUI_HWC_PATH_DEFAULT);
    if (Path.of(jsweetTSGUIHWCPathProperty).isAbsolute()) {
      this.jsweetTSGUIHWCDir = Path.of(jsweetTSGUIHWCPathProperty);
    } else {
      this.jsweetTSGUIHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetTSGUIHWCPathProperty);
    }

    // JSweet Out Dir
    String jsweetDomainOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_DOMAIN_OUT_DIR_PROPERTY, JSWEET_DOMAIN_OUT_DIR_DEFAULT);
    if (Path.of(jsweetDomainOutDirProperty).isAbsolute()) {
      this.jsweetDomainOutDir = Path.of(jsweetDomainOutDirProperty);
    } else {
      this.jsweetDomainOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetDomainOutDirProperty);
    }
    String jsweetGUIOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_OUT_DIR_PROPERTY, JSWEET_GUI_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGUIOutDirProperty).isAbsolute()) {
      this.jsweetGUIOutDir = Path.of(jsweetGUIOutDirProperty);
    } else {
      this.jsweetGUIOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetGUIOutDirProperty);
    }
    String jsweetGUIAdditionalsOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_ADDITIONALS_OUT_DIR_PROPERTY, JSWEET_GUI_ADDITIONALS_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGUIAdditionalsOutDirProperty).isAbsolute()) {
      this.jsweetGUIAdditionalsOutDir = Path.of(jsweetGUIAdditionalsOutDirProperty);
    } else {
      this.jsweetGUIAdditionalsOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetGUIAdditionalsOutDirProperty);
    }

    // JSweet Config HWC
    String jsweetDomainConfigHWCDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_DOMAIN_CONFIG_HWC_DIR_PROPERTY, JSWEET_DOMAIN_CONFIG_HWC_DIR_DEFAULT);
    if (Path.of(jsweetDomainConfigHWCDirProperty).isAbsolute()) {
      this.jsweetDomainConfigHWCDir = Path.of(jsweetDomainConfigHWCDirProperty);
    } else {
      this.jsweetDomainConfigHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetDomainConfigHWCDirProperty);
    }
    String jsweetGuiConfigHWCDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_CONFIG_HWC_DIR_PROPERTY, JSWEET_GUI_CONFIG_HWC_DIR_DEFAULT);
    if (Path.of(jsweetGuiConfigHWCDirProperty).isAbsolute()) {
      this.jsweetGUIConfigHWCDir = Path.of(jsweetGuiConfigHWCDirProperty);
    } else {
      this.jsweetGUIConfigHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetGuiConfigHWCDirProperty);
    }
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
