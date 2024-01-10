package gradle;

import gradle.common.CheckDirEqualityTask;
import gradle.specific.UMLPBackendTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

public class UMLPPluginBackendTest implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(UMLPPluginBackend.class);
    UMLPPluginBackend backendPlugin = project.getPlugins().getPlugin(UMLPPluginBackend.class);

    UMLPBackendTask firstTask = (UMLPBackendTask) project.getTasks().getByName(UMLPPluginBackend.UMLP_BACKEND_TASK_NAME);
    // UMLPBackendTask secondTask = UMLPPluginBackend.createUMLPBackendTask(project, "COMPARE_" + UMLPPluginBackend.UMLP_BACKEND_TASK_NAME).get();
    UMLPBackendTask secondTask = project.getTasks().register("COMPARE_" + UMLPPluginBackend.UMLP_BACKEND_TASK_NAME, UMLPBackendTask.class, umlpTask -> {
      // TODO: Is there a better way to copy _all_ properties from the tasks?
      umlpTask.getInput().from(firstTask.getInput());
      umlpTask.getHwcDir().set(firstTask.getHwcDir());
      umlpTask.getSymbolDir().set(firstTask.getSymbolDir());
      umlpTask.getUseSpring().set(firstTask.getUseSpring());
      umlpTask.getUseShell().set(firstTask.getUseShell());

      // Change Outputs of the task... because we want to generate into a different directory & compare afterwars
      umlpTask.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test"));
      umlpTask.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test_symbols"));
    }).get();

    TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiy");
    compareTask.get().dependsOn(firstTask, secondTask);
    project.getTasks().getByName("build").dependsOn(compareTask);
  }

  private TaskProvider<CheckDirEqualityTask> createCompareTask(Project project, DirectoryProperty dir1, DirectoryProperty dir2, String taskName){
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir2().set(dir2);
    });
  }
}
