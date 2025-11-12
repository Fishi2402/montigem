package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.GUI2Java2TSTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

import static de.montigem.gradle.FrontendPlugin.GUI2JAVA2TS_TASK;

public class FrontendTestPlugin implements Plugin<Project> {

  public static final String CLONE_GUI2JAVA2TS_TASK = "clone_gui2java2ts";
  public static final String COMPARE_GUI2JAVA2TS_TASK = "compare_gui2java2ts";

  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(FrontendPlugin.class);

    // Frontend GUI Tasks:
    TaskProvider<GUI2Java2TSTask> firstTask = project.getTasks().named(GUI2JAVA2TS_TASK, GUI2Java2TSTask.class);
    TaskProvider<GUI2Java2TSTask> secondTask = project.getTasks().register(CLONE_GUI2JAVA2TS_TASK, GUI2Java2TSTask.class, task -> {
      task.setGroup("verification");
      task.getImportsDir().from(firstTask.map(GUI2JavaTask::getImportsDir));
      task.getInput().from(firstTask.map(CommonMCTask::getInput));
      task.getHandWrittenCodeDir().from(firstTask.map(CommonMCTask::getHandWrittenCodeDir));
      task.getModelDir().from(firstTask.map(GUI2JavaTask::getModelDir));
      task.getSymbolPath().from(firstTask.map(GUI2JavaTask::getSymbolPath));
      task.getDomain().set(firstTask.flatMap(GUI2JavaTask::getDomain));
      task.getGuiCreateModels().set(firstTask.flatMap(GUI2JavaTask::getGuiCreateModels));
      task.getGUIGenJSweet().set(firstTask.flatMap(GUI2JavaTask::getGUIGenJSweet));
      task.getGUIGenTSFunctionality().set(firstTask.flatMap(GUI2JavaTask::getGUIGenTSFunctionality));
      task.getJSweetAdapterDir().set(firstTask.flatMap(GUI2Java2TSTask::getJSweetAdapterDir));
      task.getHandWrittenJSweetDir().set(firstTask.flatMap(GUI2Java2TSTask::getHandWrittenJSweetDir));
      task.getInput4RelativePathCalculation().from(firstTask.map(GUI2Java2TSTask::getInput4RelativePathCalculation));
      task.getJSweetWorkingDir().set(firstTask.flatMap(GUI2Java2TSTask::getJSweetWorkingDir));
      task.getJSweetCandiesDir().set(firstTask.flatMap(GUI2Java2TSTask::getJSweetCandiesDir));
      task.getJSweetClassPath().from(firstTask.map(GUI2Java2TSTask::getJSweetClassPath));
      task.getExcludeTranspileFiles().from(firstTask.map(GUI2Java2TSTask::getExcludeTranspileFiles));
      task.getExcludeSource().set(firstTask.flatMap(GUI2Java2TSTask::getExcludeSource));

      // Outputs of the task to generate into a different directory & compare after
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("intermediate/clone_gui2java2ts"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("clone_gui2sym"));
    });

    TaskProvider<CheckDirEqualityTask> compareGUI2Java = project.getTasks().register(COMPARE_GUI2JAVA2TS_TASK, CheckDirEqualityTask.class, task -> {
      task.setGroup("verification");
      task.getDir1().set(firstTask.flatMap(CommonMCTask::getOutputDir));
      task.getDir2().set(secondTask.flatMap(CommonMCTask::getOutputDir));
    });

    project.getTasks().named("build").configure(t -> t.dependsOn(compareGUI2Java));
  }

}
