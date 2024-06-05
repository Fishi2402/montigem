/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPJava2TypeScriptAdditionalsTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymbolsTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
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
import java.util.function.Consumer;

import static gradle.UMLPPluginFrontendBase.CREATE_DOMAIN_SYMBOLS_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_DOMAIN_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_SETUP_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_GROUP_NAME;

/**
 * The JSweet-specific gradle setup
 */
public abstract class UMLPPluginFrontendWithJSweet implements Plugin<Project> {

  static final String UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME = "translateJava2TSDomain";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME = "genJava2TSConfig";
  static final String UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME = "genJava2TSAdditionals";
  static final String UMLP_FRONTEND_GUI_J2TS_TASK_NAME = "translateJava2TSGUI";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginFrontendBase.class);

    boolean local = UMLPPluginFrontendBase.getBooleanProjectProperty(project, "useLocalDependencies");
    setUpJSweetClassPath(project, local);

    TaskProvider<UMLPGuiGenTask> frontendGuiTask = project.getTasks().named(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class);
    TaskProvider<Sync> syncDomainTask = project.getTasks().named("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class);
    TaskProvider<Sync> syncGuiTask = project.getTasks().named("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class);

    TaskProvider<FrontendSetup> frontendSetupTask = project.getTasks().named(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class);
    TaskProvider<UMLPSymbolsTask> domainSymbolGenTask = project.getTasks().named(CREATE_DOMAIN_SYMBOLS_TASK_NAME, UMLPSymbolsTask.class);

    frontendGuiTask.configure(umlpTask -> {
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
      umlpTask.getGuiGenTSFuncionality().set(false);
      umlpTask.getGuiGenJsweet().set(true);
    });

    TaskProvider<UMLPJava2TypeScriptTask> frontendGUIJ2TSTask = createFrontendGUIJ2TSTask(project);
    TaskProvider<UMLPJava2TypeScriptAdditionalsTask> java2TypeScriptAdditionals = createFrontendJ2TSAdditionalsTask(project);

    TaskProvider<UMLPJava2TypeScriptConfigTask> java2TypeScriptConfigTaskTask = createFrontendJ2TSConfigTask(project);
    TaskProvider<Sync> syncJsweetConfigTask = createSyncJsweetConfigTask(project);
    TaskProvider<UMLPJava2TypeScriptTask> frontendDomainJ2TSTask = createFrontendDomainJ2TSTask(project);

    this.addSourceSets(project);
    this.addProjectDependencies(project, local);

    // dependencies
    syncJsweetConfigTask.configure(umlpTask -> {
      umlpTask.from(java2TypeScriptConfigTaskTask.get().getJsweetConfigOut().getAsFile());
      umlpTask.dependsOn(java2TypeScriptConfigTaskTask);
    });
    frontendDomainJ2TSTask.configure(umlpTask -> {
      umlpTask.dependsOn(syncJsweetConfigTask.get());
      umlpTask.dependsOn(domainSymbolGenTask);
      if (local) {
        umlpTask.dependsOn(project.findProject(":umlp-rte").getTasks().getByName("jar"));
        umlpTask.dependsOn(project.findProject(":umlp-rte:umlp-jsweet").getTasks().getByName("jar"));
      }
    });

    project.getTasks().getByName("compileJava").dependsOn(frontendGuiTask.get());

    frontendGUIJ2TSTask.configure(umlpTask -> {
      umlpTask.dependsOn(frontendGuiTask);
      umlpTask.dependsOn(syncJsweetConfigTask);
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

    project.getTasks().named("generate").configure(generateTask -> {
      generateTask.dependsOn(frontendGUIJ2TSTask, syncJsweetConfigTask);
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendDomainJ2TSTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      // // add domain-sources to the to-be-transpiled
      umlpTask.getInput().from(project.getConfigurations().getByName("domainSources"));
      umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName("jsweettranspiler"));

      // TODO: Fix ShellAPI imports
      // Required due to "imports cannot use * wildcards when using modules"
      umlpTask.getExcludeSource().add("glob:**/shellapi/*ShellAPI.java"); // via @Erased

      umlpTask.getOutputDir().set(UMLPPluginFrontendBase.getAppDomainPath(project).toFile());
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptConfigTask> createFrontendJ2TSConfigTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, UMLPJava2TypeScriptConfigTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getJsweetConfigOut().set(project.getLayout().getBuildDirectory().dir("jsweetGen/"));
      setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(project.getProjectDir().getAbsolutePath(), "jsweet"));
    });
  }

  protected TaskProvider<UMLPJava2TypeScriptTask> createFrontendGUIJ2TSTask(final Project project) {

    return project.getTasks().register(UMLP_FRONTEND_GUI_J2TS_TASK_NAME, UMLPJava2TypeScriptTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getOutputDir().set(getAppGuiJ2TSPath(project).toFile());

      // set jsweet-runtime classpath
      umlpTask.getJSweetClassPath().from(project.getConfigurations().getByName("jsweettranspiler"));
      // add src/main/java as an input
      umlpTask.getInput().from(project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava());
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

  private TaskProvider<UMLPJava2TypeScriptAdditionalsTask> createFrontendJ2TSAdditionalsTask(Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_J2TS_ADDITIONALS_TASK_NAME, UMLPJava2TypeScriptAdditionalsTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(UMLPPluginFrontendBase.getModelsPath(project));
      umlpTask.getOutputDir().set(getAppGuiJ2TSConfigPath(project).toFile());

      // Always create symbols & tag files
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "app"));
    });
  }

  private TaskProvider<Sync> createSyncJsweetConfigTask(Project project) {
    return project.getTasks().register("SYNC_" + UMLP_FRONTEND_DOMAIN_J2TS_CONFIG_TASK_NAME, Sync.class, syncTask -> {
      syncTask.into(project.getLayout().getBuildDirectory().dir("jsweet/"));
      syncTask.from(new File(project.getProjectDir(), "jsweet"));
    });
  }

  void setUpJSweetClassPath(Project project, boolean local) {
    Configuration domainSources = project.getConfigurations().create("domainSources");
    domainSources.setTransitive(false);
    domainSources.attributes(at -> {
      at.attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, project.getObjects().named(VerificationType.class, VerificationType.MAIN_SOURCES));
      at.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
    });

    // Configuration containing the compile/transpile dependencies (for running jsweet)
    Configuration jsweettranspiler = project.getConfigurations().create("jsweettranspiler");
    jsweettranspiler.extendsFrom(project.getConfigurations().getByName("compileClasspath"));

    if (local) {
      project.getDependencies().add("jsweettranspiler", project.getDependencies().project(Map.of("path", ":umlp-rte:umlp-jsweet")));
    }
    else {
      project.getDependencies().add("jsweettranspiler", project.getDependencies().create("umlp:umlp-jsweet:" + project.getVersion()));
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
      project.getDependencies().add("implementation", "umlp:umlp-jsweet:" + UMLPPluginFrontendBase.getStringProjectProperty(project, "version", ""));
    }

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    // TODO: Use constant for jsweet-core
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");
  }

  @NotNull
  private static Path getAppGuiJ2TSPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate", "appguiJ2TS");
  }

  @NotNull
  private static Path getAppGuiJ2TSConfigPath(Project project) {
    return Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate", "appguiJ2TSconfig");
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
