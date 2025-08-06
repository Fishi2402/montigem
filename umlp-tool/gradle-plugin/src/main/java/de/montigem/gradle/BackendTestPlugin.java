package de.montigem.gradle;

import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.TaskProvider;

import static de.montigem.gradle.BackendPlugin.createUMLP2JavaTask;

/**
 *
 */
public class BackendTestPlugin implements Plugin<Project> {

  public static final String COMPARE_UMLP2JAVA_TASK = "compare_umlp2java";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BackendPlugin.class);

    TaskProvider<UMLP2JavaTask> firstTask = project.getTasks().named(BackendPlugin.UMLP2JAVA_TASK, UMLP2JavaTask.class);
    TaskProvider<UMLP2JavaTask> secondTask = createUMLP2JavaTask(project);
    secondTask.configure(task -> {
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test_symbols"));
    });
    /*
    project.getTasks().register(COMPARE_UMLP2JAVA_TASK, UMLP2JavaTask.class, task -> {
      // TODO: Is there a better way to copy _all_ properties from the tasks?
      task.getInput().setFrom(firstTask.getInput());
      task.getHandWrittenCodeDir().setFrom(firstTask.getHandWrittenCodeDir());
      task.getSymbolDir().set(firstTask.getSymbolDir());
      task.getSymbolPath().from(firstTask.getSymbolPath());
      task.getUseSpring().set(firstTask.getUseSpring());
      task.getUseShell().set(firstTask.getUseShell());
      task.getModelDir().setFrom(firstTask.getModelDir());

      // Change Outputs of the task... because we want to generate into a different directory & compare afterwars
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp_backend_test_symbols"));
    }).get();
    /**/

    /*
    TaskProvider<CheckDirEqualityTask> compareTask = createCompareTask(project, firstTask.getOutputDir(), secondTask.getOutputDir(), "checkEqualitiy");
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir2().set(dir2);
    });

    TaskProvider<CheckDirEqualityTask> compareSymbolsTask = createCompareTask(project, firstTask.getSymbolOutDir(), secondTask.getSymbolOutDir(), "checkSymbolEqualitiy");
    compareSymbolsTask.get().dependsOn(firstTask, secondTask);

    project.getTasks().getByName("build").dependsOn(compareTask); // TODO: also add "compareSymbolsTask"
    /**/
  }

  private TaskProvider<CheckDirEqualityTask> createCompareTask(Project project, DirectoryProperty dir1, DirectoryProperty dir2, String taskName) {
    return project.getTasks().register(taskName, CheckDirEqualityTask.class, task -> {
      task.getDir1().set(dir1);
      task.getDir2().set(dir2);
    });
  }
}
