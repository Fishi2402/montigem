package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class FrontendTestPlugin implements Plugin<Project> {

  public static final String CLONE_GUI2JAVA_TASK = "clone_gui2java";
  public static final String COMPARE_GUI2JAVA_TASK = "compare_gui2java";

  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(FrontendPlugin.class);

    // Frontend GUI Tasks:
    TaskProvider<GUI2JavaTask> firstTask = project.getTasks().named(FrontendBasePlugin.GUI2JAVA_TASK, GUI2JavaTask.class);
    TaskProvider<GUI2JavaTask> secondTask = project.getTasks().register(CLONE_GUI2JAVA_TASK, GUI2JavaTask.class, task -> {
      task.getInput().from(firstTask.map(CommonMCTask::getInput));
      task.getHandWrittenCodeDir().from(firstTask.map(CommonMCTask::getHandWrittenCodeDir));
      task.getModelDir().set(firstTask.flatMap(GUI2JavaTask::getModelDir));
      task.getSymbolPath().from(firstTask.map(GUI2JavaTask::getSymbolPath));
      task.getDomainModels().set(firstTask.flatMap(GUI2JavaTask::getDomainModels));
      task.getGuiCreateModels().set(firstTask.flatMap(GUI2JavaTask::getGuiCreateModels));

      // Outputs of the task to generate into a different directory & compare after
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("intermediate/app/clone_gui2java"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("clone_gui2sym"));
    });

    TaskProvider<CheckDirEqualityTask> compareGUI2Java = project.getTasks().register(COMPARE_GUI2JAVA_TASK, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(firstTask.flatMap(CommonMCTask::getOutputDir));
      task.getDir2().set(secondTask.flatMap(CommonMCTask::getOutputDir));
    });

    project.getTasks().named("build").configure(t -> t.dependsOn(compareGUI2Java));
  }

}
