/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.se_rwth.commons.logging.Log;
import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.Incremental;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@CacheableTask
@Deprecated // See output from prepareRun();
public abstract class UMLPTask extends UMLPSingleFileTask {

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental  // This Annotation requires custom java-logic to run correctly. See "getOtherInputDirectories()"
  public abstract DirectoryProperty getModelDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // No @Incremental, changes of the Symbols lead to full rebuild
  public abstract DirectoryProperty getSymbolDir();

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  // Do not add "@Incremental" (unless you really know what you are doing)
  public abstract DirectoryProperty getTmplDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<String> getConfigTemplate();


  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getCdToGui();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getForClient();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUseSpring();


  public UMLPTask() {
    super();

    getCdToGui().convention(false);
    getForClient().convention(false);
    getUseSpring().convention(false);
  }


  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    if(this.getSymbolDir().isPresent()) {
      result.add("-path");
      result.add(handlePath.apply(getSymbolDir().getAsFile().get().toPath()));
    }

    if(this.getModelDir().isPresent()) {
      result.add("-modelPath");
      result.add(handlePath.apply(getModelDir().getAsFile().get().toPath()));
    }

    if(this.getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().getAsFile().get().toPath()));
    }

    if(this.getTmplDir().isPresent()) {
      result.add("-fp");
      result.add(handlePath.apply(getTmplDir().getAsFile().get().toPath()));
    }

    if(this.getConfigTemplate().isPresent()){
      result.add("-ct");
      result.add(getConfigTemplate().get());
    }

    if(this.getCdToGui().get()){
      result.add("-" + UMLPToolOptions.CD2GUI.getCMD());
    }

    if(this.getForClient().get()){
      result.add("-" + UMLPToolOptions.FOR_CLIENT.getCMD());
    }

    if(this.getUseSpring().get()){
      result.add("-" + UMLPToolOptions.USE_SPRING.getCMD());
    }

    return result;
  }


  @Internal
  @Override
  protected Set<Provider<? extends FileSystemLocation>> getOtherInputDirectories(){
    Set<Provider<? extends FileSystemLocation>> result = new HashSet<>(super.getOtherInputDirectories());
    result.add(getModelDir());
    return result;
  }

  @Override
  protected boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".gui");
  }

  @Override
  protected void prepareRun(){
    super.prepareRun();
    Log.warn("Using <UMLPTask> is deprecated! The functionality will be removed 20.07.2023!");
  }
}
