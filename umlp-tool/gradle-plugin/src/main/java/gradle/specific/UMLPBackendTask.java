/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.UMLPToolOptions;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@CacheableTask
public abstract class UMLPBackendTask extends UMLPSingleFileTask {

  @InputFiles
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental  // This Annotation requires custom java-logic to run correctly. See "getStreamOfChanges()"
  public abstract ConfigurableFileCollection getModelDir();

  @Deprecated() // MCSingleFileTask already defines the symbolPath Property
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

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUseShell();

  /**
   * @deprecated in favor of {@link UMLPTestInfrastructureTask#getUsePlaywright()}
   */
  @Deprecated
  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getUsePlaywright();

  @Inject
  public UMLPBackendTask() {
    this("UMLPBackendTask");
  }

  public UMLPBackendTask(String type) {
    super(type, null);

    getUseSpring().convention(false);
    getUseShell().convention(false);
    getUsePlaywright().convention(false);

    getModelDir().from(getModules());
  }

  ConfigurableFileCollection ocl = getProject().getObjects().fileCollection();

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  @Incremental  // This Annotation requires custom java-logic to run correctly. See "getStreamOfChanges()"
  public ConfigurableFileCollection getOcl() {
    return ocl;
  }

  public void setOcl(File f){
    ocl.from(f);
  }

  /**
   * Adds additional arguments for calling the UMLPTool#run method.
   * To allow parallel execution using a WorkQueue, {@link this#getToolAction()} is used to
   * invoke the UMLPTool
   *
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    // Remove when we remove symbolDir
    if(this.getSymbolDir().isPresent()) {
      result.add("-path");
      result.add(handlePath.apply(getSymbolDir().getAsFile().get().toPath()));
    }

    if (!this.getSymbolPath().isEmpty()) {
      for (var symDir : this.getSymbolPath()) {
        result.add("-path");
        result.add(symDir.getAbsolutePath());
      }
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

    if(!this.getOcl().isEmpty()) {
      result.add("-ocl");
      result.add(handlePath.apply(Path.of(getOcl().getAsPath())));
    }

    if(this.getUseSpring().get()){
      result.add("-" + UMLPToolOptions.USE_SPRING.getCMD());
    }

    if(this.getUseShell().get()){
      result.add("-" + UMLPToolOptions.USE_SHELL.getCMD());
    }

    return result;
  }

  @Override
  protected Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = super.getStreamOfChanges(inputChanges);
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getModelDir()).spliterator(), false));
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getOcl()).spliterator(), false));
    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp") || f.getName().endsWith(".cd");
  }
}
