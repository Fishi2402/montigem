/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.montigem.gradle.specific.CreateSymlinkTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.Java2TSTask;
import de.montigem.gradle.specific.Model2JsweetConfigTask;
import de.montigem.gradle.specific.SetupFrontendTask;
import de.montigem.gradle.specific.UMLP2JsweetObserverTask;
import de.montigem.gradle.specific.UMLP2SymTask;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.VerificationType;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.montigem.gradle.FrontendBasePlugin.CREATE_SYMLINK_TASK;
import static de.montigem.gradle.FrontendBasePlugin.GUI2JAVA_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SETUP_FRONTEND_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SYNC_GUI2JAVA_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SYNC_UMLP2TS_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP2SYM_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.FrontendBasePlugin.getStringProjectPropertyStrip;
import static de.montigem.gradle.FrontendBasePlugin.mcVersion;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG;

/**
 * The JSweet-specific gradle setup
 */
public abstract class FrontendWithJSweetPlugin implements Plugin<Project> {

  static final String UMLP_JAVA2TS_TASK = "umlpJava2ts";
  static final String GUI_JAVA2TS_TASK = "guiJava2ts";
  static final String UMLP2JSWEET_TASK = "umlp2jsweet";
  static final String SYNC_UMLP2JSWEET_TASK = "sync_umlp2jsweet";
  static final String GUI2JSWEET_TASK = "gui2jsweet";
  static final String SYNC_GUI2JSWEET_TASK = "sync_gui2jsweet";
  static final String UMLP2JSWEET_OBS_TASK = "umlp2jsweetObs";

  private static final String DOMAIN_MODEL_CONFIG = "domainModel";
  private static final String DOMAIN_SOURCES_CONFIG = "domainSources";

  private static final String JSWEET_CONFIG = "jsweetConfig";

  private static List<String> defaultExcludedSources = List.of(
      "glob:**/shellapi/*ShellAPI.java",
      "glob:**/Abstract*E2ETest*.java",
      "glob:**/test_infrastructure/*.java",

      "glob:**/*ManagerImpl.java",
      "glob:**/*TestSetup.java",
      "glob:**/*DummyProvider.java",
      "glob:**/*ServerApplication.java",
      "glob:**/*DAO.java",
      "glob:**/*DAOFactory.java",
      "glob:**/*DAONeo4J.java",
      "glob:**/*DAOInterface.java",
      "glob:**/*DBObserver.java"
  );

