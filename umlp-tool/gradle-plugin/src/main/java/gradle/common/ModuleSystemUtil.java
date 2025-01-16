package gradle.common;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Internal;
import umlp.module_system.ModuleMetadata;
import umlp.module_system.TaskModuleMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModuleSystemUtil {


  @Internal
  protected static List<ModuleMetadata> getModuleMetadataList(ConfigurableFileCollection modules){
    List<ModuleMetadata> res = new ArrayList<>();

    for (File file : modules.getFiles()) {
      var opt = readModuleMetadata(file); //The file might be missing (as in empty jars, only describing dependencies)
      opt.ifPresent(s -> res.add(ModuleMetadata.fromJson(s)));
    }
    return res;
  }

  protected static Optional<String> readModuleMetadata(File file) {
    try(var zf = new ZipFile(file)){
      Enumeration<? extends ZipEntry> entries = zf.entries();
      ZipEntry entry = null;
      boolean found = false;
      while(entries.hasMoreElements() && (entry = entries.nextElement()) != null){
        if(entry.getName().matches("[^/]+[.]metadata[.](json)")){
          found = true;
          break;
        }
      }
      if(entry != null && found){
        try(var is = zf.getInputStream(entry)){
          return Optional.of(IOUtils.toString(is, StandardCharsets.UTF_8));
        }
      }
    }catch (IOException e){
      throw new RuntimeException("Error loading metadata of module " + file, e);
    }

    Log.warn("Can not find metadata of module " + file);
    return Optional.empty();
  }

  @Internal
  protected static List<TaskModuleMetadata> getMetadataForThisTask(Task task, ConfigurableFileCollection modules, String taskType){
    List<TaskModuleMetadata> res = new ArrayList<>();

    for (ModuleMetadata moduleMetadata : getModuleMetadataList(modules)) {
      for (TaskModuleMetadata taskModuleMetadata : moduleMetadata.getTasks()) {
        if(taskModuleMetadata.getType().equals(taskType)){
          task.getLogger().info("Adding module " + moduleMetadata.getName(), task.getName());
          res.add(taskModuleMetadata);
        }
      }
    }
    return res;
  }


}
