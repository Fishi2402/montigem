package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 *
 */
public class BackendTestPlugin implements Plugin<Project> {

  public static final String CLONE_UMLP2JAVA_TASK = "clone_umlp2java";
  public static final String COMPARE_UMLP2JAVA_TASK = "compare_umlp2java";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BackendPlugin.class);

    TaskProvider<UMLP2JavaTask> firstTask = project.getTasks().named(BackendPlugin.UMLP2JAVA_TASK, UMLP2JavaTask.class);
    TaskProvider<UMLP2JavaTask> secondTask = project.getTasks().register(CLONE_UMLP2JAVA_TASK, UMLP2JavaTask.class, task -> {
      task.getInput().from(firstTask.map(CommonMCTask::getInput));
      task.getHandWrittenCodeDir().from(firstTask.map(CommonMCTask::getHandWrittenCodeDir));
      task.getSymbolPath().from(firstTask.map(CommonMCTask::getSymbolPath));
      task.getUseSpring().set(firstTask.flatMap(UMLP2JavaTask::getUseSpring));
      task.getUseShell().set(firstTask.flatMap(UMLP2JavaTask::getUseShell));
      task.getModelDir().from(firstTask.map(UMLP2JavaTask::getModelDir));

      // Outputs of the task to generate into a different directory & compare after
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("clone_umlp2java"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("clone_umlp2sym"));
    });

    TaskProvider<CheckDirEqualityTask> compareTask = project.getTasks().register(COMPARE_UMLP2JAVA_TASK, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(firstTask.flatMap(CommonMCTask::getOutputDir));
      task.getDir2().set(secondTask.flatMap(CommonMCTask::getOutputDir));
    });

    project.getTasks().getByName("build").dependsOn(compareTask);
  }

}