  @Override
  public void apply(Project project) {
    setDirectories(project);

    project.getPluginManager().apply(FrontendBasePlugin.class);

    boolean local = FrontendBasePlugin.getBooleanProjectProperty(project, "useLocalDependencies");

    String domainModel = getStringProjectPropertyStrip(project, DOMAIN_MODEL_CONFIG, "");
    if (domainModel.isEmpty()) {
      Directory models = project.getLayout().getProjectDirectory().dir("../models");
      // default .umlp file(s)
      Set<File> umlp;
      // default name(s) of .umlp file(s)
      List<String> domainNames = new ArrayList<>();

      if (models.getAsFile().exists()) {
        umlp = models.getAsFileTree().matching(spec -> spec.include("**/*.umlp")).getFiles();
        if (!umlp.isEmpty()) {
          domainNames = umlp.stream()
              .map(File::getName)
              .map(name -> {
                int dotIndex = name.lastIndexOf('.');
                return (dotIndex != -1) ? name.substring(0, dotIndex) : name;
              })
              .collect(Collectors.toList());
        }
      }
      else {
        umlp = new HashSet<>();
      }
      if (domainNames.size() == 1) {
        domainModel = domainNames.get(0);
      }
      else {
        Log.warn("0x0B001 Could not identify the domain model. Please set the `domainModel` property in your `gradle.properties` file.");
      }
    }

    setupJSweetClasspath(project, local);

    TaskProvider<SetupFrontendTask> setupFrontendTask = project.getTasks().named(SETUP_FRONTEND_TASK, SetupFrontendTask.class);

    TaskProvider<GUI2JavaTask> gui2java = project.getTasks().named(GUI2JAVA_TASK, GUI2JavaTask.class);
    TaskProvider<Sync> syncUMLP2TSTask = project.getTasks().named(SYNC_UMLP2TS_TASK, Sync.class);
    TaskProvider<Sync> syncGUI2JavaTask = project.getTasks().named(SYNC_GUI2JAVA_TASK, Sync.class);

    TaskProvider<CreateSymlinkTask> createSymlinkTask =
        project.getTasks().named(CREATE_SYMLINK_TASK, CreateSymlinkTask.class);

    TaskProvider<UMLP2SymTask> umlp2sym = project.getTasks().named(UMLP2SYM_TASK, UMLP2SymTask.class);

    TaskProvider<Java2TSTask> guiJava2TS = createGUIJava2TSTask(project);
    TaskProvider<UMLP2JsweetObserverTask> umlp2jsweetObs = createUMLP2JSweetObsTask(project);

    TaskProvider<Model2JsweetConfigTask> umlp2jsweet = createUMLP2JSweetTask(project, domainModel);
    TaskProvider<Sync> syncUMLP2JSweet = createSyncUMLP2JSweetTask(project);

    TaskProvider<Model2JsweetConfigTask> gui2jsweet = createGUI2JSweetTask(project, domainModel);
    TaskProvider<Sync> syncGUI2JSweet = createSyncGUI2JSweetTask(project);
    TaskProvider<Java2TSTask> umlpJava2TS = createUMLPJava2TSTask(project);

    this.addSourceSets(project);
    this.addProjectDependencies(project, local);

    // dependencies
    gui2java.configure(task -> {
      setIfPathExists(x -> task.getHwcDir().from(x), this.jsweetJavaHWCDir);
      setIfPathExists(x -> task.getHwcDir().from(x), this.jsweetTSHWCDir);
      task.getImportsDir().from(umlpJava2TS.get().getOutputDir());
      task.dependsOn(umlpJava2TS);

      task.getGuiGenTSFuncionality().set(false);
      task.getGuiGenJsweet().set(true);
    });
    syncUMLP2JSweet.configure(task -> {
      task.from(umlp2jsweet.get().getJsweetConfigOut().getAsFile());
      task.dependsOn(umlp2jsweet);
      task.into(this.jsweetBuildDomainDir);
      if (umlp2jsweet.get().getHwcDir().isPresent()) {
        task.from(umlp2jsweet.get().getHwcDir());
      }
    });
    syncGUI2JSweet.configure(task -> {
      task.from(gui2jsweet.get().getJsweetConfigOut().getAsFile());
      task.dependsOn(gui2jsweet);
      task.into(this.jsweetBuildGUIDir);
      if (gui2jsweet.get().getHwcDir().isPresent()) {
        task.from(gui2jsweet.get().getHwcDir());
      }
    });
    umlpJava2TS.configure(task -> {
      task.getJsweetDir().set(syncUMLP2JSweet.get().getDestinationDir());
      task.dependsOn(syncUMLP2JSweet.get());
      task.dependsOn(umlp2sym);
      if (local) {
        task.dependsOn(project.findProject(":umlp-rte").getTasks().getByName("jar"));
        task.dependsOn(project.findProject(":umlp-test-rte").getTasks().getByName("jar"));
        task.dependsOn(project.findProject(":umlp-rte:umlp-jsweet").getTasks().getByName("jar"));
      }
    });

    project.getTasks().getByName("compileJava").dependsOn(gui2java.get());

    guiJava2TS.configure(task -> {
      task.getJsweetDir().set(syncGUI2JSweet.get().getDestinationDir());
      task.dependsOn(gui2java);
      task.dependsOn(syncGUI2JSweet);
    });

    syncUMLP2TSTask.configure(task -> {
      task.from(umlpJava2TS.get().getOutputDir());
      task.dependsOn(umlpJava2TS.get(), setupFrontendTask.get());
    });

    syncGUI2JavaTask.configure(task -> {
      task.from(gui2java.get().getOutputDir());
      task.from(guiJava2TS.get().getOutputDir());
      task.from(umlp2jsweetObs.get().getOutputDir());
      task.dependsOn(guiJava2TS, setupFrontendTask);
    });

    createSymlinkTask.configure(task -> {
      if (task.getSymlinkMap().isPresent()) {
        File hwcSrcLink = project.getBuildDir().toPath().resolve("app/hwcsrc").toFile();
        task.getSymlinkMap().put(hwcSrcLink, this.jsweetTSHWCDir.toFile());

        File angularTemplateResolveLink = project.getBuildDir().toPath().resolve("build/umlp_frontend").toFile();
        File angularTemplateResolveTarget = project.getBuildDir().toPath().resolve("umlp_frontend").toFile();
        task.getSymlinkMap().put(angularTemplateResolveLink, angularTemplateResolveTarget);
      }
    });

/*
    syncTSGuiHWC.configure(umlpTask -> {
      umlpTask.dependsOn(syncGuiTask, setupFrontendTask);
    });

    syncTSDomainHWC.configure(umlpTask -> {
      umlpTask.dependsOn(syncDomainTask, setupFrontendTask);
    });
*/

    project.getTasks().named("generate").configure(generateTask -> {
      generateTask.dependsOn(guiJava2TS, syncUMLP2JSweet/*, syncTSGuiHWC, syncTSDomainHWC*/);
    });
  }

