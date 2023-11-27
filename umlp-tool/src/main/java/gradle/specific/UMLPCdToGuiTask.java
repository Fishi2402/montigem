/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPSingleFileTask;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class UMLPCdToGuiTask extends UMLPSingleFileTask {
  public UMLPCdToGuiTask() {
    super("UMLPCdToGuiTask");
  }

  /**
   * @param filePath Path of InputFile
   * @return Arguments that can be used to start CLI-Tool
   */
  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    result.add("-" + UMLPToolOptions.CD2GUI.getCMD());

    return result;
  }

  @Override
  public boolean isInputFile(File f) {
    return f.getName().endsWith(".umlp");
  }
}
