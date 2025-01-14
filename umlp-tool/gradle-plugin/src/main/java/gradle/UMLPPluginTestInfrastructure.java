package gradle;

import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPTestInfrastructureTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import static gradle.UMLPPluginFrontendBase.GUI_LIB_NAME;
import static gradle.UMLPPluginFrontendBase.getBooleanProjectProperty;

/**
 * Plugin which handles test infrastructure generation.
 *
 * <p><strong>Usage</strong>
 * Will be automatically applied if {@link  UMLPPluginBackend} is used.
 * Alternatively:
 * <pre>{@code
 * //build.gradle
 *
 * plugins {
 *   //...
 *   id 'de.rwth.se.umlp-tool-testing'
 * }
 * }</pre>
 */
public abstract class UMLPPluginTestInfrastructure implements Plugin<Project> {

  protected static final String UMLP_TEST_INFRASTRUCTURE_TASK_NAME = "generateTestInfrastructure";
  protected static final String UMLP_GROUP_NAME = "umlp";
  protected static final String TEST_IMPLEMENTATION = "testImplementation";
  protected static final String COMPILE_ONLY = "compileOnly";

  @Override
  public void apply(Project project) {
    PluginManager pluginManager = project.getPluginManager();

    pluginManager.apply(UMLPPluginBase.class);
    pluginManager.apply("java");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    var umlpTestInfrastructureTask = createUmlpTestInfrastructureTask(project, local);
    //generate test infrastructure must run before compiling the tests
    project.getTasks()
        .withType(AbstractCompile.class)
        .configureEach(testTask -> testTask.dependsOn(umlpTestInfrastructureTask));

    //add generated source files to source set
    JavaPluginConvention javaConvention = project.getConvention()
        .getPlugin(JavaPluginConvention.class);
    SourceSet mainSourceSet = javaConvention.getSourceSets()
        .getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    mainSourceSet.getJava().srcDir(umlpTestInfrastructureTask.get().getOutputDir());

    //dependencies
    DependencyHandler dependencies = project.getDependencies();

    // Adding the dependencies to compileOnly and testImplementation is a tradeoff to using
    // the java-test-fixtures plugin, which would force the developers to TOP in the src/textFixtures/java
    // dir, which is very gradle specific and weird.

    //umlp-test-rte
    Project umlpTestRte = project.findProject(":umlp-test-rte");
    if(umlpTestRte != null){
      dependencies.add(COMPILE_ONLY, umlpTestRte);
      dependencies.add(TEST_IMPLEMENTATION, umlpTestRte);
    }

    //playwright
    if(project.hasProperty("playwright_version")) {
      dependencies.add(COMPILE_ONLY,
          "com.microsoft.playwright:playwright:" + project.property("playwright_version"));
      dependencies.add(TEST_IMPLEMENTATION,
          "com.microsoft.playwright:playwright:" + project.property("playwright_version"));
    }

    //jsweet - annotations like erased
    if(project.hasProperty("jsweet_version")) {
      dependencies.add(COMPILE_ONLY, "org.jsweet:jsweet-core:" + project.property("jsweet_version"));
      dependencies.add(TEST_IMPLEMENTATION, "org.jsweet:jsweet-core:" + project.property("jsweet_version"));
    }
  }

  public static TaskProvider<UMLPTestInfrastructureTask> createUmlpTestInfrastructureTask(Project project, boolean local) {
    TaskProvider<UMLPTestInfrastructureTask> testInfrastructureTask = project.getTasks()
        .register(UMLP_TEST_INFRASTRUCTURE_TASK_NAME, UMLPTestInfrastructureTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);

          UMLPBackendTask backendTask = (UMLPBackendTask) project.getTasks().findByName(
              UMLPPluginBackend.UMLP_BACKEND_TASK_NAME
          );

          String buildDirPath = project.getBuildDir().getAbsolutePath();
          String projectDirPath = project.getProjectDir().getAbsolutePath();

          if (backendTask != null) {
            task.dependsOn(UMLPPluginBackend.UMLP_BACKEND_TASK_NAME);

            task.getInput().from(backendTask.getInput());
            task.getTmplDir().convention(backendTask.getTmplDir());
            task.getModelDir().from(backendTask.getModelDir());
          }else {
            setIfPathExists(task::setInput, Path.of(projectDirPath, "src", "models"));

            setIfPathExists(path -> task.getTmplDir().set(path),
                Path.of(projectDirPath, "src", "templates"));
          }
          setIfPathExists(path -> task.getHandWrittenCodeDir().from(path),
              Path.of(projectDirPath, "src", "main", "java"));
          task.getOutputDir().set(Path.of(buildDirPath, "umlp", "backend_test").toFile());
          task.getSymbolOutDir().set(Path.of(buildDirPath, "umlp", "backend_test_symbols").toFile());
        });


    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        testInfrastructureTask.get().dependsOn(createGuiSymbols, createCDSymbols);
        testInfrastructureTask.get().getSymbolPath().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      testInfrastructureTask.get().getSymbolPath().from(
          project.provider(() -> guiLib.getFiles())
      );
    }

    return testInfrastructureTask;
  }

  private static void setIfPathExists(Consumer<File> setter, Path path) {
    File file = path.toFile();
    if (file.exists()) {
      setter.accept(file);
    }
  }
}
