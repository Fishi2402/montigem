package gradle;

import gradle.specific.CheckDirEqualityTask;
import gradle.specific.UMLPBackendTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

import java.nio.file.Path;

public class UMLPPluginBackendTest implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(UMLPPluginBackend.class);
    UMLPPluginBackend backendPlugin = project.getPlugins().getPlugin(UMLPPluginBackend.class);

    UMLPBackendTask firstTask = (UMLPBackendTask) project.getTasks().getByName(UMLPPluginBackend.UMLP_BACKEND_TASK_NAME);
    UMLPBackendTask secondTask = UMLPPluginBackend.createUMLPBackendTask(project, "COMPARE_" + backendPlugin.UMLP_BACKEND_TASK_NAME).get();
    // Change Ouputs of the task... because we want to generate into a different directory
    secondTask.getOutputDir().set(Path.of(
        project.getBuildDir().getAbsolutePath(), "umlp_test", "backend").toFile());
    secondTask.getSymbolOutDir().set(Path.of(
        project.getBuildDir().getAbsolutePath(), "umlp_test", "backend_symbols").toFile());

    TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiy");
    project.getTasks().getByName("build").dependsOn(compareTask);
  }

  private TaskProvider<CheckDirEqualityTask> createCompareTask(Project project, DirectoryProperty dir1, DirectoryProperty dir2, String taskName){
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir1().set(dir2);
    });
  }
}
