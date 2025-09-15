package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.specific.GUI2Java2TSTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.UMLPJava2TSTask;
import de.montigem.gradle.specific.serialize.CD2SymTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

import static de.montigem.gradle.FrontendBasePlugin.GUI_TYPES_CONFIG;
import static de.montigem.gradle.FrontendBasePlugin.inRTE;
import static de.montigem.gradle.FrontendWithJSweetPlugin.GUI2JAVA2TS_TASK;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;

public class FrontendTestPlugin implements Plugin<Project> {

  public static final String CLONE_GUI2JAVA2TS_TASK = "clone_gui2java2ts";
  public static final String COMPARE_GUI2JAVA2TS_TASK = "compare_gui2java2ts";

  @Override
  public void apply(Project project) {

    project.getPluginManager().apply(FrontendPlugin.class);

    // Frontend GUI Tasks:
    TaskProvider<GUI2Java2TSTask> firstTask = project.getTasks().named(GUI2JAVA2TS_TASK, GUI2Java2TSTask.class);
    TaskProvider<GUI2Java2TSTask> secondTask = project.getTasks().register(CLONE_GUI2JAVA2TS_TASK, GUI2Java2TSTask.class, task -> {
      task.getImportsDir().from(firstTask.map(GUI2JavaTask::getImportsDir));
      task.getInput().from(firstTask.map(CommonMCTask::getInput));
      task.getHandWrittenCodeDir().from(firstTask.map(CommonMCTask::getHandWrittenCodeDir));
      task.getModelDir().set(firstTask.flatMap(GUI2JavaTask::getModelDir));
      task.getSymbolPath().from(firstTask.map(GUI2JavaTask::getSymbolPath));
      task.getDomainModels().set(firstTask.flatMap(GUI2JavaTask::getDomainModels));
      task.getGuiCreateModels().set(firstTask.flatMap(GUI2JavaTask::getGuiCreateModels));
      task.getGUIGenJSweet().set(firstTask.flatMap(GUI2JavaTask::getGUIGenJSweet));
      task.getGUIGenTSFunctionality().set(firstTask.flatMap(GUI2JavaTask::getGUIGenTSFunctionality));
      task.getDomain().set(firstTask.flatMap(GUI2Java2TSTask::getDomain));
      task.getJSweetAdapterDir().set(firstTask.flatMap(GUI2Java2TSTask::getJSweetAdapterDir));
      task.getHandWrittenJSweetDir().set(firstTask.flatMap(GUI2Java2TSTask::getHandWrittenJSweetDir));
      task.getExtraJava2TSInput().from(firstTask.map(GUI2Java2TSTask::getExtraJava2TSInput));
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
      task.getDir1().set(firstTask.flatMap(CommonMCTask::getOutputDir));
      task.getDir2().set(secondTask.flatMap(CommonMCTask::getOutputDir));
    });

    project.getTasks().named("build").configure(t -> t.dependsOn(compareGUI2Java));
  }

}
