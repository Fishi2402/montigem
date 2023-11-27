package umlp.module_system;

import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;

public class ModuleMetadata {
  private static final Yaml yaml = new Yaml();
  private String name;
  private List<TaskModuleMetadata> tasks = new ArrayList<>();

  public static ModuleMetadata fromYaml(String content){
    return yaml.loadAs(content, ModuleMetadata.class);
  }

  public ModuleMetadata(){

  }

  public ModuleMetadata(String name, List<TaskModuleMetadata> taskModuleMetadataList) {
    this.name = name;
    this.tasks = taskModuleMetadataList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TaskModuleMetadata> getTasks() {
    return tasks;
  }

  public void setTasks(List<TaskModuleMetadata> taskModuleMetadataList) {
    this.tasks = taskModuleMetadataList;
  }
}
