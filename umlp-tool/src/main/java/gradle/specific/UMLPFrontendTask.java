package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPFrontendTask extends UMLPSingleFileTask {
  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    result.add("-" + UMLPToolOptions.FOR_CLIENT.getCMD());

    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp");
  }
}