  protected TaskProvider<Java2TSTask> createUMLPJava2TSTask(Project project) {
    return project.getTasks().register(UMLP_JAVA2TS_TASK, Java2TSTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getJsweetDir().set(jsweetBuildDomainDir.toFile());
      task.getJsweetWorkingDir().set(jsweetWorkingDir.resolve("umlpJava2ts").toFile());
      task.getCandiesJsOutDir().set(task.getJsweetWorkingDir().dir("candies"));
      task.getOutputDir().set(jsweetDomainOutDir.toFile());

      // // add domain-sources to the to-be-transpiled
      task.getInput().from(project.getConfigurations().getByName(DOMAIN_SOURCES_CONFIG));
      task.getJSweetClassPath().from(project.getConfigurations().getByName(JSWEET_CONFIG));

      // TODO: Fix ShellAPI imports
      // Required due to "imports cannot use * wildcards when using modules"
      for (String s : defaultExcludedSources) {
        task.getExcludeSource().add(s); // via @Erased
      }
    });
  }

  protected TaskProvider<Model2JsweetConfigTask> createUMLP2JSweetTask(Project project,
      String domainModel) {
    return project.getTasks().register(UMLP2JSWEET_TASK, Model2JsweetConfigTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getDomainModel().set(domainModel);
      task.getGenerateForDomain().set(true);
      task.getJsweetConfigOut().set(jsweetDomainConfigOutDir.toFile());
      setIfPathExists(x -> task.getHwcDir().set(x), jsweetDomainConfigHWCDir);
    });
  }

  protected TaskProvider<Model2JsweetConfigTask> createGUI2JSweetTask(Project project,
      String domainModel) {
    return project.getTasks().register(GUI2JSWEET_TASK, Model2JsweetConfigTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getDomainModel().set(domainModel);
      umlpTask.getGenerateForGui().set(true);
      umlpTask.getJsweetConfigOut().set(jsweetGuiConfigOutDir.toFile());
      setIfPathExists(x -> umlpTask.getHwcDir().set(x), jsweetGUIConfigHWCDir);
    });
  }

  protected TaskProvider<Java2TSTask> createGUIJava2TSTask(Project project) {
    return project.getTasks().register(GUI_JAVA2TS_TASK, Java2TSTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getJsweetDir().set(jsweetBuildGUIDir.toFile());
      task.getJsweetWorkingDir().set(jsweetWorkingDir.resolve(task.getName()).toFile());
      task.getCandiesJsOutDir().set(task.getJsweetWorkingDir().dir("candies"));
      task.getOutputDir().set(jsweetGUIOutDir.toFile());

      // set jsweet-runtime classpath
      task.getJSweetClassPath().from(project.getConfigurations().getByName(JSWEET_CONFIG));
      // add src/main/java as an input
      task.getInput().from(project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava());
      // Add gui2java-outputdir as an input
      //            umlpTask.getInput().from(frontendGuiTask.get().getOutputDir());

      // TODO: Remove the following at some point
      // required with getExcludeTranspile - otherwise "import { Project } from '@src/dtcockpit/Project';" is missing in GUI ts files
      // Add the domain-sources as a java-source
      task.getInput().from(project.getConfigurations().getByName(DOMAIN_SOURCES_CONFIG));
      // but exclude them from being transpiled (thus, only available as source files)
      task.getExcludeTranspileFiles().from(project.getConfigurations().getByName(DOMAIN_SOURCES_CONFIG));
      // TODO: END OF REMOVAL

      // TODO: Fix ShellAPI imports
      // Required due to "imports cannot use * wildcards when using modules"
      for (String s : defaultExcludedSources) {
        task.getExcludeSource().add(s); // via @Erased
      }
    });
  }

  private TaskProvider<UMLP2JsweetObserverTask> createUMLP2JSweetObsTask(Project project) {
    TaskProvider<UMLP2JsweetObserverTask> umlp2jsweetObs = project.getTasks().register(UMLP2JSWEET_OBS_TASK, UMLP2JsweetObserverTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(FrontendBasePlugin.getModels(project));
      task.getOutputDir().set(jsweetGUIAdditionalsOutDir.toFile());

      // Always create symbols & tag files
      // task.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "umlp2sym").toFile());

      setIfPathExists(x -> task.getHwcDir().from(x), this.jsweetTSHWCDir);

      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));

      task.getModelDir().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_MODELS_CONFIG));
    });
    return umlp2jsweetObs;
  }

  private TaskProvider<Sync> createSyncUMLP2JSweetTask(Project project) {
    return project.getTasks().register(SYNC_UMLP2JSWEET_TASK, Sync.class, sync -> {
      sync.setGroup("sync");
      sync.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
    });
  }

  private TaskProvider<Sync> createSyncGUI2JSweetTask(Project project) {
    return project.getTasks().register(SYNC_GUI2JSWEET_TASK, Sync.class, sync -> {
      sync.setGroup("sync");
      sync.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
    });
  }


