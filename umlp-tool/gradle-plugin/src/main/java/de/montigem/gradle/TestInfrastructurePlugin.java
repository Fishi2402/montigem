package de.montigem.gradle;

import de.montigem.gradle.specific.UMLP2JavaTask;
import de.montigem.gradle.specific.UMLP2TestInfrastructureTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.FrontendBasePlugin.getBooleanProjectProperty;
import static de.montigem.gradle.FrontendBasePlugin.getStringProjectProperty;

/**
 * Plugin which handles test infrastructure generation.
 *
 * <p><strong>Usage</strong>
 * Will be automatically applied if {@link  BackendPlugin} is used.
 * Alternatively:
 * <pre>{@code
 * //build.gradle
 *
 * plugins {
 *   //...
 *   id 'de.rwth.se.montigem-testing'
 * }
 * }</pre>
 */
public abstract class TestInfrastructurePlugin implements Plugin<Project> {

  protected static final String UMLP_TEST_INFRASTRUCTURE_TASK_NAME = "generateTestInfrastructure";
  protected static final String UMLP_GROUP_NAME = "umlp";
  protected static final String TEST_IMPLEMENTATION = "testImplementation";
  protected static final String COMPILE_ONLY = "compileOnly";

  @Override
  public void apply(Project project) {
    PluginManager pluginManager = project.getPluginManager();

    pluginManager.apply(MGBasePlugin.class);
    pluginManager.apply("java");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    var umlpTestInfrastructureTask = createUmlpTestInfrastructureTask(project, local);
    var mainSourceSet = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    var testSourceSet = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME);
    //generate test infrastructure must run before compiling the tests
    project.getTasks()
        .withType(AbstractCompile.class)
        .matching(t -> t.getName().equals(testSourceSet.getCompileJavaTaskName())
                                          || t.getName().equals(mainSourceSet.getCompileJavaTaskName() // TODO: Remove this dependency and introduce test fixtures?
                                         ))
        .configureEach(testTask -> testTask.dependsOn(umlpTestInfrastructureTask));

    // TODO: Introduce test-fixtures and generate the "test-infra" into the test source set instead of the main source set
    mainSourceSet.getJava().srcDir(umlpTestInfrastructureTask.get().getOutputDir());

    //TODO: add generated source files to "test" source set
//    testSourceSet.getJava().srcDir(umlpTestInfrastructureTask.get().getOutputDir());

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

  public static TaskProvider<UMLP2TestInfrastructureTask> createUmlpTestInfrastructureTask(Project project, boolean local) {
    TaskProvider<UMLP2TestInfrastructureTask> testInfrastructureTask = project.getTasks()
        .register(UMLP_TEST_INFRASTRUCTURE_TASK_NAME, UMLP2TestInfrastructureTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);

          UMLP2JavaTask backendTask = (UMLP2JavaTask) project.getTasks().findByName(
              BackendPlugin.UMLP2JAVA_TASK
          );

          String buildDirPath = project.getBuildDir().getAbsolutePath();
          String projectDirPath = project.getProjectDir().getAbsolutePath();

          if (backendTask != null) {
            task.dependsOn(BackendPlugin.UMLP2JAVA_TASK);

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
      Project rteClient = project.project(":umlp-rte:frontend");
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, rteClient);
      testInfrastructureTask.configure(task -> {
        task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
      });
    }
    else {
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
          "umlp:frontend:" + getStringProjectProperty(project, "mc_version", "")
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
