/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class UMLPBackendTask extends UMLPSingleFileTask {

  @InputFiles
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental  // This Annotation requires custom java-logic to run correctly. See "getOtherInputDirectories()"
  public abstract ConfigurableFileCollection getModelDir();

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

    if(!this.getModelDir().isEmpty()) {
      for (File file : this.getModelDir()) {
        result.add("-modelPath");
        result.add(handlePath.apply(file.toPath()));
      }
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

  @Override
  protected Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = StreamSupport.stream(inputChanges.getFileChanges(getInput()).spliterator(), false);
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getModelDir()).spliterator(), false));
    return result;
  }

  @Internal
  @Override
  public Set<Provider<? extends FileSystemLocation>> getOtherInputDirectories(){
    throw new IllegalStateException("never called");
    /*Set<Provider<? extends FileSystemLocation>> result = new HashSet<>(super.getOtherInputDirectories());
    for (File file : getModelDir()) {
      // TODO: not lazy enough, all providers get resolved at config time
      result.add(getProject().getLayout().dir(getProject().provider(() -> file)));
    }
    return result;*/
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".cd");
  }
}
