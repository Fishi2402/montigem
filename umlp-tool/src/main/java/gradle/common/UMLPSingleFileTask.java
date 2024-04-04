package gradle.common;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCSingleFileTask;
import gradle.UMLPPluginBase;
import org.apache.commons.io.IOUtils;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import umlp.module_system.ModuleMetadata;
import umlp.module_system.TaskModuleMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class UMLPSingleFileTask extends MCSingleFileTask {

  @InputFiles     // Not Incremental. Any change should trigger full rebuild
  @PathSensitive(PathSensitivity.NAME_ONLY)
  @Optional
  public abstract ConfigurableFileCollection getModules();

  @Input
  @Optional
  public abstract ListProperty<String> getModuleConfigTemplates();

  public UMLPSingleFileTask(String type, String symbolPathConfigurationName) {
    super(type, symbolPathConfigurationName);

    getModules().from(getProject().getConfigurations().getByName(UMLPPluginBase.UMLP_MODULES_CONFIG));

    getModuleConfigTemplates().convention(getProject().provider(() -> {
      List<String> res = new ArrayList<>();
      for (TaskModuleMetadata taskMetadata : getMetadataForThisTask()) {
        res.addAll(taskMetadata.getAdditionalConfigs());
      }
      return res;
    }));

    getExtraClasspathElements().from(getModules());
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);
    if(getModuleConfigTemplates().isPresent()){
      for (String moduleConfigTemplate : getModuleConfigTemplates().get()) {
        result.add("--module-config");
        result.add(moduleConfigTemplate);
      }
    }

    return result;
  }


  @Internal
  protected List<ModuleMetadata> getModuleMetadataList(){
    List<ModuleMetadata> res = new ArrayList<>();

    for (File file : getModules().getFiles()) {
      res.add(ModuleMetadata.fromJson(readModuleMetadata(file)));
    }
    return res;
  }

  protected String readModuleMetadata(File file) {
    try(var zf = new ZipFile(file)){
      Enumeration<? extends ZipEntry> entries = zf.entries();
      ZipEntry entry;
      while((entry = entries.nextElement()) != null){
        if(entry.getName().matches("[^/]+[.]metadata[.](json)")){
          break;
        }
      }
      if(entry != null){
        try(var is = zf.getInputStream(entry)){
          return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
      }
    }catch (IOException e){
      throw new RuntimeException("Error loading metadata of module " + file, e);
    }

    throw new IllegalStateException("Can not find metadata of module "  + file);
  }

  @Internal
  protected List<TaskModuleMetadata> getMetadataForThisTask(){
    List<TaskModuleMetadata> res = new ArrayList<>();

    for (ModuleMetadata moduleMetadata : getModuleMetadataList()) {
      for (TaskModuleMetadata taskModuleMetadata : moduleMetadata.getTasks()) {
        if(taskModuleMetadata.getType().equals(type)){
          getLogger().info("Adding module " + moduleMetadata.getName(), this.getName());
          res.add(taskModuleMetadata);
        }
      }
    }
    return res;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return UMLPToolInvoker::run;
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return UMLPAction.class;
  }

}
