/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import umlp.generator.gui.JSweetConfigGenerator;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static umlp.UMLPToolOptions.GUI_GEN_JSWEET;

// Task might not be a @CacheableTask since relative paths are generated for HWC-files
public abstract class UMLPJava2TypeScriptAdditionalsTask extends UMLPSingleFileTask {

  @OutputDirectory
  public abstract DirectoryProperty getJsweetConfigOut();

  // TODO MH remove if unnecessary
  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  public UMLPJava2TypeScriptAdditionalsTask() {
    super("UMLPJava2TypeScriptConfigTask", null);
    getJsweetConfigOut().convention(getProject().getLayout().getBuildDirectory().dir("jsweet/gen/"));
  }

  @InputFiles
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @Incremental
  // This Annotation requires custom java-logic to run correctly. See "getStreamOfChanges()"
  public abstract ConfigurableFileCollection getModelDir();


  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);
    result.add("-" + GUI_GEN_JSWEET.getCMD());
    result.add("true");

    if (this.getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().getAsFile().get().toPath()));
    }
    if(!this.getModelDir().isEmpty()) {
      for (File file : this.getModelDir()) {
        result.add("-mp");
        result.add(handlePath.apply(file.toPath()));
      }
    }

    System.out.println("Symbol path is " + this.getSymbolPath().getFiles());
    if(!this.getSymbolPath().isEmpty()) {
      for (File file : this.getSymbolPath()) {
        result.add("-path");
        result.add(handlePath.apply(file.toPath()));
      }
    }

    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp");
  }

  @Override
  protected Stream<FileChange> getStreamOfChanges(InputChanges inputChanges) {
    Stream<FileChange> result = super.getStreamOfChanges(inputChanges);
    result = Stream.concat(result, StreamSupport.stream(inputChanges.getFileChanges(getModelDir()).spliterator(), false));
    return result;
  }
}
