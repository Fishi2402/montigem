package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

import static de.montigem.gradle.FrontendBasePlugin.createGUI2JavaTask;

public class FrontendTestPlugin implements Plugin<Project> {

  public static final String COMPARE_GUI2JAVA_TASK = "compare_gui2java";

  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(FrontendPlugin.class);

    // Frontend GUI Tasks:
    {
      TaskProvider<GUI2JavaTask> firstTask = project.getTasks().named(FrontendBasePlugin.GUI2JAVA_TASK, GUI2JavaTask.class);
      TaskProvider<GUI2JavaTask> secondTask = createGUI2JavaTask(project);
      /*
          project.getTasks().register(COMPARE_GUI2JAVA_TASK, GUI2JavaTask.class, umlpTask -> {
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
      /**/
      secondTask.configure(task -> {
        // Change Outputs of the task to compare with generated files of first task
        task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("compare_gui2java"));
        task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("compare_gui2java"));
      });

      TaskProvider<CheckDirEqualityTask> compareGUI2Java = project.getTasks().register(COMPARE_GUI2JAVA_TASK, CheckDirEqualityTask.class, task -> {
        task.getDir1().set(firstTask.flatMap(CommonMCTask::getOutputDir));
        task.getDir2().set(secondTask.flatMap(CommonMCTask::getOutputDir));
      });

      // TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiyofGUI");

      project.getTasks().named("build").configure(t -> t.dependsOn(compareGUI2Java));
    }
  }

}
