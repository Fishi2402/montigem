/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import de.monticore.symboltable.serialization.json.JsonElement;
import gradle.stat.GradleTaskStatistic;
import gradle.stat.TaskStatData;
import org.gradle.api.tasks.Internal;

import java.util.List;
import java.util.function.Consumer;

abstract public class UMLPAllFilesTask extends MCAllFilesTask implements GradleTaskStatistic {
  protected final TaskStatData statdata = new TaskStatData("AllFiles");

  public UMLPAllFilesTask(String type) {
    super(type, null);
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return UMLPToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return UMLPAction.class;
  }


  @Override
  protected void startGeneration(List<String> args, String progressName) {
    statdata.countRun();
    super.startGeneration(args, progressName);
  }
  @Override
  public void prepareRun(){
    super.prepareRun();
    statdata.setNumberOfInputFiles(this.getInputFilesAsStream().count());
  }
  @Override
  @Internal
  public JsonElement getGradleStatisticData() {
    return statdata.getData();
  }
}
