package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPFrontendTask extends UMLPSingleFileTask {

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @Input
  @org.gradle.api.tasks.Optional
  public abstract Property<Boolean> getGenerateSymbolsOnly();

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    if (!getGenerateSymbolsOnly().isPresent() || !getGenerateSymbolsOnly().get()) {
      result.add("-" + UMLPToolOptions.FOR_CLIENT.getCMD());
    } else {
      result.add("-" + UMLPToolOptions.NO_CODE_GENERATION.getCMD());
    }

    if (this.getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().getAsFile().get().toPath()));
    }

    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp");
  }
}
