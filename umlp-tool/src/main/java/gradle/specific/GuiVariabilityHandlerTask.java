/* (c) https://github.com/MontiCore/monticore */
package gradle.specific;

import gradle.common.UMLPAllFilesv2Task;
import umlp.UMLPToolOptions;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

abstract public class GuiVariabilityHandlerTask extends UMLPAllFilesv2Task {

  public GuiVariabilityHandlerTask() {
    super("GuiVariabilityHandlerTask");
  }

  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".gui");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    result.add("-" + UMLPToolOptions.GUIVariability.getCMD());

    return result;
  }
}
