/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.cd;

import gradle.common.UMLPSingleFileTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class GUITypes2JavaTask extends UMLPSingleFileTask {

  public GUITypes2JavaTask() {
    super("GUITypes2JavaTask", null);
  }

  @InputDirectory
  @org.gradle.api.tasks.Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  // No @Incremental, changes of the Symbols lead to full rebuild
  public abstract DirectoryProperty getTemplatePath();

  @OutputDirectory
  @org.gradle.api.tasks.Optional
  public abstract DirectoryProperty getSymbolOutDir();

  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".cd");
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

    if (this.getSymbolOutDir().isPresent()) {
      result.add("-s");
      result.add(handlePath.apply(getSymbolOutDir().getAsFile().get().toPath()));
    }
    {
      result.add("--input");
      result.add(handlePath.apply(filePath));
    }
    {
      result.add("--out");
      result.add(handlePath.apply(getOutputDir().get().getAsFile().toPath()));
    }
    if (!getHandWrittenCodeDir().isEmpty()) {
      result.add("-hwc");
      result.add(handlePath.apply(getHandWrittenCodeDir().getSingleFile().toPath()));
    }
    if (getTemplatePath().isPresent()) {
      result.add("--templatePath");
      result.add(handlePath.apply(getTemplatePath().get().getAsFile().toPath()));
    }

    result.add("-rte");

    return result;
  }
}