/*  private TaskProvider<Sync> createSyncTSDomainHWC(Project project, TaskProvider<SetupFrontendTask> setupFrontendTask) {
    return project.getTasks().register(SYNC_PREFIX + "TSDomainHWC", Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.from(jsweetTSDomainHWCDir);
      syncTask.into(Path.of(setupFrontendTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appdomain").toFile());*/
  // syncTask.exclude("**/*.java");
/*      syncTask.getPreserve().include("**");
    });
  }

  private TaskProvider<Sync> createSyncTSGUIHWC(Project project, TaskProvider<SetupFrontendTask> setupFrontendTask) {
    return project.getTasks().register(SYNC_PREFIX + "TSGuiHWC", Sync.class, syncTask -> {
      syncTask.setGroup("sync");
      syncTask.from(jsweetTSGUIHWCDir);
      syncTask.into(Path.of(setupFrontendTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appgui").toFile());*/
  //syncTask.exclude("**/*.java");
/*      syncTask.getPreserve().include("**");
    });
  }*/

  void setupJSweetClasspath(Project project, boolean local) {
    project.getConfigurations().register(DOMAIN_SOURCES_CONFIG, config -> {
      config.setTransitive(false);
      config.attributes(at -> {
        at.attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
            project.getObjects()
                .named(VerificationType.class, VerificationType.MAIN_SOURCES));
        at.attribute(Bundling.BUNDLING_ATTRIBUTE,
            project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      });
    });

    // Configuration containing the compile/transpile dependencies (for running jsweet)
    project.getConfigurations().register(JSWEET_CONFIG, config -> {
      config.extendsFrom(project.getConfigurations().getByName("compileClasspath"));
    });

    if (local) {
      project.getDependencies().add(JSWEET_CONFIG,
          project.getDependencies().project(Map.of("path", ":umlp-rte:umlp-jsweet")));
    }
    else {
      project.getDependencies().add(JSWEET_CONFIG, "umlp:umlp-jsweet:" + mcVersion(project));
    }
  }

  void addSourceSets(Project project) {
    SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
    SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(FrontendBasePlugin.getIntermediateGUI2Java(project));
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
    Action<MavenRepositoryContentDescriptor> filterContent = it -> it.includeModuleByRegex("org[.]jsweet.*", "(?!jsweet-core).*");
    snapshotRepo.mavenContent(filterContent);
    releaseRepo.mavenContent(filterContent);

    project.getDependencies().add("implementation", "org.jsweet:jsweet-core:6.3.0");
    Dependency dep = project.getDependencies().create("de.rwth.se.umlp.jsweet:jsweet-transpiler:3.2.0-UMLP-SNAPSHOT", new Closure<Object>(this, this) {
      @Override
      public Object call(Object arg) {
        var emd = (ExternalModuleDependency) arg;
        emd.exclude(Map.of(
            "group", "com.kohlschutter.jdk",
            "module", "standalone-home"
        ));
        return arg;
      }

      @Override
      public int getMaximumNumberOfParameters() {
        return 1;
      }
    });
    project.getDependencies().add("implementation", dep);
    project.getDependencies().add("implementation", "com.kohlschutter.jdk.compiler:standalone-jdk11:1.0.0");
    if (local) {
      project.getDependencies().add("implementation", project.project(":umlp-rte:umlp-jsweet"));
    }
    else {
      project.getDependencies().add("implementation", "umlp:umlp-jsweet:" + mcVersion(project));
    }

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    // TODO: Use constant for jsweet-core
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");
  }


  private static final String JSWEET_JAVA_HWC_PATH_PROPERTY = "jsweetjavaHWCPath";
  private static final String JSWEET_JAVA_HWC_PATH_DEFAULT = "src/main/java";
  private static final String JSWEET_TS_HWC_PATH_PROPERTY = "jsweetTSHWCPath";
  private static final String JSWEET_TS_HWC_PATH_DEFAULT = "src/main/gui";
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
  private Path jsweetTSHWCDir;
  private Path jsweetWorkingDir;
  private Path jsweetDomainOutDir;
  ;
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
    }
    else {
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
    }
    else {
      this.jsweetDomainConfigOutDir = this.jsweetBuildDirBase.resolve(jsweetDomainConfigOutDirProperty);
    }
    String jsweetGuiConfigOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_CONFIG_OUT_DIR_PROPERTY, JSWEET_GUI_CONFIG_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGuiConfigOutDirProperty).isAbsolute()) {
      this.jsweetGuiConfigOutDir = Path.of(jsweetGuiConfigOutDirProperty);
    }
    else {
      this.jsweetGuiConfigOutDir = this.jsweetBuildDirBase.resolve(jsweetGuiConfigOutDirProperty);
    }

    // Java HWC
    String jsweetJavaHWCPathProperty = getStringProjectPropertyStrip(project,
        JSWEET_JAVA_HWC_PATH_PROPERTY, JSWEET_JAVA_HWC_PATH_DEFAULT);
    if (Path.of(jsweetJavaHWCPathProperty).isAbsolute()) {
      this.jsweetJavaHWCDir = Path.of(jsweetJavaHWCPathProperty);
    }
    else {
      this.jsweetJavaHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetJavaHWCPathProperty);
    }

    // TS HWC
    String jsweetTSHWCPathProperty = getStringProjectPropertyStrip(project,
        JSWEET_TS_HWC_PATH_PROPERTY, JSWEET_TS_HWC_PATH_DEFAULT);
    if (Path.of(jsweetTSHWCPathProperty).isAbsolute()) {
      this.jsweetTSHWCDir = Path.of(jsweetTSHWCPathProperty);
    }
    else {
      this.jsweetTSHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetTSHWCPathProperty);
    }

    // JSweet Out Dir
    String jsweetDomainOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_DOMAIN_OUT_DIR_PROPERTY, JSWEET_DOMAIN_OUT_DIR_DEFAULT);
    if (Path.of(jsweetDomainOutDirProperty).isAbsolute()) {
      this.jsweetDomainOutDir = Path.of(jsweetDomainOutDirProperty);
    }
    else {
      this.jsweetDomainOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetDomainOutDirProperty);
    }
    String jsweetGUIOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_OUT_DIR_PROPERTY, JSWEET_GUI_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGUIOutDirProperty).isAbsolute()) {
      this.jsweetGUIOutDir = Path.of(jsweetGUIOutDirProperty);
    }
    else {
      this.jsweetGUIOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetGUIOutDirProperty);
    }
    String jsweetGUIAdditionalsOutDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_ADDITIONALS_OUT_DIR_PROPERTY, JSWEET_GUI_ADDITIONALS_OUT_DIR_DEFAULT);
    if (Path.of(jsweetGUIAdditionalsOutDirProperty).isAbsolute()) {
      this.jsweetGUIAdditionalsOutDir = Path.of(jsweetGUIAdditionalsOutDirProperty);
    }
    else {
      this.jsweetGUIAdditionalsOutDir = Path.of(project.getBuildDir().getAbsolutePath(), jsweetGUIAdditionalsOutDirProperty);
    }

    // JSweet Config HWC
    String jsweetDomainConfigHWCDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_DOMAIN_CONFIG_HWC_DIR_PROPERTY, JSWEET_DOMAIN_CONFIG_HWC_DIR_DEFAULT);
    if (Path.of(jsweetDomainConfigHWCDirProperty).isAbsolute()) {
      this.jsweetDomainConfigHWCDir = Path.of(jsweetDomainConfigHWCDirProperty);
    }
    else {
      this.jsweetDomainConfigHWCDir = Path.of(project.getProjectDir().getAbsolutePath(), jsweetDomainConfigHWCDirProperty);
    }
    String jsweetGuiConfigHWCDirProperty = getStringProjectPropertyStrip(project,
        JSWEET_GUI_CONFIG_HWC_DIR_PROPERTY, JSWEET_GUI_CONFIG_HWC_DIR_DEFAULT);
    if (Path.of(jsweetGuiConfigHWCDirProperty).isAbsolute()) {
      this.jsweetGUIConfigHWCDir = Path.of(jsweetGuiConfigHWCDirProperty);
    }
    else {
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
