/* (c) https://github.com/MontiCore/monticore */
package gradle.specific.cd;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCSingleFileTask;
import gradle.common.UMLPAction;
import gradle.common.UMLPSingleFileTask;
import gradle.common.UMLPToolInvoker;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
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

  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".cd");
  }

  @Override
  protected List<String> createArgList(Path filePath, Function<Path, String> handlePath) {
    List<String> result = super.createArgList(filePath, handlePath);

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
