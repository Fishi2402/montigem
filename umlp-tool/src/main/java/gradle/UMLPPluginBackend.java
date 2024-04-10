/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.UMLPBackendTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/***
 * A opinionated plugin which handles backend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-backend'" in build.gradle
 * a UMLPTask is created and output is added to java sourceset.
 */
public abstract class UMLPPluginBackend implements Plugin<Project> {

  final static String UMLP_BACKEND_TASK_NAME = "generateUmlpBackend";
  static final String UMLP_GROUP_NAME = "umlp";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("java");

    TaskProvider<UMLPBackendTask> umlpTask = createUMLPBackendTask(project, UMLP_BACKEND_TASK_NAME);
    // Generate Backend must run before JavaCompile
    project.getTasks().withType(AbstractCompile.class).configureEach(compile -> {
      compile.dependsOn(umlpTask);
    });
    // Generate Backend must run before ProcessResources (e.g. for SQL schemata)
    project.getTasks().withType(ProcessResources.class).configureEach(compile -> {
      compile.dependsOn(umlpTask);
    });

    addToSourceSet(project, umlpTask.get());

    // Lifecycle task "generate" which depends on Backend-Creation
    Task generateTask = project.getTasks().create("generate");
    generateTask.dependsOn(umlpTask);

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    Task continuousTask = project.getTasks().create("continuous");
    continuousTask.dependsOn(project.getTasks().withType(Jar.class));

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    // TODO: Use constant for jsweet-core
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");
  }

  // Add generated files to SourceSets
  private void addToSourceSet(Project project, UMLPBackendTask umlpTask) {
    JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
    SourceSet main = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(umlpTask.getOutputDir());

    // Add generated resources. e.g. SQL-Schemata
    main.getResources().srcDir(umlpTask.getOutputDir().dir("resources"));
  }

  protected static TaskProvider<UMLPBackendTask> createUMLPBackendTask(final Project project, String umlpBackendTaskName) {
    final TaskProvider<UMLPBackendTask> result = project.getTasks().register(umlpBackendTaskName, UMLPBackendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      setIfPathExists(umlpTask::setInput, Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "models"));
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
      setIfPathExists(x -> umlpTask.getTmplDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "templates"));

      // Always set output dir (Gradle creates it, if not already exists)
      umlpTask.getOutputDir().set(Path.of(
          project.getBuildDir().getAbsolutePath(), "umlp", "backend").toFile());

      // Always create symbols
      umlpTask.getSymbolOutDir().set(Path.of(
          project.getBuildDir().getAbsolutePath(), "umlp", "backend_symbols").toFile());

    });
    return result;
  }

  private static void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
