package gradle.inputset;

import de.se_rwth.commons.logging.Log;
import gradle.CommonUMLPTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.InputChanges;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

abstract public class GuiVariabilityHandlerTask extends CommonUMLPTask {

  protected List<String> createArgList(Function<Path, String> handlePath) {
    List<String> result = super.createArgList(handlePath);

    result.add("-r");
    result.add(handlePath.apply(getReportDir().getAsFile().get().toPath()));

    result.add("-guidelta");

    this.getInput().getAsFileTree().getFiles().stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".gui"))
        .forEach(f -> {
          result.add("-i");
          result.add(handlePath.apply(f.toPath()));
        });

    return result;
  }

  @TaskAction
  public void run(InputChanges inputChanges) {
    Log.warn("This Task is still in development and experimental, no incremental logic exists!");

    workQueue = getWorkerExecutor().classLoaderIsolation();
    Path cwd = getProject().getProjectDir().toPath().toAbsolutePath();
    Log.warn("Starting UMLPTool: \n" +
        "\t  java -jar UMLPTool.jar " +
        String.join(" ", createArgList(p -> "." + File.separator + cwd.relativize(p)))
    );

    List<String> args = createArgList(p -> p.toAbsolutePath().toString());
    super.startGeneration(args);
  }
}
