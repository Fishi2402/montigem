/* (c) https://github.com/MontiCore/monticore */
package gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.nio.file.Path;

/***
 * A opinionated plugin which handles backend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-backend'" in build.gradle
 * a UMLPTask is created and output is added to java sourceset.
 */
public abstract class UMLPPluginBackend implements Plugin<Project> {

  final String UMLP_BACKEND_TASK_NAME = "generateUmlpBackend";
  final String UMLP_GROUP_NAME = "umlp";

    @Override
    public void apply(Project project) {
      project.getPluginManager().apply(UMLPPluginBase.class);
      project.getPluginManager().apply("java");

      TaskProvider<UMLPTask> umlpTask = createUMLPBackendTask(project);
      project.getTasks().withType(AbstractCompile.class).configureEach(compile -> {
        compile.dependsOn(umlpTask);
      });
      project.getTasks().withType(ProcessResources.class).configureEach(compile -> {
        compile.dependsOn(umlpTask);
      });
      addToSourceSet(project, umlpTask.get());
    }

    // Add generated files to SourceSets
  private void addToSourceSet(Project project, UMLPTask umlpTask) {
    JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
    SourceSet main = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(umlpTask.getOutputDir());
    main.getResources().srcDir(umlpTask.getOutputDir());
  }

  protected TaskProvider<UMLPTask> createUMLPBackendTask(final Project project) {
      final TaskProvider<UMLPTask> result = project.getTasks().register(UMLP_BACKEND_TASK_NAME, UMLPTask.class, umlpTask -> {
        umlpTask.setGroup(UMLP_GROUP_NAME);
        umlpTask.setInput(Path.of(
            project.getProjectDir().getAbsolutePath(), "src", "models").toFile()
        );
        umlpTask.getOutputDir().set(Path.of(
            project.getBuildDir().getAbsolutePath(), "umlp", "backend").toFile()
        );
        umlpTask.getHwcDir().set(Path.of(
            project.getProjectDir().getAbsolutePath(), "src", "main", "java").toFile()
        );
        umlpTask.getTmplDir().set(Path.of(
            project.getProjectDir().getAbsolutePath(), "src", "templates").toFile()
        );
      });
      return result;
    }
}
