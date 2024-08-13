package gradle;

import gradle.specific.UMLPTestInfrastructureTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Plugin which handles test infrastructure generation.
 *
 * <p><strong>Usage</strong>
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

  @Override
  public void apply(Project project) {
    PluginManager pluginManager = project.getPluginManager();

    pluginManager.apply(UMLPPluginBase.class);
    pluginManager.apply("java");

    var umlpTestInfrastructureTask = createUmlpTestInfrastructureTask(project);
    //TODO remaining setup - see UMLPPluginBackend
  }

  protected TaskProvider<UMLPTestInfrastructureTask> createUmlpTestInfrastructureTask(Project project) {
    return project.getTasks()
        .register(UMLP_TEST_INFRASTRUCTURE_TASK_NAME, UMLPTestInfrastructureTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);

          String projectDirPath = project.getProjectDir().getAbsolutePath();
          String buildDirPath = project.getBuildDir().getAbsolutePath();

          setIfPathExists(task::setInput, Path.of(projectDirPath, "src", "models"));
          setIfPathExists(path -> task.getHandWrittenCodeDir().from(path),
                          Path.of(projectDirPath, "src", "main", "java"));
          setIfPathExists(path -> task.getTmplDir().set(path),
                          Path.of(projectDirPath, "src", "templates"));

          // Always set output dir (Gradle creates it, if not already exists)
          task.getOutputDir().set(Path.of(buildDirPath, "umlp", "backend").toFile());

          // Always create symbols
          task.getSymbolOutDir().set(Path.of(buildDirPath, "umlp", "backend_symbols").toFile());
        });
  }

  private static void setIfPathExists(Consumer<File> setter, Path path) {
    File file = path.toFile();
    if (file.exists()) {
      setter.accept(file);
    }
  }
}
