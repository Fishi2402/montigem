package gradle;

import gradle.common.CheckDirEqualityTask;
import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPFrontendTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

public class UMLPPluginFrontendTest implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(UMLPPluginFrontend.class);
    UMLPPluginFrontend frontendPlugin = project.getPlugins().getPlugin(UMLPPluginFrontend.class);

    UMLPFrontendTask firstTask = (UMLPFrontendTask) project.getTasks().getByName(UMLPPluginFrontend.UMLP_FRONTEND_DOMAIN_TASK_NAME);
    // UMLPBackendTask secondTask = UMLPPluginBackend.createUMLPBackendTask(project, "COMPARE_" + UMLPPluginBackend.UMLP_BACKEND_TASK_NAME).get();
    UMLPFrontendTask secondTask = project.getTasks().register("COMPARE_" + UMLPPluginFrontend.UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      // TODO: Is there a better way to copy _all_ properties from the tasks?
      umlpTask.getInput().from(firstTask.getInput());
      umlpTask.getHwcDir().set(firstTask.getHwcDir());

      // Change Outputs of the task... because we want to generate into a different directory & compare afterwars
      umlpTask.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_test"));
      umlpTask.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_test_symbols"));
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
