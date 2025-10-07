/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import de.monticore.symboltable.serialization.json.JsonElement;
import gradle.UMLPPluginBase;
import gradle.stat.GradleTaskStatistic;
import gradle.stat.TaskStatData;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import umlp.module_system.TaskModuleMetadata;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract public class UMLPAllFilesTask extends MCAllFilesTask implements GradleTaskStatistic {
  protected final TaskStatData statdata = new TaskStatData("AllFiles");

  @InputFiles     // Not Incremental. Any change should trigger full rebuild
  @PathSensitive(PathSensitivity.NAME_ONLY)
  @Optional
  public abstract ConfigurableFileCollection getModules();

  @Input
  @Optional
  public abstract ListProperty<String> getModuleConfigTemplates();

  public UMLPAllFilesTask(String type) {
    super(type, null);

    getModules().from(getProject().getConfigurations().getByName(UMLPPluginBase.UMLP_MODULES_GEN_CONFIG));

    getModuleConfigTemplates().convention(getProject().provider(() -> {
      List<String> res = new ArrayList<>();
      for (TaskModuleMetadata taskMetadata : ModuleSystemUtil.getMetadataForThisTask(this, getModules(), type)) {
        res.addAll(taskMetadata.getAdditionalConfigs());
      }
      return res;
    }));

    getExtraClasspathElements().from(getModules());
  }

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);
    if(getModuleConfigTemplates().isPresent()){
      for (String moduleConfigTemplate : getModuleConfigTemplates().get()) {
        result.add("--module-config");
        result.add(moduleConfigTemplate);
      }
    }
    return result;
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
