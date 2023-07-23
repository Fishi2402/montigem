/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.Incremental;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@CacheableTask
public abstract class UMLPBackendTask extends UMLPSingleFileTask {

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

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUseSpring();


  public UMLPBackendTask() {
    super();

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

    if(this.getUseSpring().get()){
      result.add("-" + UMLPToolOptions.USE_SPRING.getCMD());
    }

    return result;
  }


  @Internal
  @Override
  public Set<Provider<? extends FileSystemLocation>> getOtherInputDirectories(){
    Set<Provider<? extends FileSystemLocation>> result = new HashSet<>(super.getOtherInputDirectories());
    result.add(getModelDir());
    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".cd");
  }
}
