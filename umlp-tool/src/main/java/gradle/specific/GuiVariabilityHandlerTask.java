package gradle.specific;

import de.se_rwth.commons.logging.Log;
import gradle.common.CommonUMLPTask;
import gradle.common.UMLPAllFilesTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

abstract public class GuiVariabilityHandlerTask extends UMLPAllFilesTask {

  @Override
  public boolean isInputFile(File f){
    return f.getName().endsWith(".gui");
  }

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    /* is added by superklasse
    result.add("-i");
    result.add(".\\."); //placeholder, never used
    */

    result.add("-guidelta");

    result.add("-gp");
    this.getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(this::isInputFile)
        .forEach(f -> result.add(handlePath.apply(f.toPath())));

    return result;
  }
}
