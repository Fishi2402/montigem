package de.montigem.gradle;

import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.UMLP2TSTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

public class FrontendTestPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(FrontendPlugin.class);
    FrontendPlugin frontendPlugin = project.getPlugins().getPlugin(FrontendPlugin.class);

    // Frontend Domain Tasks:
    {
      UMLP2TSTask firstTask = (UMLP2TSTask) project.getTasks().getByName(FrontendBasePlugin.UMLP_FRONTEND_DOMAIN_TASK_NAME);
      UMLP2TSTask secondTask = project.getTasks().register("COMPARE_" + FrontendBasePlugin.UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLP2TSTask.class, umlpTask -> {
        // TODO: Is there a better way to copy _all_ properties from the tasks?
        umlpTask.getInput().setFrom(firstTask.getInput());
        umlpTask.getHandWrittenCodeDir().setFrom(firstTask.getHandWrittenCodeDir());
        umlpTask.getSymbolPath().setFrom(firstTask.getSymbolPath());
        umlpTask.getModelDir().setFrom(firstTask.getModelDir());

        // Change Outputs of the task... because we want to generate into a different directory & compare afterwars
        umlpTask.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_domain_test"));
        umlpTask.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_domain_test_symbols"));
      }).get();

      TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiyofDomain");
      compareTask.get().dependsOn(firstTask, secondTask);

      project.getTasks().getByName("build").dependsOn(compareTask);
    }

    // Frontend GUI Tasks:
    {
      GUI2JavaTask firstTask = (GUI2JavaTask) project.getTasks().getByName(FrontendBasePlugin.UMLP_FRONTEND_GUI_TASK_NAME);
      GUI2JavaTask secondTask = project.getTasks().register("COMPARE_" + FrontendBasePlugin.UMLP_FRONTEND_GUI_TASK_NAME, GUI2JavaTask.class, umlpTask -> {
        // TODO: Is there a better way to copy _all_ properties from the tasks?
        umlpTask.getInput().from(firstTask.getInput());
        umlpTask.getHandWrittenCodeDir().from(firstTask.getHandWrittenCodeDir());
        umlpTask.getModelDir().set(firstTask.getModelDir());
        umlpTask.getSymbolDir().from(firstTask.getSymbolDir());
        umlpTask.getDomainModels().set(firstTask.getDomainModels());
        umlpTask.getGuiCreateModels().set(firstTask.getGuiCreateModels());

        // Change Outputs of the task... because we want to generate into a different directory & compare afterwards
        umlpTask.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_gui_test/right/dirdepth/"));
        umlpTask.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_frontend_gui_test_symbols"));
      }).get();

      TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiyofGUI");
      compareTask.get().dependsOn(firstTask, secondTask);

      // project.getTasks().getByName("build").dependsOn(compareTask);  // TODO: comment in!
    }
  }

  private TaskProvider<CheckDirEqualityTask> createCompareTask(Project project, DirectoryProperty dir1, DirectoryProperty dir2, String taskName) {
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir2().set(dir2);
    });
  }
}
