package de.montigem.gradle;

import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

/**
 *
 */
public class BackendTestPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(BackendPlugin.class);
    BackendPlugin backendPlugin = project.getPlugins().getPlugin(BackendPlugin.class);

    UMLP2JavaTask firstTask = (UMLP2JavaTask) project.getTasks().getByName(BackendPlugin.UMLP_BACKEND_TASK_NAME);
    // UMLP2JavaTask secondTask = BackendPlugin.createUMLPBackendTask(project, "COMPARE_" + BackendPlugin.UMLP_BACKEND_TASK_NAME).get();
    UMLP2JavaTask secondTask = project.getTasks().register("COMPARE_" + BackendPlugin.UMLP_BACKEND_TASK_NAME, UMLP2JavaTask.class, umlpTask -> {
      // TODO: Is there a better way to copy _all_ properties from the tasks?
      umlpTask.getInput().setFrom(firstTask.getInput());
      umlpTask.getHandWrittenCodeDir().setFrom(firstTask.getHandWrittenCodeDir());
      umlpTask.getSymbolDir().set(firstTask.getSymbolDir());
      umlpTask.getSymbolPath().from(firstTask.getSymbolPath());
      umlpTask.getUseSpring().set(firstTask.getUseSpring());
      umlpTask.getUseShell().set(firstTask.getUseShell());
      umlpTask.getModelDir().setFrom(firstTask.getModelDir());

      // Change Outputs of the task... because we want to generate into a different directory & compare afterwars
      umlpTask.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test"));
      umlpTask.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test_symbols"));
    }).get();

    TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiy");
    compareTask.get().dependsOn(firstTask, secondTask);

    TaskProvider<CheckDirEqualityTask> compareSymbolsTask = createCompareTask(project, firstTask.getSymbolOutDir(), secondTask.getSymbolOutDir(), "checkSymbolEqualitiy");
    compareSymbolsTask.get().dependsOn(firstTask, secondTask);

    project.getTasks().getByName("build").dependsOn(compareTask); // TODO: also add "compareSymbolsTask"
  }

  private TaskProvider<CheckDirEqualityTask> createCompareTask(Project project, DirectoryProperty dir1, DirectoryProperty dir2, String taskName){
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir2().set(dir2);
    });
  }
}
